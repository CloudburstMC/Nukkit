package com.nukkitx.server.network.bedrock.session.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nukkitx.protocol.bedrock.session.data.AuthData;
import lombok.Data;

import java.util.UUID;

@Data
public class AuthDataImpl implements AuthData {
    @JsonProperty
    private String displayName;
    @JsonProperty
    private UUID identity;
    @JsonProperty("XUID")
    private String xuid;
}
