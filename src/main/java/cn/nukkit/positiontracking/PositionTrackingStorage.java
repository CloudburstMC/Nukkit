package cn.nukkit.positiontracking;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Stores a sequential range of {@link PositionTracking} objects in a file. The read operation is cached.
 * <p>This object holds a file handler and must be closed when it is no longer needed.</p>
 * <p>Once closed the instance cannot be reused.</p>
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public class PositionTrackingStorage implements Closeable {
    public static final int DEFAULT_MAX_STORAGE = 500;
    private static final byte[] HEADER = new byte[]{ 12,32,32, 'P', 'N', 'P', 'T', 'D', 'B', '1' };
    private final int startIndex;
    private final int maxStorage;
    private final long garbagePos;
    private final long stringHeapPos;
    private final RandomAccessFile persistence;
    private final Cache<Integer, Optional<PositionTracking>> cache = CacheBuilder.newBuilder().expireAfterAccess(5,TimeUnit.MINUTES).concurrencyLevel(1).build();
    private int nextIndex;

    /**
     * Opens or create the file and all directories in the path automatically. The given start index will be used
     * in new files and will be checked when opening files. If the file being opened don't matches this value
     * internally than an <code>IllegalArgumentException</code> will be thrown.
     * @param startIndex The number of the first handler. Must be higher than 0 and must match the number of the existing file.
     * @param persistenceFile The file being opened or created. Parent directories will also be created if necessary.
     * @throws IOException If an error has occurred while reading, parsing or creating the file
     * @throws IllegalArgumentException If opening an existing file and the internal startIndex don't match the given startIndex
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public PositionTrackingStorage(int startIndex, File persistenceFile) throws IOException {
        this(startIndex, persistenceFile, 0);
    }

    /**
     * Opens or create the file and all directories in the path automatically. The given start index will be used
     * in new files and will be checked when opening files. If the file being opened don't matches this value
     * internally than an <code>IllegalArgumentException</code> will be thrown.
     * @param startIndex The number of the first handler. Must be higher than 0 and must match the number of the existing file.
     * @param persistenceFile The file being opened or created. Parent directories will also be created if necessary.
     * @param maxStorage The maximum amount of positions that this storage may hold. It cannot be changed after creation. 
     *                   Ignored when loading an existing file. When zero or negative, a default value will be used.
     * @throws IOException If an error has occurred while reading, parsing or creating the file
     * @throws IllegalArgumentException If opening an existing file and the internal startIndex don't match the given startIndex
     */
    public PositionTrackingStorage(int startIndex, File persistenceFile, int maxStorage) throws IOException {
        Preconditions.checkArgument(startIndex > 0, "Start index must be positive. Got {}", startIndex);
        this.startIndex = startIndex;
        if (maxStorage <= 0) {
            maxStorage = DEFAULT_MAX_STORAGE;
        }
        
        boolean created = false;
        if (!persistenceFile.isFile()) {
            if (!persistenceFile.getParentFile().isDirectory() && !persistenceFile.getParentFile().mkdirs()) {
                throw new FileNotFoundException("Could not create the directory "+persistenceFile.getParent());
            }
            if (!persistenceFile.createNewFile()) {
                throw new FileNotFoundException("Could not create the file "+persistenceFile);
            }
            created = true;
        } else if(persistenceFile.length() == 0) {
            created = true;
        }
        
        this.persistence = new RandomAccessFile(persistenceFile, "rwd");
        try {
            if (created) {
                persistence.write(ByteBuffer.allocate(HEADER.length + 4 + 4 + 4)
                        .put(HEADER)
                        .putInt(maxStorage)
                        .putInt(startIndex)
                        .putInt(startIndex)
                        .array());
                this.maxStorage = maxStorage;
                nextIndex = startIndex;
            } else {
                byte[] check = new byte[HEADER.length];
                EOFException eof = null;
                int max;
                int next;
                int start;
                try {
                    persistence.readFully(check);
                    byte[] buf = new byte[4 + 4 + 4];
                    persistence.readFully(buf);
                    ByteBuffer buffer = ByteBuffer.wrap(buf);
                    max = buffer.getInt();
                    next = buffer.getInt();
                    start = buffer.getInt();
                } catch (EOFException e) {
                    eof = e;
                    max = 0;
                    next = 0;
                    start = 0;
                }
                if (eof != null || max <= 0 || next <= 0 || start <= 0 || !Arrays.equals(check, HEADER)) {
                    throw new IOException("The file " + persistenceFile + " is not a valid PowerNukkit TrackingPositionDB persistence file.", eof);
                }
                if (start != startIndex) {
                    throw new IllegalArgumentException("The start index "+startIndex+" was given but the file "+persistenceFile+" has start index "+start);
                }
                this.maxStorage = maxStorage = max;
                this.nextIndex = next;
            }
            garbagePos = getAxisPos(startIndex + maxStorage);

            //                          cnt  off len  max
            stringHeapPos = garbagePos + 4 + (8 + 4) * 15;

            if (created) {
                persistence.seek(stringHeapPos - 1);
                persistence.writeByte(0);
            }
        } catch (Throwable e) {
            try {
                persistence.close();
            } catch (Throwable e2) {
                e.addSuppressed(e2);
            }
            throw e;
        }
    }

    private long getAxisPos(int trackingHandler) {
        //                    max str cur  on  nam len  x   y   z 
        return HEADER.length + 4 + 4 + 4 + (1 + 8 + 4 + 8 + 8 + 8) * (long)(trackingHandler - startIndex);
    }

    private void validateHandler(int trackingHandler) {
        Preconditions.checkArgument(trackingHandler >= startIndex, "The trackingHandler {} is too low for this storage (starts at {})", trackingHandler, startIndex);
        int limit = startIndex + maxStorage;
        Preconditions.checkArgument(trackingHandler <= limit, "The trackingHandler {} is too high for this storage (ends at {})", trackingHandler, limit);
    }

    /**
     * Retrieves the {@link PositionTracking} object that is assigned to the given trackingHandler. 
     * The handler must be valid for this storage.
     * <p>This call may return a cached result but the returned object can be modified freely.</p>
     * @param trackingHandler A valid handler for this storage
     * @return A clone of the cached result.
     * @throws IOException If an error has occurred while accessing the file
     * @throws IllegalArgumentException If the trackingHandler is not valid for this storage
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public PositionTracking getPosition(int trackingHandler) throws IOException {
        validateHandler(trackingHandler);
        try {
            return cache.get(trackingHandler, ()-> loadPosition(trackingHandler, true))
                    .map(PositionTracking::clone)
                    .orElse(null);
        } catch (ExecutionException e) {
            throw handleExecutionException(e);
        }
    }

    /**
     * Retrieves the {@link PositionTracking} object that is assigned to the given trackingHandler. 
     * The handler must be valid for this storage.
     * <p>This call may return a cached result but the returned object can be modified freely.</p>
     * @param trackingHandler A valid handler for this storage
     * @param onlyEnabled When false, disabled positions that wasn't invalidated may be returned. 
     *                    Caching only works when this is set to true 
     * @return A clone of the cached result.
     * @throws IOException If an error has occurred while accessing the file
     * @throws IllegalArgumentException If the trackingHandler is not valid for this storage
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public PositionTracking getPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        if (onlyEnabled) {
            return getPosition(trackingHandler);
        }
        validateHandler(trackingHandler);
        return loadPosition(trackingHandler, false).orElse(null);
    }

    /**
     * Attempts to reuse an existing and enabled trackingHandler for the given position, if none is found than a new handler is created
     * if the limit was not exceeded.
     * @param position The position that needs a handler 
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public OptionalInt addOrReusePosition(NamedPosition position) throws IOException {
        OptionalInt handler = findTrackingHandler(position);
        if (handler.isPresent()) {
            return handler;
        }
        return addNewPosition(position);
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler 
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized OptionalInt addNewPosition(NamedPosition position) throws IOException {
        return addNewPosition(position, true);
    }

    /**
     * Adds the given position as a new entry in this storage, even if the position is already registered and enabled.
     * @param position The position that needs a handler 
     * @param enabled If the position will be added as enabled or disabled
     * @return The trackingHandler assigned to the position or an empty OptionalInt if none was found and this storage is full
     * @throws IOException If an error occurred while reading or writing the file
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized OptionalInt addNewPosition(NamedPosition position, boolean enabled) throws IOException {
        OptionalInt handler = addNewPos(position, enabled);
        if (!handler.isPresent()) {
            return handler;
        }
        if (enabled) {
            cache.put(handler.getAsInt(), Optional.of(new PositionTracking(position)));
        }
        return handler;
    }
 
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public OptionalInt findTrackingHandler(NamedPosition position) throws IOException {
        OptionalInt cached = cache.asMap().entrySet().stream()
                .filter(e-> e.getValue().filter(position::matchesNamedPosition).isPresent())
                .mapToInt(Map.Entry::getKey)
                .findFirst();
        if (cached.isPresent()) {
            return cached;
        }
        IntList handlers = findTrackingHandlers(position, true, 1);
        if (handlers.isEmpty()) {
            return OptionalInt.empty();
        }
        int found = handlers.getInt(0);
        cache.put(found, Optional.of(new PositionTracking(position)));
        return OptionalInt.of(found);
    }
    
    private IOException handleExecutionException(ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof IOException) {
            return (IOException) cause;
        }
        return new IOException(e);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized void invalidateHandler(int trackingHandler) throws IOException {
        validateHandler(trackingHandler);
        invalidatePos(trackingHandler);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean isEnabled(int trackingHandler) throws IOException {
        validateHandler(trackingHandler);
        persistence.seek(getAxisPos(trackingHandler));
        return persistence.readBoolean();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean setEnabled(int trackingHandler, boolean enabled) throws IOException {
        validateHandler(trackingHandler);
        long pos = getAxisPos(trackingHandler);
        persistence.seek(pos);
        if (persistence.readBoolean() == enabled) {
            return false;
        }
        if (persistence.readLong() == 0 && enabled) {
            return false;
        }
        persistence.seek(pos);
        persistence.writeBoolean(enabled);
        cache.invalidate(trackingHandler);
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean hasPosition(int trackingHandler) throws IOException {
        return hasPosition(trackingHandler, true);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean hasPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        validateHandler(trackingHandler);
        persistence.seek(getAxisPos(trackingHandler));
        boolean enabled = persistence.readBoolean();
        if (!enabled && onlyEnabled) {
            return false;
        }
        return persistence.readLong() != 0;
    }
    
    private synchronized void invalidatePos(int trackingHandler) throws IOException {
        long pos = getAxisPos(trackingHandler);
        persistence.seek(pos);
        persistence.writeBoolean(false);
        byte[] buf = new byte[8 + 4];
        persistence.readFully(buf);
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        long namePos = buffer.getLong();
        int nameLen = buffer.getInt();
        persistence.seek(pos + 1);
        persistence.write(new byte[8 + 4]);
        cache.put(trackingHandler, Optional.empty());
        addGarbage(namePos, nameLen);
    }
    
    private synchronized void addGarbage(long pos, int len) throws IOException {
        persistence.seek(garbagePos);
        int count = persistence.readInt();
        if (count >= 15) {
            return;
        }
        byte[] buf = new byte[4 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        if (count > 0) {
            for (int attempt = 0; attempt < 15; attempt++) {
                persistence.readFully(buf);
                buffer.rewind();
                long garbage = buffer.getLong();
                int garbageLen = buffer.getInt();
                if (garbage != 0) {
                    if (garbage + garbageLen == pos) {
                        persistence.seek(persistence.getFilePointer() - 4 - 8);
                        buffer.rewind();
                        buffer.putLong(garbage)
                                .putInt(garbageLen + len);
                        persistence.write(buf);
                        return;
                    } else if (pos + len == garbage) {
                        persistence.seek(persistence.getFilePointer() - 4 - 8);
                        buffer.rewind();
                        buffer.putLong(pos)
                                .putInt(garbageLen + len);
                        persistence.write(buf);
                        return;
                    }
                }
            }
            
            persistence.seek(garbagePos + 4);
        }
        
        for (int attempt = 0; attempt < 15; attempt++) {
            persistence.readFully(buf);
            buffer.rewind();
            long garbage = buffer.getLong();
            if (garbage == 0) {
                persistence.seek(persistence.getFilePointer() - 4 - 8);
                buffer.rewind();
                buffer.putLong(pos).putInt(len);
                persistence.write(buf);
                persistence.seek(garbagePos);
                persistence.writeInt(count + 1);
                return;
            }
        }
    }
    
    private synchronized long findSpaceInStringHeap(int len) throws IOException {
        persistence.seek(garbagePos);
        int remaining = persistence.readInt();
        if (remaining <= 0) {
            return persistence.length();
        }
        
        byte[] buf = new byte[4 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        for (int attempt = 0; attempt < 15; attempt++) {
            persistence.readFully(buf);
            buffer.rewind();
            long garbage = buffer.getLong();
            int garbageLen = buffer.getInt();
            if (garbage >= stringHeapPos && len <= garbageLen) {
                persistence.seek(persistence.getFilePointer() - 4 - 8);
                if (garbageLen == len) {
                    persistence.write(new byte[8 + 4]);
                    persistence.seek(garbagePos);
                    persistence.writeInt(remaining - 1);
                } else {
                    buffer.rewind();
                    buffer.putLong(garbage + len).putInt(garbageLen - len);
                    persistence.write(buf);
                }
                return garbage;
            }
        }
        return persistence.length();
    }
    
    private synchronized OptionalInt addNewPos(NamedPosition pos, boolean enabled) throws IOException {
        if (nextIndex - startIndex >= maxStorage) {
            return OptionalInt.empty();
        }
        int handler = nextIndex++;
        writePos(handler, pos, enabled);
        persistence.seek(HEADER.length + 4);
        persistence.writeInt(nextIndex);
        return OptionalInt.of(handler);
    }
    
    private synchronized void writePos(int trackingHandler, NamedPosition pos, boolean enabled) throws IOException {
        byte[] name = pos.getLevelName().getBytes(StandardCharsets.UTF_8);
        long namePos = addLevelName(name);
        persistence.seek(getAxisPos(trackingHandler));
        persistence.write(ByteBuffer.allocate(1 + 8 + 4 + 8 + 8 + 8)
                .put(enabled? (byte)1 : 0)
                .putLong(namePos)
                .putInt(name.length)
                .putDouble(pos.x)
                .putDouble(pos.y)
                .putDouble(pos.z)
                .array());
    }
    
    private synchronized long addLevelName(byte[] name) throws IOException {
        long pos = findSpaceInStringHeap(name.length);
        persistence.seek(pos);
        persistence.write(name);
        return pos;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public synchronized IntList findTrackingHandlers(NamedPosition pos) throws IOException {
        return findTrackingHandlers(pos, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public synchronized IntList findTrackingHandlers(NamedPosition pos, boolean onlyEnabled) throws IOException {
        return findTrackingHandlers(pos, onlyEnabled, Integer.MAX_VALUE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public synchronized IntList findTrackingHandlers(NamedPosition pos, boolean onlyEnabled, int limit) throws IOException {
        persistence.seek(HEADER.length + 4 + 4 + 4);
        int handler = startIndex - 1;
        final double lookingX = pos.x;
        final double lookingY = pos.y;
        final double lookingZ = pos.z;
        final byte[] lookingName = pos.getLevelName().getBytes(StandardCharsets.UTF_8);
        IntList results = new IntArrayList(NukkitMath.clamp(limit, 1, 16));
        byte[] buf = new byte[8 + 4 + 8 + 8 + 8];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        while (true) {
            handler++;
            if (handler >= nextIndex) {
                return results;
            }
            boolean enabled = persistence.readBoolean();
            if (onlyEnabled && !enabled) {
                if (persistence.skipBytes(36) != 36) throw new EOFException();
                continue;
            }
            
            persistence.readFully(buf);
            
            buffer.rewind();
            long namePos = buffer.getLong();
            int nameLen = buffer.getInt();
            double x = buffer.getDouble();
            double y = buffer.getDouble();
            double z = buffer.getDouble();
            if (namePos > 0 && nameLen > 0 && x == lookingX && y == lookingY && z == lookingZ) {
                long fp = persistence.getFilePointer();
                byte[] nameBytes = new byte[nameLen];
                persistence.seek(namePos);
                persistence.readFully(nameBytes);
                if (Arrays.equals(lookingName, nameBytes)) {
                    results.add(handler);
                    if (results.size() >= limit) {
                        return results;
                    }
                }
                persistence.seek(fp);
            }
        }
    }
    
    private synchronized Optional<PositionTracking> loadPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        if (trackingHandler >= nextIndex) {
            return Optional.empty();
        }
        
        persistence.seek(getAxisPos(trackingHandler));
        byte[] buf = new byte[1 + 8 + 4 + 8 + 8 + 8];
        persistence.readFully(buf);
        boolean enabled = buf[0] == 1;
        if (!enabled && onlyEnabled) {
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(buf, 1, buf.length - 1);

        long namePos = buffer.getLong();
        if (namePos == 0) {
            return Optional.empty();
        }
        int nameLen = buffer.getInt();

        double x = buffer.getDouble();
        double y = buffer.getDouble();
        double z = buffer.getDouble();

        byte[] nameBytes = new byte[nameLen];
        persistence.seek(namePos);
        persistence.readFully(nameBytes);
        String name = new String(nameBytes, StandardCharsets.UTF_8);
        return Optional.of(new PositionTracking(name, x, y, z));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getStartingHandler() {
        return startIndex;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMaxHandler() {
        return startIndex + maxStorage - 1;
    }

    @Override
    public synchronized void close() throws IOException {
        persistence.close();
    }

    @Override
    protected void finalize() throws Throwable {
        persistence.close();
    }
}
