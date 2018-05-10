package com.anur.messageserver.config;

import com.anur.common.Result;
import com.anur.common.ResultCode;
import com.anur.exception.ServiceException;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.ServletException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Anur IjuoKaruKas on 2017/12/13.
 */
@Log
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

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
                log.log(Level.WARNING, message, e);
            }
//            responseResult(httpServletResponse, resultBuilder.build());
            return new ModelAndView();
        });
    }

//    private void responseResult(HttpServletResponse response, Result result) {
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("Content-type", "application/json;charset=UTF-8");
//        response.setStatus(200);
//        try {
//            response.getWriter().write(JSON.toJSONString(result));
//        } catch (IOException ex) {
//            log.error(ex.getMessage());
//        }
//    }

//    // 解决跨域问题
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        //registry.addMapping("/**");
//    }

}
