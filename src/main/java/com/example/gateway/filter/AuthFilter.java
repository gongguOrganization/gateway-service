package com.example.gateway.filter;

import com.example.gateway.config.SecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import javax.xml.bind.DatatypeConverter;

@Component
@Slf4j
public class AuthFilter  extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    public AuthFilter(){
        super((AuthFilter.Config.class));
    }
    @Autowired
    SecurityService securityService;

    @Value("${jwt.secret_key}")
    String SECRET_KEY;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "no header", HttpStatus.UNAUTHORIZED);
            }
            String token = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (!isJwtValid(token)) {
                return onError(exchange, "jwt valid error", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }
    private boolean isJwtValid(String token){
        boolean returnvalue = true;
        String subject = null;
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            subject = claims.getSubject();
        }catch (Exception e){
            returnvalue = false;
        }
        if(subject==null || subject.isEmpty()){
            returnvalue = false;
        }
        return returnvalue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    public class Config{

    }
}
