package com.thymeleaf.service;

import com.thymeleaf.repository.ITokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Component
@Slf4j
@RequiredArgsConstructor
public class PollingService {

    private final ITokenRepository tokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void delete() {
        log.info("Deleting expired tokens");
        tokenRepository.deleteByRefreshTokenExpirationDateBefore(new Timestamp(System.currentTimeMillis()));
    }

}
