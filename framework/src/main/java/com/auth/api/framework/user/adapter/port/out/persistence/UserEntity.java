    package com.auth.api.framework.user.adapter.port.out.persistence;

    import com.auth.core.shared.enumerated.Roles;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "tb_users")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class UserEntity {

        @Id
        private String id;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(nullable = false, unique = true, length = 255)
        private String email;

        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Roles role;

        @Column(name = "is_verified", nullable = false)
        private boolean verified;

        @Column(name = "is_active", nullable = false)
        private boolean active;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;
    }