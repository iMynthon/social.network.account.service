package social.network.account.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import social.network.account.model.log.Log;
import social.network.account.repository.LoggingLongRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggingLogService {

    private final LoggingLongRepository repository;

    @Async("taskExecutor")
    public void createLogMessage(double executionTime,String methodName){
        log.info("Сохранение данных о долгой операции");
        repository.save(Log.builder()
                        .taskName(methodName)
                        .executionTime(executionTime)
                .build());
    }
}
