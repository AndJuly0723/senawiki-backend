package com.senawiki.auth.service;

import com.senawiki.auth.domain.RefreshToken;
import com.senawiki.auth.domain.RefreshTokenRepository;
import com.senawiki.auth.dto.AuthResponse;
import com.senawiki.auth.dto.LoginRequest;
import com.senawiki.auth.dto.LogoutRequest;
import com.senawiki.auth.dto.RefreshRequest;
import com.senawiki.auth.dto.RegisterRequest;
import com.senawiki.auth.dto.UserResponse;
import com.senawiki.auth.dto.WithdrawRequest;
import com.senawiki.guide.domain.GuideDeck;
import com.senawiki.guide.domain.GuideDeckHeroEquipmentRepository;
import com.senawiki.guide.domain.GuideDeckRepository;
import com.senawiki.guide.domain.GuideDeckSkillOrderRepository;
import com.senawiki.guide.domain.GuideDeckSlotRepository;
import com.senawiki.guide.domain.GuideDeckTeam;
import com.senawiki.guide.domain.GuideDeckTeamRepository;
import com.senawiki.guide.domain.GuideDeckVote;
import com.senawiki.guide.domain.GuideDeckVoteRepository;
import com.senawiki.guide.domain.GuideDeckVoteType;
import com.senawiki.security.JwtTokenProvider;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailVerificationService;
    private final GuideDeckRepository guideDeckRepository;
    private final GuideDeckTeamRepository guideDeckTeamRepository;
    private final GuideDeckSlotRepository guideDeckSlotRepository;
    private final GuideDeckSkillOrderRepository guideDeckSkillOrderRepository;
    private final GuideDeckHeroEquipmentRepository guideDeckHeroEquipmentRepository;
    private final GuideDeckVoteRepository guideDeckVoteRepository;

    public AuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider,
        EmailVerificationService emailVerificationService,
        GuideDeckRepository guideDeckRepository,
        GuideDeckTeamRepository guideDeckTeamRepository,
        GuideDeckSlotRepository guideDeckSlotRepository,
        GuideDeckSkillOrderRepository guideDeckSkillOrderRepository,
        GuideDeckHeroEquipmentRepository guideDeckHeroEquipmentRepository,
        GuideDeckVoteRepository guideDeckVoteRepository
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailVerificationService = emailVerificationService;
        this.guideDeckRepository = guideDeckRepository;
        this.guideDeckTeamRepository = guideDeckTeamRepository;
        this.guideDeckSlotRepository = guideDeckSlotRepository;
        this.guideDeckSkillOrderRepository = guideDeckSkillOrderRepository;
        this.guideDeckHeroEquipmentRepository = guideDeckHeroEquipmentRepository;
        this.guideDeckVoteRepository = guideDeckVoteRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        emailVerificationService.requireVerified(request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        emailVerificationService.consumeVerification(request.getEmail());
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (!jwtTokenProvider.validateToken(storedToken.getToken())) {
            refreshTokenRepository.delete(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);

        return issueTokens(user);
    }

    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
            .ifPresent(refreshTokenRepository::delete);
    }

    public void withdraw(WithdrawRequest request) {
        User user = requireUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        refreshTokenRepository.deleteByUser(user);
        deleteUserVotes(user.getId());
        deleteUserGuideDecks(user.getId());
        userRepository.delete(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(jwtTokenProvider.getRefreshExpiryInstant());
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
            accessToken,
            refreshTokenValue,
            jwtTokenProvider.getAccessTokenValiditySeconds(),
            new UserResponse(user)
        );
    }

    private User requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void deleteUserVotes(Long userId) {
        List<GuideDeckVote> votes = guideDeckVoteRepository.findByUserId(userId);
        if (votes.isEmpty()) {
            return;
        }
        for (GuideDeckVote vote : votes) {
            Long deckId = vote.getDeck().getId();
            if (vote.getVoteType() == GuideDeckVoteType.UP) {
                guideDeckRepository.decrementUpVotes(deckId);
            } else {
                guideDeckRepository.decrementDownVotes(deckId);
            }
        }
        guideDeckVoteRepository.deleteByUserId(userId);
    }

    private void deleteUserGuideDecks(Long userId) {
        List<GuideDeck> decks = guideDeckRepository.findByAuthorUserId(userId);
        if (decks.isEmpty()) {
            return;
        }
        List<Long> deckIds = decks.stream()
            .map(GuideDeck::getId)
            .toList();
        List<GuideDeckTeam> teams = guideDeckTeamRepository.findByDeckIdIn(deckIds);
        if (!teams.isEmpty()) {
            List<Long> teamIds = teams.stream()
                .map(GuideDeckTeam::getId)
                .toList();
            guideDeckHeroEquipmentRepository.deleteByTeamIdIn(teamIds);
            guideDeckSkillOrderRepository.deleteByTeamIdIn(teamIds);
            guideDeckSlotRepository.deleteByTeamIdIn(teamIds);
            guideDeckTeamRepository.deleteAll(teams);
        }
        guideDeckRepository.deleteAll(decks);
    }
}
