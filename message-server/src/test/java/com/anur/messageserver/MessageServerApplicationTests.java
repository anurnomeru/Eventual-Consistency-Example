package com.anur.messageserver;

import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.service.TransactionMsgService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.IOP.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServerApplicationTests {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Test
    public void contextLoads() {
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setId(UUID.randomUUID().toString());
        transactionMsgService.save(transactionMsg);
    }
}
