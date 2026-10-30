package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ChunkSendOrderTest {
    private static final long INDEX = Level.chunkHash(0, 0);

    @Test
    void discardsSnapshotWhenNeighbourChangesBeforeEncodingCompletes() throws Exception {
        Fixture fixture = new Fixture(false);
        fixture.request();
        fixture.level.chunk.setChanged(); // A neighbouring chunk resolves the pane connections.
        fixture.complete();
        assertEquals(0, fixture.player.sent);
        assertTrue(fixture.waiting.containsKey(INDEX));

        fixture.request();
        fixture.complete();
        assertEquals(2, fixture.encodings);
        assertEquals(1, fixture.player.sent);
        assertFalse(fixture.waiting.containsKey(INDEX));
    }

    @Test
    void resettingDiskDirtyFlagDoesNotMakeOldNetworkSnapshotCurrent() throws Exception {
        Fixture fixture = new Fixture(false);
        fixture.level.chunk.setChanged();
        fixture.request();
        fixture.level.chunk.setChanged();
        fixture.level.chunk.setChanged(false); // Save, or connection-only load-time correction.
        fixture.complete();
        assertEquals(0, fixture.player.sent);
        fixture.request();
        fixture.complete();
        assertEquals(1, fixture.player.sent);
    }

    @Test
    void staleCachedResultIsDiscardedBeforeCompressionOrSending() throws Exception {
        Fixture fixture = new Fixture(true);
        fixture.request();
        fixture.level.chunk.setChanged();
        fixture.complete();
        assertEquals(0, fixture.player.sent);
        assertNull(fixture.level.chunk.getChunkPacket());
        assertTrue(fixture.waiting.containsKey(INDEX));
    }

    @Test
    void unchangedSnapshotIsDeliveredWithoutAnotherEncoding() throws Exception {
        Fixture fixture = new Fixture(false);
        fixture.request();
        fixture.complete();
        assertEquals(1, fixture.encodings);
        assertEquals(1, fixture.player.sent);
        assertFalse(fixture.waiting.containsKey(INDEX));
    }

    @Test
    void replacedChunkCannotReceiveThePreviousChunksSnapshot() throws Exception {
        Fixture fixture = new Fixture(false);
        fixture.request();
        fixture.level.chunk = allocate(LevelDBChunk.class);
        fixture.complete();
        assertEquals(0, fixture.player.sent);
        fixture.request();
        fixture.complete();
        assertEquals(1, fixture.player.sent);
    }

    private static final class Fixture {
        final TestLevel level = allocate(TestLevel.class);
        final RecordingPlayer player = allocate(RecordingPlayer.class);
        final Map<Long, Object> waiting = new ConcurrentHashMap<>();
        int encodings;
        long timestamp;

        Fixture(boolean cache) throws Exception {
            // Bypass constructors that open worlds/network sockets. The real request and
            // callback methods run below; only provider I/O and the receiving player are fake.
            level.chunk = allocate(LevelDBChunk.class);
            Server server = allocate(Server.class);
            server.cacheChunks = cache;
            setField(Level.class, level, "server", server);
            setField(Level.class, level, "chunkSendQueue", waiting);
            Field tasks = Level.class.getDeclaredField("chunkSendTasks");
            setField(Level.class, level, "chunkSendTasks", LongSet.class.isAssignableFrom(tasks.getType())
                    ? new LongOpenHashSet() : new Long2ObjectOpenHashMap<>());
            setField(Player.class, player, "usedChunks", new Long2ObjectOpenHashMap<Boolean>());
            player.usedChunks.put(INDEX, false);
            Int2ObjectOpenHashMap<Player> players = new Int2ObjectOpenHashMap<>();
            players.put(1, player);
            waiting.put(INDEX, players);
            LevelProvider provider = (LevelProvider) Proxy.newProxyInstance(
                    LevelProvider.class.getClassLoader(), new Class<?>[]{LevelProvider.class},
                    (proxy, method, args) -> {
                        if (!method.getName().equals("requestChunkTask")) {
                            throw new AssertionError(method.getName());
                        }
                        encodings++;
                        timestamp = level.chunk.getChanges();
                        return null;
                    });
            setField(Level.class, level, "provider", provider);
        }

        void request() throws Exception {
            Method method = Level.class.getDeclaredMethod("processChunkRequest");
            method.setAccessible(true);
            method.invoke(level);
        }

        void complete() {
            level.chunkRequestCallback(timestamp, 0, 0, 1, new byte[]{0}, INDEX);
        }
    }

    private static class TestLevel extends Level {
        BaseFullChunk chunk;

        private TestLevel() { super(null, null, null, null); }

        @Override public BaseFullChunk getChunk(int x, int z) { return chunk; }
        @Override public BaseFullChunk getChunkIfLoaded(int x, int z) { return chunk; }
        @Override public int getDimension() { return 0; }
    }

    private static class RecordingPlayer extends Player {
        int sent;

        private RecordingPlayer() { super(null, null, null); }

        @Override public boolean isConnected() { return true; }
        @Override public void sendChunk(int x, int z, int count, byte[] payload, int dimension) { sent++; }
    }

    private static void setField(Class<?> owner, Object target, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (T) ((Unsafe) field.get(null)).allocateInstance(type);
        } catch (Exception error) {
            throw new AssertionError(error);
        }
    }
}
