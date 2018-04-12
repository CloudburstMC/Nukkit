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

package cn.nukkit.server.network.util;

import cn.nukkit.server.util.NativeCodeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.jni.zlib.BungeeZlib;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

@UtilityClass
public class CompressionUtil {
    private static final ThreadLocal<BungeeZlib> zlibInflaterLocal = ThreadLocal.withInitial(() -> {
        BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
        zlib.init(false, Deflater.DEFAULT_COMPRESSION);
        return zlib;
    });
    private static final ThreadLocal<BungeeZlib> zlibDeflaterLocal = ThreadLocal.withInitial(() -> {
        BungeeZlib zlib = NativeCodeFactory.zlib.newInstance();
        zlib.init(true, Deflater.DEFAULT_COMPRESSION);
        return zlib;
    });

    public static ByteBuf zlibInflate(ByteBuf buffer) throws DataFormatException {
        // Ensure that this buffer is direct.
        if (buffer.getByte(0) != 0x78) throw new DataFormatException("No zlib header");
        ByteBuf source = null;
        ByteBuf decompressed = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            if (!buffer.isDirect()) {
                // We don't have a direct buffer. Create one.
                ByteBuf temporary = PooledByteBufAllocator.DEFAULT.directBuffer();
                temporary.writeBytes(buffer);
                source = temporary;
            } else {
                source = buffer;
            }

            zlibInflaterLocal.get().process(source, decompressed);
            return decompressed;
        } catch (DataFormatException e) {
            decompressed.release();
            throw e;
        } finally {
            if (source != null && source != buffer) {
                source.release();
            }
        }
    }

    public static ByteBuf zlibDeflate(ByteBuf buffer) throws DataFormatException {
        ByteBuf dest = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            zlibDeflate(buffer, dest);
        } catch (DataFormatException e) {
            dest.release();
            throw e;
        }
        return dest;
    }

    public static void zlibDeflate(ByteBuf toCompress, ByteBuf into) throws DataFormatException {
        ByteBuf destination = null;
        ByteBuf source = null;
        try {
            if (!toCompress.isDirect()) {
                // Source is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                source = PooledByteBufAllocator.DEFAULT.directBuffer();
                source.writeBytes(toCompress);
            } else {
                source = toCompress;
            }

            if (!into.isDirect()) {
                // Destination is not a direct buffer. Work on a temporary direct buffer and then write the contents out.
                destination = PooledByteBufAllocator.DEFAULT.directBuffer();
            } else {
                destination = into;
            }

            zlibDeflaterLocal.get().process(source, destination);

            if (destination != into) {
                into.writeBytes(destination);
            }
        } finally {
            if (source != null && source != toCompress) {
                source.release();
            }
            if (destination != null && destination != into) {
                destination.release();
            }
        }
    }
}
