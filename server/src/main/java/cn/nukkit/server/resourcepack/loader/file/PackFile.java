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

package cn.nukkit.server.resourcepack.loader.file;

import cn.nukkit.server.util.NativeCodeFactory;
import com.voxelwind.server.jni.hash.VoxelwindHash;

public abstract class PackFile {
    public static final int CHUNK_SIZE = 1048576;
    static final ThreadLocal<VoxelwindHash> hashLocal = ThreadLocal.withInitial(NativeCodeFactory.hash::newInstance);

    public abstract long getPackSize();

    public abstract byte[] getSha256();

    public abstract byte[] getPackChunk(int chunkIndex);

    public abstract int getChunkCount();

    public abstract int getCompressedSize();
}
