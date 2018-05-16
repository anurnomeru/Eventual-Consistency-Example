package com.anur.messageserver.service;

import com.anur.config.ArtistConfiguration;
import com.anur.field.DeadStatusEnum;
import com.anur.field.MsgStatusEnum;
import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageserver.dao.TransactionMsgMapper;
import com.anur.exception.ServiceException;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.core.AbstractService;
import com.anur.messageserver.rabbitmq.MsgSender;
import com.github.pagehelper.PageHelper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by Anur IjuoKaruKas on 2018/05/07.
 */
@Service
@Transactional
@Log
public class TransactionMsgService extends AbstractService<TransactionMsg> implements TransactionMsgApi {
    @Resource
    private TransactionMsgMapper transactionMsgMapper;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    @Autowired
    private MsgSender msgSender;

    @Override
    public int prepareMsg(String id, String msg, String routingKey, String exchange, String paramMap, String artist) {
        if (transactionMsgMapper.selectByPrimaryKey(id) != null) {
            log.info("Create msg fail: " + id + ", cause to exist primary key.");
            return 0;
        }

        TransactionMsg.TransactionMsgBuilder builder = TransactionMsg.builder();

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

        return transactionMsgMapper.insert(builder.build());
    }

    @Override
    public int confirmMsgToSend(String id, String caller) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        if (transactionMsg == null) {
            log.info("Confirm msg fail: " + id + ", cause to not exist data.");
            return 0;
        }

        transactionMsg.setDead(DeadStatusEnum.ALIVE.name());
        transactionMsg.setEditor(artistConfiguration.getArtist());
        transactionMsg.setEditTime(new Date());
        transactionMsg.setMsgSendTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.CONFIRM.name());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id, MsgStatusEnum.PREPARE));
    }

    @Override
    public void sendMsg(String id) {
        // 更新表单字段
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        if (transactionMsg == null) {
            return;
        }

        // 发消息到队列
        msgSender.send(transactionMsg.getMsgExchange(), transactionMsg.getMsgRoutingKey(), transactionMsg.getMsgContent(), new CorrelationData(transactionMsg.getId()));
    }

    @Override
    public int acknowledgement(String id, String artist) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        transactionMsg.setEditor(artist);
        transactionMsg.setEditTime(new Date());

        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setStatus(MsgStatusEnum.ACK.name());
        transactionMsg.setDead(DeadStatusEnum.DEAD.name());
        transactionMsg.setVersion(originalVersion + 1);

        return transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id, null));
    }

    /**
     * version 参数防止并发下的重复修改，确保数据修改正确
     */
    private Condition _genVersionCondition(int version, String id, MsgStatusEnum msgStatusEnum) {
        Condition condition = new Condition(TransactionMsg.class);

        Example.Criteria criteria = condition.or();
        criteria.andEqualTo("version", version).andEqualTo("id", id);

        if (msgStatusEnum != null) {
            criteria.andEqualTo("status", msgStatusEnum.name());
        }
        return condition;
    }

    /**
     * 获取所有未确认的消息
     */
    public List<TransactionMsg> getUnConfirmList() {
        Condition condition = new Condition(TransactionMsg.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -5);
        condition.or().andEqualTo("status", MsgStatusEnum.PREPARE).andEqualTo("dead", DeadStatusEnum.ALIVE).andLessThan("createTime", calendar.getTime());

        return transactionMsgMapper.selectByCondition(condition);
    }

    /**
     * 获取所有未ACK的消息
     */
    public List<TransactionMsg> getUnAckList() {
        Condition condition = new Condition(TransactionMsg.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -5);
        condition.or().andEqualTo("status", MsgStatusEnum.CONFIRM).andEqualTo("dead", DeadStatusEnum.ALIVE).andLessThan("msgSendTime", calendar.getTime());

        return transactionMsgMapper.selectByCondition(condition);
    }

    /**
     * 更新version，version到5直接DEAD
     */
    public int updateVersion(String id, MsgStatusEnum msgStatusEnum) {
        TransactionMsg transactionMsg = transactionMsgMapper.selectByPrimaryKey(id);
        int originalVersion = transactionMsg.getVersion();
        transactionMsg.setEditor(artistConfiguration.getArtist());
        transactionMsg.setEditTime(new Date());

        if (originalVersion == 10) {
            transactionMsg.setDead(DeadStatusEnum.DEAD.name());
        }

        transactionMsg.setVersion(originalVersion + 1);
        return transactionMsgMapper.updateByConditionSelective(transactionMsg, this._genVersionCondition(originalVersion, id, msgStatusEnum));
    }
}