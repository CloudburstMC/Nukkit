package cn.nukkit.api.scheduler;

import cn.nukkit.api.Server;

/**
 * @author CreeperFace
 */
public interface AsyncTask extends Runnable {

    void onCompletion(Server server);
}
