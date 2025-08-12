package com.infina.hissenet.security.abstracts;

public interface IRedisTokenService {
    void saveSession(String sessionId, String token,long expirationSeconds);
    String getTokenBySessionId(String sessionId);
    void deleteSession(String sessionId);
    void extendSession(String sessionId, long extensionSeconds);

}
