package com.senawiki.auth.service;

import com.senawiki.auth.domain.EmailVerification;
import com.senawiki.auth.domain.EmailVerificationRepository;
import com.senawiki.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmailVerificationService {

    private static final int DAILY_SEND_LIMIT = 3;
    private static final int MAX_VERIFY_ATTEMPTS = 3;
    private static final Duration CODE_TTL = Duration.ofMinutes(5);

    private final EmailVerificationRepository repository;
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String brevoApiKey;
    private final String fromAddress;
    private final String fromName;
    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationService(
        EmailVerificationRepository repository,
        UserRepository userRepository,
        RestClient.Builder restClientBuilder,
        @Value("${app.brevo.api-key}") String brevoApiKey,
        @Value("${app.brevo.from}") String fromAddress,
        @Value("${app.brevo.from-name:SenaWiki}") String fromName
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.restClient = restClientBuilder.baseUrl("https://api.brevo.com").build();
        this.brevoApiKey = brevoApiKey;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
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
        verification.setFailedAttempts(0);
        repository.save(verification);

        sendEmail(email, code);
    }

    public void verifyCode(String email, String code) {
        EmailVerification verification = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid code"));

        if (verification.getFailedAttempts() >= MAX_VERIFY_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Verification attempts exceeded");
        }
        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Verification code expired");
        }
        if (!verification.getCode().equals(code)) {
            verification.setFailedAttempts(verification.getFailedAttempts() + 1);
            repository.save(verification);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid code");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(Instant.now());
        verification.setFailedAttempts(0);
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
        String subjectText = "[SenaWiki] 이메일 인증 코드 안내";
        String bodyText =
            "안녕하세요, SenaWiki입니다.\n\n"
                + "아래 인증 코드를 입력해주세요.\n"
                + "인증 코드: " + code + "\n\n"
                + "인증 코드는 5분 후 만료됩니다.";

        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Brevo API key not configured");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Brevo sender address not configured");
        }

        Map<String, Object> payload = Map.of(
            "sender", Map.of("email", fromAddress, "name", fromName),
            "to", List.of(Map.of("email", to)),
            "subject", subjectText,
            "textContent", bodyText
        );

        try {
            restClient.post()
                .uri("/v3/smtp/email")
                .header("api-key", brevoApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Failed to send verification email",
                ex
            );
        }
    }
}
