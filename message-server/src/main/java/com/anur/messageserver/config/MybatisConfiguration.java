package com.anur.messageserver.config;

import com.anur.messageserver.common.Constant;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.autoconfigure.PageHelperProperties;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Anur IjuoKaruKas on 2017/12/13.
 */
@Configuration
public class MybatisConfiguration {

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(Constant.MODEL_PACKAGE);

//        // 配置分页插件，详情请查阅官方文档
//        PageHelperProperties properties = new PageHelperProperties();
//        properties.setPageSizeZero("true");// 分页尺寸为0时查询所有纪录不再执行分页
//        properties.setReasonable("true");// 页码<=0 查询第一页，页码>=总页数查询最后一页
//        properties.setSupportMethodsArguments("true");// 支持通过 Mapper 接口参数来传递分页参数
//
//        PageInterceptor pageInterceptor = new PageInterceptor();
//        pageInterceptor.setProperties(properties.getProperties());
//        //  添加插件
//        factory.setPlugins(new Interceptor[]{pageInterceptor});

        // 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        return factory.getObject();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage(Constant.MAPPER_PACKAGE);

        // 配置通用Mapper，详情请查阅官方文档
        Properties properties = new Properties();
        properties.setProperty("mappers", Constant.MAPPER_INTERFACE_REFERENCE);
        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }
}

