package cn.nukkit.server.network.minecraft.session.data;

import cn.nukkit.api.util.data.DeviceOS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
public class ClientData {
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
    private final int deviceOs;
    @JsonProperty("GameVersion")
    private final String gameVersion;
    @JsonProperty("GuiScale")
    private final int guiScale;
    @JsonProperty("IsEduMode")
    private final boolean EducationMode;
    @JsonProperty("LanguageCode")
    private final String languageCode;
    @JsonProperty("ServerAddress")
    private final String serverAddress;
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

    @NonFinal private transient DeviceOS deviceOS;

    public DeviceOS getDeviceOs() {
        if (deviceOS == null) {
            deviceOS = DeviceOS.values()[deviceOs];
        }
        return deviceOS;
    }
}
