package cn.nukkit.scheduler;

import cn.nukkit.utils.Utils;

import java.io.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FileWriteTask extends AsyncTask {
    private File file;
    private InputStream contents;

    public FileWriteTask(String path, String contents) throws UnsupportedEncodingException {
        this(path, new ByteArrayInputStream(contents.getBytes("UTF-8")));
    }

    public FileWriteTask(String path, InputStream contents) {
        this.file = new File(path);
        this.contents = contents;
    }

    public FileWriteTask(File file, String contents) throws UnsupportedEncodingException {
        this(file, new ByteArrayInputStream(contents.getBytes("UTF-8")));
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
            e.printStackTrace();
        }
    }

}
