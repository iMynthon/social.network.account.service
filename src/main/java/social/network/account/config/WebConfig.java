package social.network.account.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import social.network.account.service.AccountIsOnlineStatusService;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AccountIsOnlineStatusService accountIsOnlineStatusService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accountIsOnlineStatusService)
                .addPathPatterns("/api/**")
                .order(1);
    }
}
