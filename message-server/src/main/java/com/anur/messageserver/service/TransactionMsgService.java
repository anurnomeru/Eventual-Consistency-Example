package com.anur.messageserver.service;

import com.anur.config.ArtistConfiguration;
import com.anur.field.DeadStatusEnum;
import com.anur.field.MsgStatusEnum;
import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageserver.dao.TransactionMsgMapper;
import com.anur.exception.ServiceException;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.core.AbstractService;
import com.anur.messageserver.rabbitmq.MsgSender;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by Anur IjuoKaruKas on 2018/05/07.
 */
@Service
@Transactional
public class TransactionMsgService extends AbstractService<TransactionMsg> implements TransactionMsgApi {
    @Resource
    private TransactionMsgMapper transactionMsgMapper;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    @Autowired
    private MsgSender msgSender;

    @Override
    public String prepareMsg(String msg, String routingKey, String exchange, String paramMap, String artist) {
        TransactionMsg.TransactionMsgBuilder builder = TransactionMsg.builder();
        String id = UUID.randomUUID() + String.valueOf(Math.random());
        System.out.println("PREPARE MSG: " + id);

        builder.id(id)
                .creater(artist)
                .createTime(new Date())
                .dead(DeadStatusEnum.ALIVE.name())
                .status(MsgStatusEnum.PREPARE.name())
                .version(0)
                .msgRoutingKey(routingKey)
                .msgExchange(exchange)
                .msgContent(msg)
                .paramMap(paramMap);

        transactionMsgMapper.insert(builder.build());

        return id;
    }

    @Override
    public int confirmMsgToSend(String id) {
        System.out.println("CONFIRM MSG: " + id);
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);

        if (transactionMsg == null) {
            throw new ServiceException("id is invalid.");
        }

        transactionMsg.setEditor(artistConfiguration.getArtist());
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.CONFIRM.name());
        transactionMsg.setVersion(originalVersion + 1);

        int result = transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id));
        return result;
    }

    @Async
    @Override
    public void sendMsg(String id) {
        System.out.println("SENDING MSG: " + id);
        // 更新表单字段
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
//        transactionMsg.setEditor(artistConfiguration.getArtist());
//        transactionMsg.setEditTime(new Date());
//
//        int originalVersion = transactionMsg.getVersion();
//        transactionMsg.setStatus(MsgStatusEnum.SENDING.name());
//        transactionMsg.setMsgSendTime(new Date());
//        transactionMsg.setVersion(originalVersion + 1);
//
//        transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id));

        // 发消息到队列
        msgSender.send(transactionMsg.getMsgExchange(), transactionMsg.getMsgRoutingKey(), transactionMsg.getMsgContent(), new CorrelationData(id));
    }

    @Override
    public int acknowledgement(String id, String artist) {
        System.out.println("ACK MSG: " + id);

        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        transactionMsg.setEditor(artist);
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.ACK.name());
        transactionMsg.setDead(DeadStatusEnum.DEAD.name());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id));
    }

    /**
     * version 参数防止并发下的重复修改，确保数据修改正确
     */
    private Condition _genVersionCondition(int version, String id) {
        Condition condition = new Condition(TransactionMsg.class);
        condition.or().andEqualTo("version", version).andEqualTo("id", id);
        return condition;
    }

    public List<TransactionMsg> getUnConfirmList() {
        Condition condition = new Condition(TransactionMsg.class);
        condition.or().andEqualTo("status", MsgStatusEnum.PREPARE).andEqualTo("dead", DeadStatusEnum.ALIVE);
        return transactionMsgMapper.selectByCondition(condition);
    }
}