package com.anur.messageserver.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anur.messageserver.model.TransactionMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anur IjuoKaruKas on 2018/5/11
 */
public class UrlBuilder {

    public static String buildUrl(TransactionMsg transactionMsg) {
        String url = String.format("http://%s/check?", transactionMsg.getCreater());
        Map<String, String> paramMap = JSON.parseObject(transactionMsg.getParamMap(), new TypeReference<HashMap<String, String>>() {
        });

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
            sb.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
        return url + sb;
    }
}
