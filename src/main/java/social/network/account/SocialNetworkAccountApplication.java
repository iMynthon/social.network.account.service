package social.network.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SocialNetworkAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkAccountApplication.class, args);
	}

}
