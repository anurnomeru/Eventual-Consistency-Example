package com.anur.messageserver.cron;

import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Anur IjuoKaruKas on 2018/5/11
 */
@Component
public class MsgConfirm {

    @Autowired
    private TransactionMsgService transactionMsgService;


    public void confirmMsg(){
        List<TransactionMsg> unConfirmList = transactionMsgService.getUnConfirmList();
        for (TransactionMsg transactionMsg : unConfirmList) {

        }
    }
}
