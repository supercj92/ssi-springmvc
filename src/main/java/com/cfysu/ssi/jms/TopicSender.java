package com.cfysu.ssi.jms;

/**
 * Created by cj on 2017/8/31.
 */
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * created on 2015/6/4
 * @author dennisit@163.com
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:context/spring-activemq-producer.xml"})
public class TopicSender {

    @Resource(name = "providerJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Test
    public void send(){
        sendMqMessage(null,"spring activemq topic type message[with listener] !");
    }

    /**
     * 说明:发送的时候如果这里没有显示的指定destination.将用spring xml中配置的destination
     * @param destination
     * @param message
     */
    public void sendMqMessage(Destination destination, final String message){
        if(null == destination){
            destination = jmsTemplate.getDefaultDestination();
        }
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });
        System.out.println("spring send text message...");
    }


    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
}