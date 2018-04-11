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

package cn.nukkit.server.console;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

@Log4j2
@Plugin(
        name = "NukkitConsole",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE,
        printObject = true
)
public class NukkitConsoleAppender extends AbstractAppender {
    private static final PrintStream STDOUT;
    private static org.jline.terminal.Terminal terminal;
    private static LineReader lineReader;
    private static boolean initialized = false;
    private static NukkitConsoleFormatter formatter;

    static {
        STDOUT = System.out;
        formatter = new NukkitConsoleFormatter();
    }

    private NukkitConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        initializeTerminal();
    }

    public static Terminal getTerminal() {
        return terminal;
    }

    public static void setLineReader(LineReader reader) {
        if (reader != null && reader.getTerminal() != terminal) {
            throw new IllegalArgumentException("LineReader given was not created with NukkitConsoleAppender!");
        }
        lineReader = reader;
    }

    private static void initializeTerminal() {
        if (!initialized) {
            initialized = true;

            try {
                terminal = TerminalBuilder.builder().jansi(Ansi.isDetected()).build();
            } catch (IllegalStateException e) {
                log.warn("The environment you're running is unsupported.");
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            } catch (IOException e) {
                log.fatal("Failed to initialize the terminal. Console input will not be possible.");
            }
        }
    }

    public static void close() throws IOException {
        if (initialized) {
            initialized = false;
            if (terminal != null) {
                try {
                    terminal.close();
                } finally {
                    terminal = null;
                }
            }
        }
    }

    @PluginFactory
    public static NukkitConsoleAppender createAppender(
            @Required(message = "No name provided for NukkitConsoleAppender") @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") @Nullable Layout<? extends Serializable> layout,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new NukkitConsoleAppender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        if (terminal != null) {
            try {
                if (lineReader != null) {
                    // Draw the prompt line again if a reader is available
                    lineReader.callWidget(LineReader.CLEAR);
                    terminal.writer().print(formatEvent(event));
                    lineReader.callWidget(LineReader.REDRAW_LINE);
                    lineReader.callWidget(LineReader.REDISPLAY);
                } else {
                    terminal.writer().print(formatEvent(event));
                }
            } catch (Exception e) {
                // Ignore
            }
            terminal.writer().flush();
        } else {
            STDOUT.print(getLayout().toSerializable(event));
        }
    }

    public String formatEvent(LogEvent event) {
        return formatter.apply(getLayout().toSerializable(event).toString());
    }
}