package com.infina.hissenet.repository;

import com.infina.hissenet.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for VerificationCode entity operations.
 * Handles email verification codes and security measures.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * Finds the latest active verification code for an email.
     *
     * @param email the email address
     * @param now current timestamp for expiration check
     * @return optional containing active code if exists
     */
    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND " +
            "v.used = false AND v.blocked = false AND v.expiresAt > :now " +
            "ORDER BY v.createdAt DESC LIMIT 1")
    Optional<VerificationCode> findActiveCodeByEmail(String email, LocalDateTime now);


    /**
     * Counts verification codes sent to an email since a specific time.
     *
     * @param email the email address
     * @param since the timestamp to count from
     * @return number of codes sent since the specified time
     */
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.email = :email AND v.createdAt > :since")
    long countCodesSentSince(String email, LocalDateTime since);


    /**
     * Counts verification attempts from an IP address since a specific time.
     *
     * @param ipAddress the IP address
     * @param since the timestamp to count from
     * @return number of attempts from the IP since specified time
     */
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.ipAddress = :ipAddress AND v.lastAttemptAt > :since")
    long countAttemptsByIpSince(String ipAddress, LocalDateTime since);


    /**
     * Counts blocked codes for an email since a specific time.
     *
     * @param email the email address
     * @param since the timestamp to count from
     * @return number of blocked codes since specified time
     */
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.email = :email AND " +
            "v.blocked = true AND v.blockedAt > :since")
    long countBlockedCodesByEmail(String email, LocalDateTime since);


    /**
     * Marks all unused codes for an email as used.
     *
     * @param email the email address
     */
    @Modifying
    @Query("UPDATE VerificationCode v SET v.used = true WHERE v.email = :email AND v.used = false")
    void invalidateAllCodesForEmail(String email);


    /**
     * Deletes expired verification codes.
     *
     * @param cutoffDate the cutoff date for deletion
     */
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.createdAt < :cutoffDate")
    void deleteExpiredCodes(LocalDateTime cutoffDate);


    /**
     * Unblocks old blocked verification codes.
     *
     * @param cutoffDate the cutoff date for unblocking
     */
    @Modifying
    @Query("UPDATE VerificationCode v SET v.blocked = false, v.blockedAt = null WHERE v.blockedAt < :cutoffDate")
    void unblockOldCodes(LocalDateTime cutoffDate);
}