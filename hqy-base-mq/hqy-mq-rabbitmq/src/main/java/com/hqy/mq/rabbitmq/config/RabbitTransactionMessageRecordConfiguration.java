package com.hqy.mq.rabbitmq.config;

import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.common.service.impl.MessageTransactionRecordServiceImpl;
import com.hqy.mq.rabbitmq.RabbitmqProcessor;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 基于rabbitmq + 本地消息表 实现分布式事务.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 11:26
 */
@Slf4j
@Configuration
@SuppressWarnings("rawtypes")
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "transactional", value = "true")
public class RabbitTransactionMessageRecordConfiguration {

    public static final String EXCHANGE = "global-transaction-exchange";

    public static final String QUEUE = "global-transaction-queue";






    @Bean
    FanoutExchange transactionalExchange() {
        return new FanoutExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue transactionalQueue() {
        return new Queue(QUEUE, true, false, false);
    }

    @Bean
    Binding transactionalBinding(FanoutExchange transactionalExchange, Queue transactionalQueue) {
        return BindingBuilder.bind(transactionalQueue).to(transactionalExchange);
    }





    @Bean
    @SuppressWarnings("unchecked")
    public DeliveryMessageService rabbitDeliveryService() {
        return messageRecord -> {
            try {
                CorrelationData correlationData = new CorrelationData(messageRecord.getMessageId());
                correlationData.getFuture().addCallback(ackCallBack -> {
                    if (ackCallBack == null || !ackCallBack.isAck()) {
                        //重发消息
                        RabbitmqProcessor.getInstance().sendMessage(EXCHANGE,  "", messageRecord, correlationData);
                    } else {
                        MessageTransactionRecordServiceImpl service = SpringContextHolder.getBean(MessageTransactionRecordServiceImpl.class);
                        messageRecord.setStatus(true);
                        service.update(messageRecord);
                    }
                }, failCallback -> RabbitmqProcessor.getInstance().sendMessage(EXCHANGE, "", messageRecord, correlationData));

                RabbitmqProcessor.getInstance().sendMessage(EXCHANGE, "", messageRecord, correlationData);
            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }

}
