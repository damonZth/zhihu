package com.jnu.service;

import com.jnu.utils.RedisKeyUtil;
import jdk.internal.org.objectweb.asm.util.TraceAnnotationVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Damon on 2017/8/17.
 */
@Service
public class FollowService {
    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.mulit(jedis);
        System.out.println(1);

        transaction.zadd(followerKey, date.getTime(), String.valueOf(userId));
        transaction.zadd(followeeKey, date.getTime(), String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(transaction, jedis);
        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction transaction = jedisAdapter.mulit(jedis);

        transaction.zrem(followerKey, String.valueOf(userId));
        transaction.zrem(followeeKey, String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(transaction, jedis);
        return ret.size() == 2 && (Long)ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
    }

    public long getFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    public long getFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey =RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }

    private List<Integer> getIdsFromSet(Set<String> idSet){
        List<Integer> ids = new ArrayList<>();
        for(String str : idSet){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


}
