package com.anur.messageserver.cron;

import com.anur.field.MsgStatusEnum;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.rabbitmq.MsgSender;
import com.anur.messageserver.service.TransactionMsgService;
import com.anur.messageserver.util.UrlBuilder;
import com.github.pagehelper.PageHelper;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by Anur IjuoKaruKas on 2018/5/11
 */
@Component
@Log
public class MsgConfirm {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "*/2 * * * * *")
    public void confirmMsg() {
        PageHelper.startPage(1, 10).setOrderBy("create_time DESC");
        List<TransactionMsg> unConfirmList = transactionMsgService.getUnConfirmList();

        if (unConfirmList.size() > 0) {
            log.info(String.format("There is %s Msg not confirm immediately", unConfirmList.size()));
        }

        for (TransactionMsg transactionMsg : unConfirmList) {
            boolean resultBoolean = false;
            try {
                String url = UrlBuilder.buildUrl(transactionMsg);
                resultBoolean = restTemplate.getForObject(url, boolean.class);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (resultBoolean) {
                    log.info(String.format("The Msg id: %s reConfirm from provider success.", transactionMsg.getId()));
                    int result = transactionMsgService.confirmMsgToSend(transactionMsg.getId(), this.getClass().getSimpleName());
                    if (result == 1) {
                        transactionMsgService.sendMsg(transactionMsg.getId());
                    } else {
                        log.info("Msg confirm fail: " + transactionMsg.getId() + ", caller is " + this.getClass().getSimpleName());
                    }
                } else {
                    log.info(String.format("The Msg id: %s reConfirm from provider fail!", transactionMsg.getId()));
                    transactionMsgService.updateVersion(transactionMsg.getId(), MsgStatusEnum.PREPARE);
                }
            }
        }
    }
}
