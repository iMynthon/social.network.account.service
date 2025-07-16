package social.network.account.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomRetryer implements Retryer {

    private int attempt = 1;
    private final int maxAttempt = 5;


    @Override
    public void continueOrPropagate(RetryableException e) {
        if(attempt++ >= maxAttempt){
            throw e;
        }
        try {
            sleep();
        } catch (InterruptedException ex) {
            log.info("Ошибка при остановке потока");
            Thread.currentThread().interrupt();
        }
    }

    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }



    @Override
    public Retryer clone() {
        return new CustomRetryer();
    }
}
