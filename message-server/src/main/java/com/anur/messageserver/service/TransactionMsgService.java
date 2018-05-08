package com.anur.messageserver.service;

import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.core.Service;


/**
 * Created by Anur IjuoKaruKas on 2018/05/07.
 */
public interface TransactionMsgService extends Service<TransactionMsg> {

    /**
     * 预发送消息，先将消息保存到消息中心
     */
    int prepareMsg(TransactionMsg transactionMsg);

    /**
     * 生产者确认消息可投递
     */
    int confirmMsgToSend(String id);

    /**
     * 向队列投递消息
     */
    int sendMsg(String id);

    /**
     * 消费者确认消费成功
     */
    int acknowledgement(String id);
}
