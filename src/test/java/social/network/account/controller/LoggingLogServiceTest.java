package social.network.account.controller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.network.account.model.log.Log;
import social.network.account.repository.LoggingLongRepository;
import social.network.account.service.LoggingLogService;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LoggingLogServiceTest {

    @Mock
    private LoggingLogService loggingLogService;

    @Mock
    private LoggingLongRepository loggingLongRepository;

    @Test
    void testSaveLongLog(){
        Log log = Log.builder()
                .createdAt(LocalDateTime.now())
                .taskName("task name")
                .executionTime(12.12)
                .build();
        doNothing().when(loggingLogService).createLogMessage(log.getExecutionTime(),log.getTaskName());
        loggingLogService.createLogMessage(log.getExecutionTime(),log.getTaskName());
        verify(loggingLogService,times(1)).createLogMessage(log.getExecutionTime(),log.getTaskName());
    }

    @Test
    void testSaveRepository(){
        Log log = Log.builder()
                .id(1)
                .createdAt(LocalDateTime.now())
                .taskName("task name")
                .executionTime(12.12)
                .build();
        when(loggingLongRepository.save(log)).thenReturn(log);
        Log log1 = loggingLongRepository.save(log);
        assertEquals(log,log1);
        verify(loggingLongRepository,times(1)).save(log);
    }

}
