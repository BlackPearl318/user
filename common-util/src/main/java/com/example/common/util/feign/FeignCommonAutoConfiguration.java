package com.example.common.util.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignCommonAutoConfiguration {

    /**
     * 使用 @ConditionalOnMissingBean
     * 如果容器中已经有了 HttpMessageConverters，则跳过
     * 如果没有（在 gateway 中），则注入这个默认的 Jackson 转换器
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        return new HttpMessageConverters(jacksonConverter);
    }
}
