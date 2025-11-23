package com.health_fitness.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.health_fitness.config.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User createdBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User lastModifiedBy;

    @JsonIgnore
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @JsonIgnore
    private LocalDateTime lastModifiedDate;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();

        // TODO: gán user thật (hiện tạm dùng "system")
        if (this.createdBy == null) {
            this.createdBy = getCurrentUser();
        }
        this.lastModifiedBy = this.createdBy;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = getCurrentUser();
    }

    /**
     * Lấy user hiện đang login.
     * Có thể thay thế bằng SecurityContextHolder hoặc ThreadLocal tùy hệ thống.
     */
    protected User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}

