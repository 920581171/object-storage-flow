package com.luoyk.osf.core.mq.jms;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;
import org.springframework.jms.core.JmsMessagingTemplate;

public class JmsMessagingReceiver implements OsfReceiver {

    private final JmsMessagingTemplate jmsMessagingTemplate;

    public JmsMessagingReceiver(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @Override
    public boolean receive(DelayMessage delayMessage) {
        return false;
    }
}
