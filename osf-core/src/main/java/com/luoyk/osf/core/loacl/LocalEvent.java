package com.luoyk.osf.core.loacl;

import org.springframework.context.ApplicationEvent;

public class LocalEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public LocalEvent(Object source) {
        super(source);
    }
}
