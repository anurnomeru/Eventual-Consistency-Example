package com.anur.messageserver.service.impl;

import com.anur.messageserver.dao.TransactionMsgMapper;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.service.TransactionMsgService;
import com.anur.messageserver.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by Anur IjuoKaruKas on 2018/05/07.
 */
@Service
@Transactional
public class TransactionMsgServiceImpl extends AbstractService<TransactionMsg> implements TransactionMsgService {
    @Resource
    private TransactionMsgMapper transactionMsgMapper;

}
