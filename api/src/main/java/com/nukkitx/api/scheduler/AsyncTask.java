package com.nukkitx.api.scheduler;

import com.nukkitx.api.Server;

/**
 * @author CreeperFace
 */
public interface AsyncTask extends Runnable {

    void onCompletion(Server server);
}
