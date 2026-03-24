package cn.nukkit.network.auth;

import lombok.Getter;

import java.util.Objects;

public class TokenPayload implements AuthPayload {

    @Getter
    private final String token;
    private final AuthType type;

    public TokenPayload(String token, AuthType type) {
        if (type == AuthType.UNKNOWN) {
            throw new IllegalArgumentException("TokenPayload cannot be of type UNKNOWN");
        }

        this.token = token;
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public AuthType getAuthType() {
        return type;
    }
}
