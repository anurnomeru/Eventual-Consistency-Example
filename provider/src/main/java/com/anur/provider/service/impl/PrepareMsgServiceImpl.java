package com.anur.provider.service.impl;

import com.anur.config.ArtistConfiguration;
import com.anur.provider.dao.PrepareMsgMapper;
import com.anur.provider.feignservice.TransactionMsgService;
import com.anur.provider.model.PrepareMsg;
import com.anur.provider.service.PrepareMsgService;
import com.anur.provider.core.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * Created by Anur IjuoKaruKas on 2018/05/14.
 */
@Service
@Transactional
public class PrepareMsgServiceImpl extends AbstractService<PrepareMsg> implements PrepareMsgService {
    @Resource
    private PrepareMsgMapper prepareMsgMapper;

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    public PrepareMsg genMsg(String id, String msg, String routingKey, String exchange, String paramMap) {
        PrepareMsg prepareMsg = new PrepareMsg();
        prepareMsg.setId(id);
        prepareMsg.setMsg(msg);
        prepareMsg.setRoutingKey(routingKey);
        prepareMsg.setExchange(exchange);
        prepareMsg.setParamMap(paramMap);
        prepareMsg.setCreateTime(new Date());
        prepareMsgMapper.insert(prepareMsg);
        return prepareMsg;
    }

    @Async
    @Override
    public void prepareMsg(PrepareMsg prepareMsg) {
        if (transactionMsgService.prepareMsg(prepareMsg.getId(), prepareMsg.getMsg(), prepareMsg.getRoutingKey(), prepareMsg.getExchange(), prepareMsg.getParamMap(), artistConfiguration.getArtist()) == 1) {
            prepareMsgMapper.deleteByPrimaryKey(prepareMsg.getId());
        }
    }

    @Override
    public List<PrepareMsg> getUnConfirmList() {
        Condition condition = new Condition(PrepareMsg.class);
        Date date = new Date();
        date.setSeconds(date.getSeconds() - 1);
        condition.or().andLessThan("createTime", date);
        return prepareMsgMapper.selectByCondition(condition);
    }

    @Async
    @Override
    public void confirmMsgToSend(String orderId) {
        System.out.println("=====================");
        System.out.println(orderId);
        System.out.println(transactionMsgService);
        transactionMsgService.confirmMsgToSend(orderId);
    }
}
