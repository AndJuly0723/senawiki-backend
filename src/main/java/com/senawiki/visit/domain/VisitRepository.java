package com.senawiki.visit.domain;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    long countByVisitDate(LocalDate visitDate);

    @Query("select count(distinct v.visitorKey) from Visit v")
    long countDistinctVisitorKey();

    @Modifying
    @Transactional
    @Query(
        value = """
            insert into visits (visitor_key, visit_date, created_at)
            values (:visitorKey, :visitDate, now())
            on conflict (visit_date, visitor_key) do nothing
            """,
        nativeQuery = true
    )
    void insertIfNotExists(
        @Param("visitorKey") String visitorKey,
        @Param("visitDate") LocalDate visitDate
    );
}
