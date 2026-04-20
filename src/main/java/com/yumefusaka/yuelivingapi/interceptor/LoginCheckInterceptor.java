package com.yumefusaka.yuelivingapi.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yumefusaka.yuelivingapi.common.context.BaseContext;
import com.yumefusaka.yuelivingapi.common.result.Result;
import com.yumefusaka.yuelivingapi.common.role.RoleRequired;
import com.yumefusaka.yuelivingapi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Arrays;

//自定义拦截器
@Component //当前拦截器对象由Spring创建和管理
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    //前置方式
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle .... ");
        //1.获取请求url
        //2.判断请求url中是否包含login，如果包含，说明是登录操作，放行
        //3.获取请求头中的令牌（token）
        String token = request.getHeader("Authorization");
        log.info("从请求头中获取的令牌：{}", token);
        //4.判断令牌是否存在，如果不存在，返回错误结果（未登录）
        if (!StringUtils.hasLength(token)) {
            log.info("Token不存在");

            //创建响应结果对象
            Result responseResult = Result.noToken("请先登录喵~");
            //把Result对象转换为JSON格式字符串 (fastjson是阿里巴巴提供的用于实现对象和json的转换工具类)
            String json = JSONObject.toJSONString(responseResult);
            //设置状态码
            response.setStatus(401);
            //设置响应头（告知浏览器：响应的数据类型为json、响应的数据编码表为utf-8）
            response.setContentType("application/json;charset=utf-8");
            //响应
            response.getWriter().write(json);
            return false;//不放行
        }

        // 处理Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //5.解析token，如果解析失败，返回错误结果（未登录）
        try {
            jwtUtils.parseToken(token);
            Long userId = jwtUtils.getUserIdFromToken(token);
            Long roleId = jwtUtils.getRoleIdFromToken(token);
            BaseContext.setCurrentId(userId.toString());
            BaseContext.setCurrentRoleId(roleId == null ? null : roleId.toString());

            if (handler instanceof HandlerMethod handlerMethod) {
                RoleRequired required = handlerMethod.getMethodAnnotation(RoleRequired.class);
                if (required == null) {
                    required = handlerMethod.getBeanType().getAnnotation(RoleRequired.class);
                }
                if (required != null) {
                    Long currentRoleId = roleId;
                    if (currentRoleId == null || Arrays.stream(required.value()).noneMatch(r -> r == currentRoleId)) {
                        Result responseResult = Result.error("权限不足");
                        String json = JSONObject.toJSONString(responseResult);
                        response.setContentType("application/json;charset=utf-8");
                        response.setStatus(403);
                        response.getWriter().write(json);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.info("令牌解析失败!");

            //创建响应结果对象
            Result responseResult = Result.noToken("NOT_LOGIN");
            //把Result对象转换为JSON格式字符串 (fastjson是阿里巴巴提供的用于实现对象和json的转换工具类)
            String json = JSONObject.toJSONString(responseResult);
            //设置响应头
            response.setContentType("application/json;charset=utf-8");
            //设置状态码
            response.setStatus(401);
            //响应
            response.getWriter().write(json);
            return false;
        }

        //6.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.clear();
    }
}