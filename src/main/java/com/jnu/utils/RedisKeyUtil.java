package com.jnu.utils;

import sun.util.locale.provider.SPILocaleProviderAdapter;

/**
 * Created by Damon on 2017/8/16.
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";//点赞
    private static String BIZ_DISLIKE = "DISLIKE";//点踩
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";//事件队列标识
    private static String BIZ_FOLLOWER = "FOLLOWER";//粉丝
    private static String BIZ_FOLLOWEE = "FOLLOWEE";//关注对象

    public static String getFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + String.valueOf(entityId);
    }

    public static String getFolloweeKey(int userId, int entityType){
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + String.valueOf(entityType);
    }

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + String.valueOf(entityId);
    }

    public static String getBizEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }
}
