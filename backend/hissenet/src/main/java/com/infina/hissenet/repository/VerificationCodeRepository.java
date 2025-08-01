package com.infina.hissenet.repository;

import com.infina.hissenet.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND " +
            "v.used = false AND v.blocked = false AND v.expiresAt > :now " +
            "ORDER BY v.createdAt DESC LIMIT 1")
    Optional<VerificationCode> findActiveCodeByEmail(String email, LocalDateTime now);

    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.email = :email AND v.createdAt > :since")
    long countCodesSentSince(String email, LocalDateTime since);

    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.ipAddress = :ipAddress AND v.lastAttemptAt > :since")
    long countAttemptsByIpSince(String ipAddress, LocalDateTime since);

    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.email = :email AND " +
            "v.blocked = true AND v.blockedAt > :since")
    long countBlockedCodesByEmail(String email, LocalDateTime since);

    @Modifying
    @Query("UPDATE VerificationCode v SET v.used = true WHERE v.email = :email AND v.used = false")
    void invalidateAllCodesForEmail(String email);

    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.createdAt < :cutoffDate")
    void deleteExpiredCodes(LocalDateTime cutoffDate);

    @Modifying
    @Query("UPDATE VerificationCode v SET v.blocked = false, v.blockedAt = null WHERE v.blockedAt < :cutoffDate")
    void unblockOldCodes(LocalDateTime cutoffDate);
}