package com.luoyk.osf.core.loacl;

import com.google.common.eventbus.EventBus;

/**
 * 自定义线程池
 *
 * @author luoyk
 */
public class LocalEventBus {
    public static final EventBus EVENT_BUS = new EventBus();
}