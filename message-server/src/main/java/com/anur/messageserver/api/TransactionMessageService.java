package com.anur.messageserver.api;

/**
 * Created by Anur IjuoKaruKas on 2018/5/6
 */
public interface TransactionMessageService {

    /**
     * 预发送消息，先将消息保存到消息中心
     */
    int prepareMsg();

    /**
     * 生产者确认消息可投递
     */
    void confirmMsgToSend();

    /**
     * 向队列投递消息
     */
    int sendMsg();

    /**
     * 消息重发
     */
    int resendMsg();

    /**
     * 消费者确认消费成功
     */
    void confirmMsgSended();
}
