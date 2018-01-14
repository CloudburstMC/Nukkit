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
