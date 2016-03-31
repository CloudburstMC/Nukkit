package cn.nukkit.scheduler;

import cn.nukkit.Server;
import cn.nukkit.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FileWriteTask extends AsyncTask {
    private File file;
    private InputStream contents;

    public FileWriteTask(String path, String contents) {
        this(new File(path), contents);
    }

    public FileWriteTask(String path, byte[] contents) {
        this(new File(path), contents);
    }

    public FileWriteTask(String path, InputStream contents) {
        this.file = new File(path);
        this.contents = contents;
    }

    public FileWriteTask(File file, String contents) {
        this.file = file;
        this.contents = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
    }

    public FileWriteTask(File file, byte[] contents) {
        this.file = file;
        this.contents = new ByteArrayInputStream(contents);
    }

    public FileWriteTask(File file, InputStream contents) {
        this.file = file;
        this.contents = contents;
    }

    @Override
    public void onRun() {
        try {
            Utils.writeFile(file, contents);
        } catch (IOException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

}
