package com.nukkitx.server.scheduler;

import com.nukkitx.server.util.Utils;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class FileWriteTask extends AsyncTask<Path> {
    private Path path;
    private final InputStream contents;

    public FileWriteTask(String path, String contents) {
        this(Paths.get(path), contents);
    }

    public FileWriteTask(String path, byte[] contents) {
        this(Paths.get(path), contents);
    }

    public FileWriteTask(String path, InputStream contents) {
        this.path = Paths.get(path);
        this.contents = contents;
    }

    public FileWriteTask(Path path, String contents) {
        this.path = path;
        this.contents = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
    }

    public FileWriteTask(Path path, byte[] contents) {
        this.path = path;
        this.contents = new ByteArrayInputStream(contents);
    }

    public FileWriteTask(Path path, InputStream contents) {
        this.path = path;
        this.contents = contents;
    }

    @Override
    public void run() {
        try {
            Utils.writeFile(path, contents);
        } catch (IOException e) {
            log.throwing(e);
        }
    }

    @Override
    public Path getRawResult() {
        return path;
    }

    @Override
    protected void setRawResult(Path path) {
        this.path = path;
    }
}
