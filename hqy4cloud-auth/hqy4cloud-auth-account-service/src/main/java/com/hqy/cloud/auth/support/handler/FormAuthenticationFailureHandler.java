package com.hqy.cloud.auth.support.handler;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.hqy.cloud.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 表单登录失败处理逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 16:13
 */
@Slf4j
public class FormAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.debug("表单登录失败:{}", exception.getLocalizedMessage());
        String url = HttpUtil.encodeParams(String.format("/token/login?error=%s", exception.getMessage()),
                CharsetUtil.CHARSET_UTF_8);
        WebUtils.getResponse().sendRedirect(url);
    }
}
