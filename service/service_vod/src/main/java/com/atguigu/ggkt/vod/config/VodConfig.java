package com.atguigu.ggkt.vod.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.atguigu.ggkt.vod.mapper")
public class VodConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        //设置请求的页面大于最大页后的操作，true回到首页；false继续请求
        paginationInterceptor.setOverflow(true);

        // 设置最大单页限制数量，默认500条，-1不受限制
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
