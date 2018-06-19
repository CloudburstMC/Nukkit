package com.nukkitx.server.network.bedrock.session.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nukkitx.api.util.data.DeviceOS;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientData {
    @JsonProperty("TenantId")
    private final String tenantId;
    @JsonProperty("ADRole")
    private final String activeDirectoryRole;
    @JsonProperty("CapeData")
    private final byte[] capeData; // deserialized
    @JsonProperty("ClientRandomId")
    private final long clientRandomId;
    @JsonProperty("CurrentInputMode")
    private final int currentInputMode;
    @JsonProperty("DefaultInputMode")
    private final int defaultInputMode;
    @JsonProperty("DeviceModel")
    private final String deviceModel;
    @JsonProperty("DeviceOS")
    private final DeviceOS deviceOs;
    @JsonProperty("GameVersion")
    private final String gameVersion;
    @JsonProperty("GuiScale")
    private final int guiScale;
    @JsonProperty("IsEduMode")
    private final boolean EducationMode;
    @JsonProperty("LanguageCode")
    private final String languageCode;
    @JsonProperty("ThirdPartyName")
    private final String thirdPartyName;
    @JsonProperty("PlatformChatId")
    private final String platformChatId;
    @JsonProperty("ServerAddress")
    private final String serverAddress;
    @JsonProperty("SkinDataLength")
    private final int skinDataLength;
    @JsonProperty("SkinData")
    private final byte[] skinData; // deserialized
    @JsonProperty("SkinGeometry")
    private final byte[] skinGeometry; // deserialized
    @JsonProperty("SkinGeometryName")
    private final String skinGeometryName;
    @JsonProperty("SkinId")
    private final String skinId;
    @JsonProperty("UIProfile")
    private final int uiProfile;
}
