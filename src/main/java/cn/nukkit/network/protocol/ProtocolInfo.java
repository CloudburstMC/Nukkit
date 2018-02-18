package cn.nukkit.network.protocol;

import com.google.common.primitives.Ints;

import java.util.List;

/**
 * author: MagicDroidX & iNevet
 * Nukkit Project
 */
public interface ProtocolInfo {

    /**
     * Actual Minecraft: PE protocol version
     */
    int CURRENT_PROTOCOL = Integer.valueOf("201"); //plugins can change it
    int MINIMUM_COMPATIBLE_PROTOCOL = Integer.parseInt("113");

    List<Integer> SUPPORTED_PROTOCOLS = Ints.asList(CURRENT_PROTOCOL);

    String MINECRAFT_VERSION = "v1.x.x";
    String MINECRAFT_VERSION_NETWORK = "1.2.10";

}
