package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;

/**
 * Authentication contract for login/logout operations.
 *
 * @author Furkan Can
 */
public interface IAuthService {

    /**
     * Authenticates the user and issues a session with JWT and TTL.
     *
     * @param request login credentials
     * @return auth response with user info and session metadata
     */
    AuthResponse login(LoginRequest request);

    /**
     * Revokes the session identified by the session id.
     *
     * @param sessionId server-side session identifier
     * @return message
     */
    String logout(String sessionId);
}
