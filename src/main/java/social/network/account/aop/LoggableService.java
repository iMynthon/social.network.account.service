package social.network.account.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import social.network.account.service.LoggingLogService;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class LoggableService {

    private final LoggingLogService service;

    public LoggableService(LoggingLogService service) {
        this.service = service;
    }

    @Around("@within(social.network.account.aop.Loggable)")
    public Object loggingServiceClass(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getDeclaringClass().getSimpleName() + " - " + signature.getName();
        log.info("before service {}, args=[{}]", methodName, joinPoint.getArgs());
        StopWatch time = new StopWatch();
        time.start();
        Object result = joinPoint.proceed();
        time.stop();
        String stopWatchOutput = String.format("method %s executed in %f seconds", methodName,
                time.getTotalTimeSeconds());
        log.info("Execution metrics service: {}", stopWatchOutput);
        Duration duration = Duration.ofMillis(time.getTotalTimeMillis());
        if (duration.toMinutes() > 2L) {
            service.createLogMessage(time.getTotalTimeSeconds(), methodName);
        }
        log.info("After service: - {}",result == null ? "void" : result);
        return result;
    }
}
