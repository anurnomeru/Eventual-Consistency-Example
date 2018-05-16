package com.anur.provider.service;

import com.anur.provider.model.PrepareMsg;
import com.anur.provider.core.Service;

import java.util.List;
import java.util.concurrent.Future;


/**
 * Created by Anur IjuoKaruKas on 2018/05/14.
 */
public interface PrepareMsgService extends Service<PrepareMsg> {
    PrepareMsg genMsg(String id, String msg, String routingKey, String exchange, String paramMap);

    Future<Integer> prepareMsg(PrepareMsg prepareMsg);

    List<PrepareMsg> getUnConfirmList();

    void confirmMsgToSend(String orderId, String caller);
}
