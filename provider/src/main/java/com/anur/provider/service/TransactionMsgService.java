package com.anur.provider.service;

import com.anur.messageapi.api.TransactionMsgApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@Service
@FeignClient(value = "message-server")
public interface TransactionMsgService extends TransactionMsgApi {

}
