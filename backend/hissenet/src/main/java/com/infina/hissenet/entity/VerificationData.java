package com.infina.hissenet.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * Redis verification code data model
 * Used for temporary storage of verification codes with security controls
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
public class VerificationData {

    private String email;
    private String code;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    private boolean used = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedAt;

    private String ipAddress;
    private int attemptCount = 0;
    private int maxAttempts = 3;
    private boolean blocked = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime blockedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAttemptAt;


    public VerificationData() {}

    public VerificationData(String email, String code, String description,
                            LocalDateTime expiresAt, int maxAttempts) {
        this.email = email;
        this.code = code;
        this.description = description;
        this.expiresAt = expiresAt;
        this.maxAttempts = maxAttempts;
    }


    public void incrementAttempt(String ipAddress) {
        this.attemptCount++;
        this.lastAttemptAt = LocalDateTime.now();
        this.ipAddress = ipAddress;

        if (this.attemptCount >= this.maxAttempts) {
            this.blocked = true;
            this.blockedAt = LocalDateTime.now();
        }
    }

    public void markAsUsed(String ipAddress) {
        this.used = true;
        this.usedAt = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }

    @JsonIgnore
    public int getRemainingAttempts() {
        return Math.max(0, maxAttempts - attemptCount);
    }

    @JsonIgnore
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
    @JsonIgnore
    public boolean isUsable() {
        return !used && !blocked && !isExpired();
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public LocalDateTime getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(LocalDateTime lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    @Override
    public String toString() {
        return "VerificationData{" +
                "email='" + email + '\'' +
                ", used=" + used +
                ", blocked=" + blocked +
                ", attemptCount=" + attemptCount +
                ", maxAttempts=" + maxAttempts +
                ", expiresAt=" + expiresAt +
                '}';
    }
}