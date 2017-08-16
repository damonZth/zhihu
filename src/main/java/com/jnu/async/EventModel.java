package com.jnu.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Damon on 2017/8/16.
 */
public class EventModel {
    private EventType eventType;//事件类型
    private int actorId;//事件触发者
    //触发体
    private int entityType;
    private int entityId;
    //触发对象
    private int entityOwnerId;
    private Map<String, String> exts = new HashMap<>();

    public EventModel(){

    }

    public EventModel(EventType eventType){
        this.eventType = eventType;
    }

    public EventModel setExts(String key, String value){
        exts.put(key, value);
        return this;//链式调用
    }
    public String getExts(String  key){
        return exts.get(key);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

}
