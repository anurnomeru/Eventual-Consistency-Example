package com.anur.provider.service.impl;

import com.anur.config.ArtistConfiguration;
import com.anur.provider.dao.PrepareMsgMapper;
import com.anur.provider.feignservice.TransactionMsgService;
import com.anur.provider.model.PrepareMsg;
import com.anur.provider.service.PrepareMsgService;
import com.anur.provider.core.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;


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
    public Future<Integer> prepareMsg(PrepareMsg prepareMsg) {
        int result = transactionMsgService.prepareMsg(prepareMsg.getId(), prepareMsg.getMsg(), prepareMsg.getRoutingKey(), prepareMsg.getExchange(), prepareMsg.getParamMap(), artistConfiguration.getArtist());
        if (result == 1) {
            prepareMsgMapper.deleteByPrimaryKey(prepareMsg.getId());
        }
        return new AsyncResult<>(result);
    }

    @Override
    public List<PrepareMsg> getUnConfirmList() {
        Condition condition = new Condition(PrepareMsg.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -2);
        condition.or().andLessThan("createTime", calendar.getTime());
        return prepareMsgMapper.selectByCondition(condition);
    }

    @Async
    @Override
    public void confirmMsgToSend(String orderId) {
        System.out.println(orderId);
        transactionMsgService.confirmMsgToSend(orderId);
    }
}
