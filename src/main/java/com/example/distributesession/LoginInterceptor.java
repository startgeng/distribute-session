package com.example.distributesession;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final String JWT_KEY = "imooc";

    /**
     * 返回true表示不拦截 返回false不拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)){
            throw new RuntimeException("token为空");
        }
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            request.setAttribute("id",decodedJWT.getClaim("id").asInt());
        }catch (Exception e){
            //token过期或者解码失败
            throw new RuntimeException("token过期");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
