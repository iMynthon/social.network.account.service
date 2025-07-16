package social.network.account.config.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import social.network.account.filter.JwtAuthenticationFilter;
import social.network.account.openfeign.AccountFeignClient;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> tokenFilter(AccountFeignClient accountFeignClient) {
        JwtAuthenticationFilter bearerAuthFilter =
                new JwtAuthenticationFilter(accountFeignClient);
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(bearerAuthFilter);
        registration.addUrlPatterns("/api/v1/account","/api/v1/account/**");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security,
                                           JwtAuthenticationFilter tokenFilter) throws Exception {
        security.authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer<HttpSecurity>::disable)
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }
}
