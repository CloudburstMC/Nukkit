package cn.nukkit.scheduler;

import cn.nukkit.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FileWriteTask extends AsyncTask {
    private String path;
    private InputStream contents;

    public FileWriteTask(String path, String contents) {
        this(path, new ByteArrayInputStream(contents.getBytes()));
    }

    public FileWriteTask(String path, InputStream contents) {
        this.path = path;
        this.contents = contents;
    }

    @Override
    public void onRun() {
        Utils.writeFile(path, contents);
    }

}
