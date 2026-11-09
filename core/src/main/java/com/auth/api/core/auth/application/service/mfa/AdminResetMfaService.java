package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.AdminMfaResetResponse;
import com.auth.api.core.auth.application.port.in.mfa.AdminResetMfaUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;
import lombok.extern.slf4j.Slf4j;

import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

/**
 * Reset de MFA feito por um ADMIN para quem perdeu o celular E os códigos de recuperação.
 *
 * A verificação de identidade do usuário (documento, videochamada etc.) é um processo de
 * suporte fora da API; este caso de uso só executa o reset depois dessa verificação.
 *
 * Efeitos: mfaStatus -> PENDING, apaga segredo TOTP, códigos de recuperação e desafios
 * pendentes, revoga todas as sessões e avisa o usuário por e-mail.
 */
@Slf4j
public class AdminResetMfaService implements AdminResetMfaUseCase {

    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final UserTotpPersistencePort userTotpPersistencePort;
    private final RecoveryCodePersistencePort recoveryCodePersistencePort;
    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public AdminResetMfaService(
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            UserTotpPersistencePort userTotpPersistencePort,
            RecoveryCodePersistencePort recoveryCodePersistencePort,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            RefreshTokenPersistencePort refreshTokenPersistencePort,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.userTotpPersistencePort = userTotpPersistencePort;
        this.recoveryCodePersistencePort = recoveryCodePersistencePort;
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.refreshTokenPersistencePort = refreshTokenPersistencePort;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public AdminMfaResetResponse execute(String adminUserId, String targetUserId, String reason) {
        requireNotBlank(adminUserId, "Admin is required");
        requireNotBlank(targetUserId, "User id is required");
        requireNotBlank(reason, "Reason is required");

        // Evita que uma sessão de admin comprometida remova o próprio segundo fator.
        if (adminUserId.equals(targetUserId)) {
            throw new BusinessException("Admins cannot reset their own MFA. Ask another admin");
        }

        UserDomain user = userAuthenticationPersistencePort.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserDomain resetUser = user.resetMfa();

        int revokedSessions = transactionManager.execute(() -> {
            userAuthenticationPersistencePort.save(resetUser);
            userTotpPersistencePort.deleteByUserId(user.id());
            recoveryCodePersistencePort.deleteByUserId(user.id());
            mfaChallengePersistencePort.deleteByUserId(user.id());
            return refreshTokenPersistencePort.revokeAllActiveByUserId(user.id());
        });

        // Trilha de auditoria: quem resetou, de quem e por quê.
        log.warn("AUDIT mfa_reset admin={} user={} revokedSessions={} reason=\"{}\"",
                adminUserId, user.id(), revokedSessions, reason.replaceAll("[\\r\\n]", " "));

        emailPort.send(
                new EmailMessage(
                        user.email(),
                        "Your two-factor authentication was reset",
                        emailTemplateUseCase.buildMfaResetEmail(user.name())
                )
        );

        return new AdminMfaResetResponse(user.id(), resetUser.mfaStatus(), revokedSessions);
    }
}
