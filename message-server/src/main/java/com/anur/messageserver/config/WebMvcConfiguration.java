package com.anur.messageserver.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.anur.common.Result;
import com.anur.common.ResultCode;
import com.anur.messageserver.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Anur IjuoKaruKas on 2017/12/13.
 */
@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    // 使用阿里 FastJson 作为JSON MessageConverter
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,// 保留空的字段
                SerializerFeature.WriteNullStringAsEmpty,// String null -> ""
                SerializerFeature.WriteNullNumberAsZero);// Number null -> 0

        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(converter);
    }

    // 统一异常处理
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add((httpServletRequest, httpServletResponse, o, e) -> {
            Result.ResultBuilder resultBuilder = Result.builder();
            if (e instanceof ServiceException) {// 业务失败的异常，如“账号或密码错误”
                resultBuilder.code(ResultCode.FAIL.code).message(e.getMessage());
                log.info(e.getMessage());
                e.printStackTrace();

            } else if (e instanceof ValidationException) {// 数据验证异常
                resultBuilder.code(ResultCode.FAIL.code);

            } else if (e instanceof NoHandlerFoundException) {
                resultBuilder.code(ResultCode.NOT_FOUND.code).message("API [" + httpServletRequest.getRequestURI() + "] NOT FOUND");

            } else if (e instanceof ServletException) {
                resultBuilder.code(ResultCode.FAIL.code).message(e.getMessage());
            } else {
                resultBuilder.code(ResultCode.INTERNAL_SERVER_ERROR.code).message("API [" + httpServletRequest.getRequestURI() + "] ERROR: " + e.getMessage());
                String message;
                if (o instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) o;
                    message = String.format("API [%s] ERROR, METHOD：%s.%s： %s",
                            httpServletRequest.getRequestURI(),
                            handlerMethod.getBean().getClass().getName(),
                            handlerMethod.getMethod().getName(),
                            e.getMessage());

                } else {
                    message = e.getMessage();
                }
                log.error(message, e);
            }
            responseResult(httpServletResponse, resultBuilder.build());
            return new ModelAndView();
        });
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    // 解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //registry.addMapping("/**");
    }

}
