package com.nukkitx.server.util;

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
