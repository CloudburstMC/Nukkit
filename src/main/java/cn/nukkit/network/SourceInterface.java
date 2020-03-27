package cn.nukkit.network;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface SourceInterface {

    void setName(String name);

    boolean process();

    void shutdown();

    void emergencyShutdown();
}
