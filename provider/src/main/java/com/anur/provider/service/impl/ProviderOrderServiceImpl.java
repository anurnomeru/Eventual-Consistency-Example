package com.anur.provider.service.impl;

import com.anur.provider.core.AbstractService;
import com.anur.provider.dao.ProviderOrderMapper;
import com.anur.provider.model.ProviderOrder;
import com.anur.provider.service.ProviderOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by Anur IjuoKaruKas on 2018/05/11.
 */
@Service
@Transactional
public class ProviderOrderServiceImpl extends AbstractService<ProviderOrder> implements ProviderOrderService {
    @Resource
    private ProviderOrderMapper providerOrderMapper;

}
