package com.jnu.async;

import java.util.List;

/**
 * Created by Damon on 2017/8/16.
 */
public interface EventHandler {

    void doHandle(EventModel eventModel);

    List<EventType> getSupportEventTypes();

}
