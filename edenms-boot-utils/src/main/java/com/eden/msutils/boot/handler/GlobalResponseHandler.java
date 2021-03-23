package com.eden.msutils.boot.handler;

import com.eden.msutils.core.entity.R;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice {

    private String basePackagePattern;

    private AntPathMatcher matcher = new AntPathMatcher(".");

    public GlobalResponseHandler(String basePackage) {
        this.basePackagePattern = basePackage + ".**";
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Class clazz = returnType.getDeclaringClass();
        return matcher.match(basePackagePattern,clazz.getName());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body == null)
            return  R.success();

        if (body instanceof R){
            return body;
        }
        return R.success(body);
    }
}
