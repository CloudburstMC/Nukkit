package cn.nukkit.scheduler;

import cn.nukkit.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class FileWriteTask extends AsyncTask {
    private final File file;
    private final InputStream contents;

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
            log.throwing(Level.ERROR, e);
        }
    }
}
