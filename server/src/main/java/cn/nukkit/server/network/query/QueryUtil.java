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

package cn.nukkit.server.network.query;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class QueryUtil {
    public static final byte[] LONG_RESPONSE_PADDING_TOP = new byte[]{115, 112, 108, 105, 116, 110, 117, 109, 0, -128, 0};
    public static final byte[] LONG_RESPONSE_PADDING_BOTTOM = new byte[]{1, 112, 108, 97, 121, 101, 114, 95, 0, 0};

    public static void writeNullTerminatedByteArray(ByteBuf buf, byte[] array) {
        if (array != null) {
            buf.writeBytes(array);
        }
        buf.writeByte(0); // Null byte
    }

    public static String readNullTerminatedString(ByteBuf in) {
        StringBuilder read = new StringBuilder();
        byte readIn;
        while ((readIn = in.readByte()) != '\0') {
            read.append((char) readIn);
        }
        return read.toString();
    }

    public static void writeNullTerminatedString(ByteBuf buf, String string) {
        writeNullTerminatedByteArray(buf, string.getBytes(StandardCharsets.UTF_8));
    }
}
