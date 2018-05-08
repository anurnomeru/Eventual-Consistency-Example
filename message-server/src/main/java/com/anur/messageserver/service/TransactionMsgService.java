package com.anur.messageserver.service;

import com.anur.field.DeadStatusEnum;
import com.anur.field.MsgStatusEnum;
import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageapi.config.ArtistConfiguration;
import com.anur.messageserver.dao.TransactionMsgMapper;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.core.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Date;
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

    @Override
    public String prepareMsg(Object msg, String routingKey, String exchange, String paramMap, String artist) {
        TransactionMsg.TransactionMsgBuilder builder = TransactionMsg.builder();
        String id = UUID.randomUUID() + String.valueOf(Math.random());
        builder.id(id)
                .creater(artist)
                .paramMap(paramMap)
                .createTime(new Date())
                .dead(DeadStatusEnum.ALIVE.name())
                .status(MsgStatusEnum.PREPARE.name())
                .version(0);
        transactionMsgMapper.insert(builder.build());
        return id;
    }

    @Override
    public int confirmMsgToSend(String id) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        transactionMsg.setEditor(artistConfiguration.getArtist());
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.CONFIRM.name());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByCondition(transactionMsg, this._genVersionCondition(originalVersion));
    }

    @Override
    public int sendMsg(String id) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        transactionMsg.setEditor(artistConfiguration.getArtist());
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.SENDING.name());
        transactionMsg.setMsgSendTime(new Date());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByCondition(transactionMsg, this._genVersionCondition(originalVersion));
    }

    @Override
    public int acknowledgement(String id, String artist) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        transactionMsg.setEditor(artist);
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.ACK.name());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByCondition(transactionMsg, this._genVersionCondition(originalVersion));
    }

    /**
     * version 参数防止并发下的重复修改，确保数据修改正确
     */
    private Condition _genVersionCondition(int version) {
        Condition condition = new Condition(TransactionMsg.class);
        condition.or().andEqualTo("version", version);
        return condition;
    }
}
