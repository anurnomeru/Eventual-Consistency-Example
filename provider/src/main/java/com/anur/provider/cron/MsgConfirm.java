package com.anur.provider.cron;

import com.anur.provider.model.PrepareMsg;
import com.anur.provider.service.PrepareMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Anur IjuoKaruKas on 2018/5/14
 */
@Component
public class MsgConfirm {

    @Autowired
    private PrepareMsgService prepareMsgService;

    @Scheduled(cron = "*/1 * * * * *")
    public void checkPrepareMsg() {
        List<PrepareMsg> prepareMsgList = prepareMsgService.getUnConfirmList();
        if (prepareMsgList.size() > 0) {
            System.out.println("消息重发中");
        }
        for (PrepareMsg prepareMsg : prepareMsgList) {
            prepareMsgService.prepareMsg(prepareMsg);
        }
    }
}
