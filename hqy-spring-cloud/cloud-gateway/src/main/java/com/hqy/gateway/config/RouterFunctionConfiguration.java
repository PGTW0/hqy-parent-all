package com.hqy.gateway.config;

import com.hqy.gateway.handler.ImageCodeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 路由配置信息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/7 10:38
 */
@Configuration(proxyBeanMethods = false)
public class RouterFunctionConfiguration {

    @Bean
    public ImageCodeHandler imageCodeHandler() {
        return new ImageCodeHandler();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ImageCodeHandler imageCodeHandler) {
        return RouterFunctions.route(
                RequestPredicates.path("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), imageCodeHandler);
    }

}
