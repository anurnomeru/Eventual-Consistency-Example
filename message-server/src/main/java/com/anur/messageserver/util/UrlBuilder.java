package com.anur.messageserver.util;

import com.anur.messageserver.model.TransactionMsg;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anur IjuoKaruKas on 2018/5/11
 */
public class UrlBuilder {

    public static String buildUrl(TransactionMsg transactionMsg) {
        String url = String.format("http://%s/check?", transactionMsg.getCreater());
        Map<String, String> paramMap = new Gson().fromJson(transactionMsg.getParamMap(), HashMap.class);

        StringBuilder sb = new StringBuilder();

        paramMap.entrySet().stream().map(o -> sb.append(o.getKey()).append("&").append(o.getValue()));
        sb.deleteCharAt(sb.length() - 1);
        return url + sb;
    }
}
