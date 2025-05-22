package com.javatechie.filter;

import com.javatechie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    //    @Autowired
//    private RestTemplate template;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    // Gateway fileter
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
//                    //REST call to AUTH service
//                    template.getForObject("http://IDENTITY-SERVICE//validate?token" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);
                    String role = jwtUtil.extractRole(authHeader);
                    String path = exchange.getRequest().getURI().getPath();

                    // 💥 Allow only ADMIN to access /admin routes
                    if (path.contains("/admin4") && !role.equals("ADMIN")) {
                        throw new RuntimeException("Access denied: Only ADMIN can access this route.");
                    }
                    if (path.contains("/admin2") && !role.equals("ADMIN")) {
                        throw new RuntimeException("Access denied: Only ADMIN can access this route.");
                    }
                    if (path.contains("/admin3") && !role.equals("ADMIN")) {
                        throw new RuntimeException("Access denied: Only ADMIN can access this route.");
                    }
                    if (path.contains("/admin1") && !role.equals("ADMIN")) {
                        throw new RuntimeException("Access denied: Only ADMIN can access this route.");
                    }


                } catch (Exception e) {
                    System.out.println("invalid access...!");
                    throw new RuntimeException("un authorized access to application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
