package com.example.distributesession.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@RestController
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String IMOOC = "imooc";

    @GetMapping(value = "/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session){
        session.setAttribute("username",username);
        return "登录成功";
    }

    @GetMapping(value = "/info")
    public String info(HttpSession session){
        return "当前登陆人是:"+session.getAttribute("username");
    }

    @GetMapping(value = "/loginWithToken")
    public String loginWithToken(@RequestParam String username,
                                 @RequestParam String password){
        //账号密码不正确
        String key = "token_" + UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(key,username,3600, TimeUnit.SECONDS);
        return key;
    }


    @GetMapping(value = "/infoWithToken")
    public String infoWithToken(String token){
        return "当前登录的是" + stringRedisTemplate.opsForValue().get(token);
    }

    @GetMapping(value = "/loginWithJwt")
    public String loginWithJwt(@RequestParam String username,
                               @RequestParam String password){
        //加密字段
        Algorithm algorithm = Algorithm.HMAC256(IMOOC);
        String token = JWT.create()
                .withClaim("login_user",username)
                .sign(algorithm);
        return token;
    }


    @GetMapping(value = "/infoWithJwt")
    public DecodedJWT infoWithJwt(String token){
        Algorithm algorithm = Algorithm.HMAC256(IMOOC);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }
}
