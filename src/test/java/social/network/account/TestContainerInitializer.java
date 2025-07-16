package social.network.account;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine");
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    static {
        postgres.start();
        kafkaContainer.start();
    }

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url" + postgres.getJdbcUrl(),
                "spring.datasource.username" + postgres.getUsername(),
                "spring.datasource.password"+ postgres.getPassword(),
                "spring.kafka.bootstrap-servers" + kafkaContainer.getBootstrapServers());
    }

}
