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

package cn.nukkit.server.util;

import com.voxelwind.server.jni.hash.JavaHash;
import com.voxelwind.server.jni.hash.NativeHash;
import com.voxelwind.server.jni.hash.VoxelwindHash;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.jni.NativeCode;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.jni.cipher.JavaCipher;
import net.md_5.bungee.jni.cipher.NativeCipher;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.jni.zlib.JavaZlib;
import net.md_5.bungee.jni.zlib.NativeZlib;

@UtilityClass
public class NativeCodeFactory {
    public static final NativeCode<BungeeZlib> zlib = new NativeCode<>("native-compress", JavaZlib.class, NativeZlib.class);
    public static final NativeCode<BungeeCipher> cipher = new NativeCode<>("native-cipher", JavaCipher.class, NativeCipher.class);
    public static final NativeCode<VoxelwindHash> hash = new NativeCode<>("native-hash", JavaHash.class, NativeHash.class);
}
