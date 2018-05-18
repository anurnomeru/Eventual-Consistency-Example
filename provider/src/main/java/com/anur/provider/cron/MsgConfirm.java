package com.anur.provider.cron;

import com.anur.provider.model.PrepareMsg;
import com.anur.provider.service.PrepareMsgService;
import com.github.pagehelper.PageHelper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Anur IjuoKaruKas on 2018/5/14
 */
@Component
@Log
public class MsgConfirm {

    @Autowired
    private PrepareMsgService prepareMsgService;

    @Scheduled(cron = "*/1 * * * * *")
    public void checkPrepareMsg() {
        PageHelper.startPage(1,10).setOrderBy("create_time desc");
        List<PrepareMsg> prepareMsgList = prepareMsgService.getUnConfirmList();
        if (prepareMsgList.size() > 0) {
            log.info("There is "+ prepareMsgList.size() +"to rePrepare");
        }
        for (PrepareMsg prepareMsg : prepareMsgList) {
            prepareMsgService.prepareMsg(prepareMsg);
        }
    }
}
