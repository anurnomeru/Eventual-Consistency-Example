package com.anur.messageserver.cron;

import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.rabbitmq.MsgSender;
import com.anur.messageserver.service.TransactionMsgService;
import com.anur.messageserver.util.UrlBuilder;
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
public class MsgConfirm {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MsgSender msgSender;

    @Scheduled(cron = "*/2 * * * * *")
    public void confirmMsg() {

        List<TransactionMsg> unConfirmList = transactionMsgService.getUnConfirmList();

        if (unConfirmList.size() > 0) {
            System.out.println("定时捞取未确认任务中!");
        }

        for (TransactionMsg transactionMsg : unConfirmList) {
            boolean result = false;
            try {
                String url = UrlBuilder.buildUrl(transactionMsg);
                result = restTemplate.getForObject(url, boolean.class);
            } catch (Exception e) {
                continue;
            } finally {
                if (result) {
                    System.out.println("id: " + transactionMsg.getId() + "确认成功，准备发送！");
                    transactionMsgService.confirmMsgToSend(transactionMsg.getId());
                } else {
                    System.out.println("id: " + transactionMsg.getId() + "确认失败，如果未超过重试次数，将在下次确认中重试！");
                    transactionMsgService.updateVersion(transactionMsg.getId());
                }
            }
        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void reSendMsg() {
        List<TransactionMsg> unAckList = transactionMsgService.getUnAckList();

        if (unAckList.size() > 0) {
            System.out.println("定时捞取未ACK任务中!");
        }

        for (TransactionMsg transactionMsg : unAckList) {
            msgSender.send(transactionMsg.getMsgExchange(), transactionMsg.getMsgRoutingKey(), transactionMsg.getMsgContent(), new CorrelationData(transactionMsg.getId()));
        }
    }
}
