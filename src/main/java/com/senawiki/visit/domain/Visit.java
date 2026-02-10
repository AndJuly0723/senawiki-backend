package com.senawiki.visit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "visits",
    uniqueConstraints = @UniqueConstraint(columnNames = {"visit_date", "visitor_key"})
)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_key", nullable = false, length = 64)
    private String visitorKey;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getVisitorKey() {
        return visitorKey;
    }

    public void setVisitorKey(String visitorKey) {
        this.visitorKey = visitorKey;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
