package com.infina.hissenet.constants;

public final class VerificationConstants {

    private VerificationConstants() {}

    /**
     * Redis key patterns for verification operations
     */
    public static final class RedisKeys {
        private RedisKeys() {}

        public static final String VERIFICATION_CODE_PATTERN = "verification:code:%s";
        public static final String RATE_LIMIT_IP_PATTERN = "rate_limit:ip:%s";
        public static final String RATE_LIMIT_EMAIL_PATTERN = "rate_limit:email:%s";

        public static final String PASSWORD_CHANGE_TOKEN_PATTERN = "password_change_token:%s";
    }

}