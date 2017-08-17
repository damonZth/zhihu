package com.jnu.async;


import com.alibaba.fastjson.JSON;
import com.jnu.controller.LikeController;
import com.jnu.service.JedisAdapter;
import com.jnu.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Damon on 2017/8/16.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);

        if(beans != null){
            for(Map.Entry<String, EventHandler> eventHandlerEntry : beans.entrySet()){
                List<EventType> eventTypes = eventHandlerEntry.getValue().getSupportEventTypes();
                System.out.println(eventTypes.toString());
                for(EventType eventType : eventTypes){
                    if(!config.containsKey(eventType)){
                        config.put(eventType, new ArrayList<>());
                    }
                    config.get(eventType).add(eventHandlerEntry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getBizEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);
                    for(String event : events){
                        if(event.equals(key)){
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(event, EventModel.class);
                        if(!config.containsKey(eventModel.getEventType())){
                            logger.error("不能识别的事件" );
                            continue;
                        }

                        for(EventHandler handler : config.get(eventModel.getEventType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
