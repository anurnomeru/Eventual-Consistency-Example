package com.anur.consumer.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Anur IjuoKaruKas on 2018/5/15
 */
public class FastJsonUtils {
    public static String getJsonFromJava(Object javaObj) throws Exception {
        ValueFilter filter = (source, name, value) -> value instanceof Integer ? value + ""
                : (value instanceof Timestamp ? FastJsonUtils.format(
                (Timestamp) value, "yyyy-MM-dd HH:mm:ss")
                : (value instanceof Date ? FastJsonUtils
                .format((Date) value, "yyyy-MM-dd HH:mm:ss") //如果不需要时间，可以去掉HH:mm:ss
                : value));

        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.getValueFilters().add(filter);
        serializer.write(javaObj);
        return out.toString();
    }

    public static String format(Date date, String format) {
        return null == date ? null : (new SimpleDateFormat(format))
                .format(date);
    }

    public static <T> T getObject(String json, Class<T> clazz) throws Exception {
        return JSON.parseObject(json, clazz);
    }
}
