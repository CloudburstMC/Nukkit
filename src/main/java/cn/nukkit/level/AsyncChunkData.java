package cn.nukkit.level;

class AsyncChunkData {

    final long timestamp;
    final int x;
    final int z;
    final long hash;
    final byte[] data;
    final int count;

    AsyncChunkData(long timestamp, int x, int z, long hash, byte[] data, int count) {
        this.timestamp = timestamp;
        this.x = x;
        this.z = z;
        this.hash = hash;
        this.data = data;
        this.count = count;
    }
}
