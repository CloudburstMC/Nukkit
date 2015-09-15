package cn.nukkit.network;

import cn.nukkit.network.protocol.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Network {
    public static int BATCH_THRESHOLD = 512;

    private double upload = 0;
    private double download = 0;

    public void addStatistics(double upload, double download) {
        this.upload += upload;
        this.download += download;
    }

    public DataPacket getPacket(byte id) {

        return null;
    }
}
