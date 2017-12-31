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
