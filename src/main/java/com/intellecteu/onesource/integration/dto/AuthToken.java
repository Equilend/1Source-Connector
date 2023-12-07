package com.intellecteu.onesource.integration.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken {

    private String accessToken;
    private long expiresIn;
    @NonNull
    private LocalDateTime expiresAt;
    private String refreshToken;
    private long refreshExpiresIn;
    @NonNull
    private LocalDateTime refreshExpiresAt;
    private String tokenType;

    public boolean isAccessTokenExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isRefreshTokenExpired() {
        return refreshExpiresAt.isBefore(LocalDateTime.now());
    }
}
