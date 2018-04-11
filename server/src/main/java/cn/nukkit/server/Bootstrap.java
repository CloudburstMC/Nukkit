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

package cn.nukkit.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class Bootstrap {
    public static final long START_TIME = System.currentTimeMillis();

    public static void main(String... args) {
        boolean ansiEnabled = false;
        Path path = Paths.get(System.getProperty("user.dir"));

        OptionParser parser = new OptionParser() {{
            accepts("enable-ansi");
            accepts("data-path").withRequiredArg().ofType(String.class);
            accepts("plugin-path").withRequiredArg().ofType(String.class);
        }};
        // Get arguments
        OptionSet options = parser.parse(args);

        if (options.has("enable-ansi")) {
            ansiEnabled = true;
        }

        Path dataPath;
        if (options.has("data-path")) {
            dataPath = Paths.get((String) options.valueOf("data-path"));
        } else {
            dataPath = path;
        }

        Path pluginPath;
        if (options.has("plugin-path")) {
            pluginPath = Paths.get((String) options.valueOf("plugin-path"));
        } else {
            pluginPath = dataPath.resolve("plugins");
        }

        NukkitServer server;
        try {
            server = new NukkitServer(path, dataPath, pluginPath, ansiEnabled);
        } catch (Exception e) {
            throw new AssertionError("Could not initialize Server class", e);
        }

        try {
            server.boot();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred whilst server was running", e);
        }

        log.info("Stopping server...");
        log.debug("Stopping other threads");

        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (!(thread instanceof InterruptibleThread)) {
                continue;
            }
            log.debug("Stopping " + thread.getClass().getSimpleName() + " thread");
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }

        //ServerKiller killer = new ServerKiller(8);
        //killer.start();

        log.info("Goodbye!");
    }
}
