/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.scheduler;

import cn.nukkit.server.util.Utils;
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
