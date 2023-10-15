package com.no5619.umirecorder.security.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthenticatedUser extends UsernamePasswordAuthenticationToken {
    private String sessionId = null;
    private Date loginTime = null;
    //當session用
    private Map<String, Object> sessionData = new HashMap<>();

    public AuthenticatedUser(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticatedUser(Object principal, Object credentials, String sessionId) {
        super(principal, credentials);
        this.sessionId = sessionId;
        this.loginTime = new Date();
    }

    public AuthenticatedUser(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public String getSessionId() {
        return this.sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLoginTime() {
        return this.loginTime;
    }
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public void addData(String key, Object data) {
        this.sessionData.put(key, data);
    }
    public Object getData(String key) {
        return this.sessionData.get(key);
    }
}
