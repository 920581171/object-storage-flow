package com.luoyk.osf.core.mq.jms;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;
import org.springframework.jms.core.JmsMessagingTemplate;

public class JmsMessagingSender implements OsfSender {

    private final JmsMessagingTemplate jmsMessagingTemplate;

    public JmsMessagingSender(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        return false;
    }
}
