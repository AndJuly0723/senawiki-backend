package com.senawiki.auth.service;

import com.senawiki.auth.domain.EmailVerification;
import com.senawiki.auth.domain.EmailVerificationRepository;
import com.senawiki.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

@Service
@Transactional
public class EmailVerificationService {

    private static final int DAILY_SEND_LIMIT = 3;
    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    private final EmailVerificationRepository repository;
    private final UserRepository userRepository;
    private final SesClient sesClient;
    private final String fromAddress;
    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationService(
        EmailVerificationRepository repository,
        UserRepository userRepository,
        SesClient sesClient,
        @Value("${app.ses.from}") String fromAddress
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.sesClient = sesClient;
        this.fromAddress = fromAddress;
    }

    public void sendCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        EmailVerification verification = repository.findByEmail(email).orElseGet(() -> {
            EmailVerification created = new EmailVerification();
            created.setEmail(email);
            created.setDailySendCount(0);
            created.setLastSentDate(LocalDate.now());
            return created;
        });

        LocalDate today = LocalDate.now();
        if (today.equals(verification.getLastSentDate())) {
            if (verification.getDailySendCount() >= DAILY_SEND_LIMIT) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily send limit reached");
            }
            verification.setDailySendCount(verification.getDailySendCount() + 1);
        } else {
            verification.setLastSentDate(today);
            verification.setDailySendCount(1);
        }

        String code = generateCode();
        verification.setCode(code);
        verification.setExpiresAt(Instant.now().plus(CODE_TTL));
        verification.setVerified(false);
        verification.setVerifiedAt(null);
        repository.save(verification);

        sendEmail(email, code);
    }

    public void verifyCode(String email, String code) {
        EmailVerification verification = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid code"));

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verification code expired");
        }
        if (!verification.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid code");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(Instant.now());
        repository.save(verification);
    }

    public void requireVerified(String email) {
        EmailVerification verification = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified"));
        if (!verification.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified");
        }
    }

    public void consumeVerification(String email) {
        repository.deleteByEmail(email);
    }

    private String generateCode() {
        int value = secureRandom.nextInt(1_000_000);
        return String.format("%06d", value);
    }

    private void sendEmail(String to, String code) {
        String subjectText = "SenaWiki 이메일 인증 코드";
        String bodyText = "인증 코드는 다음과 같습니다: " + code + "\n5분 이내에 입력해주세요.";

        Destination destination = Destination.builder()
            .toAddresses(to)
            .build();

        Message message = Message.builder()
            .subject(Content.builder().data(subjectText).build())
            .body(Body.builder().text(Content.builder().data(bodyText).build()).build())
            .build();

        SendEmailRequest request = SendEmailRequest.builder()
            .source(fromAddress)
            .destination(destination)
            .message(message)
            .build();

        sesClient.sendEmail(request);
    }
}
