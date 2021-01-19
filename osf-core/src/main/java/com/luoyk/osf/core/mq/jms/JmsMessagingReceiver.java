package com.luoyk.osf.core.mq.jms;

import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DefaultReceiver;
import com.luoyk.osf.core.mq.DelayMessage;
import org.springframework.jms.core.JmsMessagingTemplate;

public class JmsMessagingReceiver extends DefaultReceiver {

    private final JmsMessagingTemplate jmsMessagingTemplate;


    public JmsMessagingReceiver(JmsMessagingTemplate jmsMessagingTemplate, AbstractOsf abstractOsf) {
        super(abstractOsf);
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @Override
    public boolean handler(DelayMessage delayMessage) {
        return false;
    }
}
