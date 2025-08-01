package com.infina.hissenet.repository;

import com.infina.hissenet.entity.VerificationCode;
import com.infina.hissenet.entity.enums.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * Retrieves a list of valid (unused and unexpired) verification codes for a given email and type.
     *
     * @param email the email address
     * @param type the type of email verification
     * @param now the current time for expiration comparison
     * @return a list of valid verification codes ordered by creation date descending
     */
    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND v.type = :type AND v.used = false AND v.expiresAt > :now ORDER BY v.createdAt DESC")
    List<VerificationCode> findValidCodesByEmailAndType(@Param("email") String email,
                                                        @Param("type") EmailType type,
                                                        @Param("now") LocalDateTime now);


    /**
     * Retrieves a valid verification code by email, code, and type, ensuring it is not used, blocked, or expired.
     *
     * @param email the email address
     * @param code the verification code
     * @param type the type of email verification
     * @param now the current time for expiration comparison
     * @return an optional containing the valid verification code, if found
     */
    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND v.code = :code AND v.type = :type AND v.used = false AND v.blocked = false AND v.expiresAt > :now")
    Optional<VerificationCode> findValidCode(@Param("email") String email,
                                             @Param("code") String code,
                                             @Param("type") EmailType type,
                                             @Param("now") LocalDateTime now);


    /**
     * Retrieves the most recent active verification code for the specified email and type.
     *
     * @param email the email address
     * @param type the type of email verification
     * @param now the current time for expiration comparison
     * @return an optional containing the active verification code, if found
     */
    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND v.type = :type AND v.used = false AND v.blocked = false AND v.expiresAt > :now ORDER BY v.createdAt DESC")
    Optional<VerificationCode> findActiveCodeByEmailAndType(@Param("email") String email,
                                                            @Param("type") EmailType type,
                                                            @Param("now") LocalDateTime now);

    /**
     * Deletes all verification codes that have expired before the specified time.
     *
     * @param expiredBefore the cutoff time for expiration
     */
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < :expiredBefore")
    void deleteExpiredCodes(@Param("expiredBefore") LocalDateTime expiredBefore);


    /**
     * Marks all unused verification codes for a given email and type as used.
     *
     * @param email the email address
     * @param type the type of email verification
     */
    @Modifying
    @Query("UPDATE VerificationCode v SET v.used = true WHERE v.email = :email AND v.type = :type AND v.used = false")
    void invalidateAllCodesForEmailAndType(@Param("email") String email, @Param("type") EmailType type);


    /**
     * Counts the number of verification codes sent to a given email address since the specified time.
     *
     * @param email the email address
     * @param since the time to start counting from
     * @return the number of codes sent
     */
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.email = :email AND v.createdAt > :since")
    long countCodesSentSince(@Param("email") String email, @Param("since") LocalDateTime since);


    /**
     * Resets blocked verification codes that were blocked before the specified time.
     *
     * @param unblockBefore the cutoff time for unblocking
     */
    @Modifying
    @Query("UPDATE VerificationCode v SET v.blocked = false, v.attemptCount = 0, v.blockedAt = null WHERE v.blockedAt < :unblockBefore")
    void unblockOldCodes(@Param("unblockBefore") LocalDateTime unblockBefore);


    /**
     * Retrieves the attempt count of the most recently created valid verification code for the given email and type.
     *
     * @param email the email address
     * @param type the type of email verification
     * @param now the current time for expiration comparison
     * @return an optional containing the last attempt count
     */
    @Query("SELECT v.attemptCount FROM VerificationCode v WHERE v.email = :email AND v.type = :type AND v.used = false AND v.expiresAt > :now ORDER BY v.createdAt DESC")
    Optional<Integer> getLastAttemptCount(@Param("email") String email,
                                          @Param("type") EmailType type,
                                          @Param("now") LocalDateTime now);

    /**
     * Counts the number of verification code attempts made from a specific IP address since the given time.
     *
     * @param ipAddress the IP address
     * @param since the time to start counting from
     * @return the number of attempts
     */
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.ipAddress = :ipAddress AND v.lastAttemptAt > :since")
    long countAttemptsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);


    /**
     * Checks whether there is any blocked (but not expired) verification code for a given email and type.
     *
     * @param email the email address
     * @param type the type of email verification
     * @param now the current time for expiration comparison
     * @return true if at least one blocked code exists; false otherwise
     */
    @Query("SELECT COUNT(v) > 0 FROM VerificationCode v WHERE v.email = :email AND v.type = :type AND v.blocked = true AND v.expiresAt > :now")
    boolean hasBlockedCodeForEmailAndType(@Param("email") String email,
                                          @Param("type") EmailType type,
                                          @Param("now") LocalDateTime now);
}