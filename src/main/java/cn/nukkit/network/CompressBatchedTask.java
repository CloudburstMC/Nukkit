package cn.nukkit.network;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Zlib;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CompressBatchedTask extends AsyncTask {

    public int level = 9;
    public byte[][] data;
    public byte[] finalData;
    public int channel = 0;
    public List<String> targets = new ArrayList<>();

    public CompressBatchedTask(byte[][] data, List<String> targets) {
        this(data, targets, 9);
    }

    public CompressBatchedTask(byte[][] data, List<String> targets, int level) {
        this(data, targets, level, 0);
    }

    public CompressBatchedTask(byte[][] data, List<String> targets, int level, int channel) {
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
