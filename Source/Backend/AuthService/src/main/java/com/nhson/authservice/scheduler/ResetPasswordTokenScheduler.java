package com.nhson.authservice.scheduler;

import com.nhson.authservice.AppConfig;
import com.nhson.authservice.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@EnableScheduling
@Configuration
@Component
@Slf4j
public class ResetPasswordTokenScheduler {
    private final TaskScheduler taskScheduler;
    private final UserRepository userRepository;
    private final AppConfig appConfig;
    public ResetPasswordTokenScheduler(TaskScheduler taskScheduler, UserRepository userRepository, AppConfig appConfig) {
        this.taskScheduler = taskScheduler;
        this.userRepository = userRepository;
        this.appConfig = appConfig;

        this.taskScheduler.scheduleAtFixedRate(() -> {
            try {
                this.deleteExpiredToken();
            } catch (Exception e) {
                log.error("Error in scheduled task: {}", e.getMessage(), e);
            }
        }, Duration.ofMillis(appConfig.getDeleteExpiredRestPwToken()));
    }

    @Transactional
    public void deleteExpiredToken() {
        long currentUnixTime = System.currentTimeMillis() / 1000;
        try {
            int deletedCount = userRepository.deleteExpiredTokens(currentUnixTime);
            log.info("Deleted {} expired tokens.", deletedCount);
        } catch (Exception e) {
            log.error("Error while deleting expired tokens: {}", e.getMessage(), e);
        }
    }
}
