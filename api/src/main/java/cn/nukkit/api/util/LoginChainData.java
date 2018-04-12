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

package cn.nukkit.api.util;

import java.util.UUID;

/**
 * @author CreeperFace
 */
public interface LoginChainData {

    String getUsername();

    UUID getClientUUID();

    String getIdentityPublicKey();

    long getClientId();

    String getServerAddress();

    String getDeviceModel();

    DeviceOS getDeviceOS();

    String getGameVersion();

    int getGuiScale();

    String getLanguageCode();

    String getXUID();

    InputMode getCurrentInputMode();

    InputMode getDefaultInputMode();

    String getCapeData();

    UIProfile getUIProfile();

    enum DeviceOS {
        UNKNOWN,
        ANDROID,
        IOS,
        OSX,
        FIREOS,
        GEARVR,
        HOLOLENS,
        WIN10,
        WIN32,
        DEDICATED,
        ORBIS,
        NX
    }

    enum UIProfile {
        CLASSIC,
        POCKET
    }

    enum InputMode {
        UNKNOWN, //0 - unknown
        KEYBOARD_MOUSE,
        TOUCH,
    }
}
