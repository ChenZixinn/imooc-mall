package com.imooc.mall.config;

import com.imooc.mall.filter.CustomerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerFilterConfig {
    @Bean
    public CustomerFilter customerFilter(){
        return new CustomerFilter();
    }

    @Bean(name = "CustomerFilterConf")
    public FilterRegistrationBean customerFilterConfig(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customerFilter());
        filterRegistrationBean.addUrlPatterns("/cart/*");
        filterRegistrationBean.addUrlPatterns("/order/*");
//        filterRegistrationBean.addUrlPatterns("/pay");
        filterRegistrationBean.setName("customerFilterConf");
        return filterRegistrationBean;
    }
}
