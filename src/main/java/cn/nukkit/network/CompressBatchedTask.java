package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Zlib;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CompressBatchedTask extends AsyncTask {

    public int level = 7;
    public byte[][] data;
    public byte[] finalData;
    public int channel = 0;
    public List<InetSocketAddress> targets;

    public CompressBatchedTask(byte[][] data, List<InetSocketAddress> targets) {
        this(data, targets, 7);
    }

    public CompressBatchedTask(byte[][] data, List<InetSocketAddress> targets, int level) {
        this(data, targets, level, 0);
    }

    public CompressBatchedTask(byte[][] data, List<InetSocketAddress> targets, int level, int channel) {
        this.data = data;
        this.targets = targets;
        this.level = level;
        this.channel = channel;
    }

    @Override
    public void onRun() {
        try {
            this.finalData = Zlib.deflate(this.data, this.level);
            this.data = null;
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void onCompletion(Server server) {
        server.broadcastPacketsCallback(this.finalData, this.targets);
    }
}
