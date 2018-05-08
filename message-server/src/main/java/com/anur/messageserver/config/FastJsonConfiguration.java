//package com.anur.messageserver.config;
//
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alibaba.fastjson.support.config.FastJsonConfig;
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
//import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Anur IjuoKaruKas on 2018/1/11.
// * 把spring-boot默认的json解析器由Jenkins换为fastjson
// */
//@Configuration
//public class FastJsonConfiguration extends FastJsonHttpMessageConverter4 {
//
//    @Bean
//    public HttpMessageConverters fastJsonConverter() {
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        //自定义格式化输出
//        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.WriteMapNullValue,//保留空的字段
//                SerializerFeature.PrettyFormat,  // 将JavaBean序列化为带格式的JSON文本
//                SerializerFeature.WriteNullStringAsEmpty, //String null -> ""
//                SerializerFeature.WriteNullNumberAsZero);//Number null -> 0
//        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        List<MediaType> mediaTypes = new ArrayList<>();
//        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//        setSupportedMediaTypes(mediaTypes);// tag6
//
//        FastJsonHttpMessageConverter4 fastjson = new FastJsonHttpMessageConverter4();
//        fastjson.setFastJsonConfig(fastJsonConfig);
//
//        return new HttpMessageConverters(fastjson);
//    }
//}
