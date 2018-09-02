package cn.nukkit.utils;

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

    int getDeviceOS();

    String getDeviceId();

    String getGameVersion();

    int getGuiScale();

    String getLanguageCode();

    String getXUID();

    boolean isXboxAuthed();

    int getCurrentInputMode();

    int getDefaultInputMode();

    String getCapeData();

    int getUIProfile();
}
