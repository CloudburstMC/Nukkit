package cn.nukkit.server.network.minecraft.session.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AuthData {
    @JsonProperty
    private String displayName;
    @JsonProperty
    private UUID identity;
    @JsonProperty("XUID")
    private String xuid;
    private UUID offlineIdentity;
}
