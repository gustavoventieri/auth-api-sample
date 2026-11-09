package com.auth.api.framework.auth.adapter.port.out.persistence.entity;

import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_totp_secrets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTotpEntity {

    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "encrypted_secret", nullable = false, length = 255)
    private String encryptedSecret;

    @Column(name = "last_used_time_step")
    private Long lastUsedTimeStep;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Lock otimista: UPDATE ... WHERE version = ?; requisições concorrentes sobre o mesmo
    // registro falham com OptimisticLockingFailureException em vez de passarem as duas.
    @Version
    @ColumnDefault("0")
    private Long version;
}
