/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.minecraft.session.data;

import cn.nukkit.api.util.data.DeviceOS;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
