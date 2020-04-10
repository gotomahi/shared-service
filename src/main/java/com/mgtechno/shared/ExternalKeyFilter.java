package com.mgtechno.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mgtechno.shared.SharedConstants.HEADER_AUTHORIZATION;
import static com.mgtechno.shared.SharedConstants.PARAM_EXKEY;

public class ExternalKeyFilter extends GenericFilterBean {
    @Autowired
    private RestService restService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String exkey = request.getParameter(PARAM_EXKEY);
        if (StringUtils.isEmpty(request.getHeader(HEADER_AUTHORIZATION)) && !StringUtils.isEmpty(exkey)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(HEADER_AUTHORIZATION, "Bearer " + exkey);
            restService.get("http://localhost:8080" + request.getRequestURI(), null, headers, Map.class);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
