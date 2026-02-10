package com.senawiki.admin.service;

import com.senawiki.admin.api.dto.AdminStatsResponse;
import com.senawiki.community.domain.CommunityPostRepository;
import com.senawiki.user.domain.UserRepository;
import com.senawiki.visit.domain.VisitRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminStatsService {

    private final UserRepository userRepository;
    private final CommunityPostRepository communityPostRepository;
    private final VisitRepository visitRepository;

    public AdminStatsService(
        UserRepository userRepository,
        CommunityPostRepository communityPostRepository,
        VisitRepository visitRepository
    ) {
        this.userRepository = userRepository;
        this.communityPostRepository = communityPostRepository;
        this.visitRepository = visitRepository;
    }

    public AdminStatsResponse getStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        AdminStatsResponse response = new AdminStatsResponse();
        response.setTotalUsers(userRepository.count());
        response.setNewUsersToday(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        response.setTotalVisitors(visitRepository.countDistinctVisitorKey());
        response.setDailyVisitors(visitRepository.countByVisitDate(LocalDate.now()));
        response.setTotalPosts(communityPostRepository.count());
        response.setNewPostsToday(communityPostRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        response.setTotalUploads(communityPostRepository.countByFileStoragePathIsNotNull());
        response.setNewUploadsToday(
            communityPostRepository.countByFileStoragePathIsNotNullAndCreatedAtBetween(startOfDay, endOfDay)
        );
        return response;
    }
}
