package cn.nukkit.positiontracking;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCompassLodestone;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PositionTrackingDBServerBroadcastPacket;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A position tracking db service. It holds file resources that needs to be closed when not needed anymore. 
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public class PositionTrackingService implements Closeable {
    private static final Pattern FILENAME_PATTERN = Pattern.compile("^\\d+\\.pnt$", Pattern.CASE_INSENSITIVE);
    private static final FilenameFilter FILENAME_FILTER = (dir, name) -> FILENAME_PATTERN.matcher(name).matches() && new File(dir, name).isFile();
    private final TreeMap<Integer, WeakReference<PositionTrackingStorage>> storage = new TreeMap<>(Comparator.reverseOrder());
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final File folder;
    private final WeakHashMap<Player, Map<PositionTrackingStorage, IntSet>> tracking = new WeakHashMap<>();

    /**
     * Creates position tracking db service. The service is ready to be used right after the creation.
     * @param folder The folder that will hold the position tracking db files
     * @throws FileNotFoundException If the folder does not exists and can't be created 
     */
    public PositionTrackingService(File folder) throws FileNotFoundException {
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new FileNotFoundException("Failed to create the folder "+folder);
        }
        this.folder = folder;
        WeakReference<PositionTrackingStorage> emptyRef = new WeakReference<>(null);
        Arrays.stream(Optional.ofNullable(folder.list(FILENAME_FILTER)).orElseThrow(()-> new FileNotFoundException("Invalid folder: "+folder)))
                .map(name-> Integer.parseInt(name.substring(0, name.length()-4)))
                .forEachOrdered(startIndex-> storage.put(startIndex, emptyRef));
    }
    
    private boolean hasTrackingDevice(Inventory inventory, int trackingHandler) {
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (isTrackingDevice(inventory.getItem(i), trackingHandler)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTrackingDevice(@Nullable Item item, int trackingHandler) {
        return item != null && item.getId() == ItemID.LODESTONE_COMPASS && item instanceof ItemCompassLodestone
                && ((ItemCompassLodestone) item).getTrackingHandle() == trackingHandler;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasTrackingDevice(Player player, int trackingHandler) {
        return Stream.of(player.getInventory(), player.getCursorInventory(), player.getOffhandInventory())
                .anyMatch(inv -> hasTrackingDevice(inv, trackingHandler));
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized PositionTracking startTracking(Player player, int trackingHandler, boolean validate) throws IOException {
        Preconditions.checkArgument(trackingHandler >= 0, "Tracking handler must be positive");
        if (validate && !hasTrackingDevice(player, trackingHandler)) {
            return null;
        }
        
        PositionTrackingStorage storage = getStorageForHandler(trackingHandler);
        if (storage == null) {
            return null;
        }
        PositionTracking position = storage.getPosition(trackingHandler);
        if (position == null) {
            return null; 
        }
        
        tracking.computeIfAbsent(player, p -> new HashMap<>()).computeIfAbsent(storage, s -> new IntOpenHashSet(3)).add(trackingHandler);
        return position;
    }
    
    private PositionTrackingDBServerBroadcastPacket destroyPacket(int trackingHandler) {
        PositionTrackingDBServerBroadcastPacket destroy = new PositionTrackingDBServerBroadcastPacket();
        destroy.setAction(PositionTrackingDBServerBroadcastPacket.Action.DESTROY);
        destroy.setTrackingId(trackingHandler);
        return destroy;
    } 

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean stopTracking(Player player) {
        Map<PositionTrackingStorage, IntSet> toRemove = tracking.remove(player);
        if (toRemove != null && player.isOnline()) {
            DataPacket[] packets = toRemove.values().stream()
                    .flatMapToInt(handlers -> IntStream.of(handlers.toIntArray()))
                    .mapToObj(this::destroyPacket)
                    .toArray(DataPacket[]::new);
            player.getServer().batchPackets(new Player[]{player}, packets);
        }
        return toRemove != null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean stopTracking(Player player, int trackingHandler) {
        Map<PositionTrackingStorage, IntSet> tracking = this.tracking.get(player);
        if (tracking == null) {
            return false;
        }
        
        for (Map.Entry<PositionTrackingStorage, IntSet> entry : tracking.entrySet()) {
            if (entry.getValue().remove(trackingHandler)) {
                if (entry.getValue().isEmpty()) {
                    tracking.remove(entry.getKey());
                }
                player.batchDataPacket(destroyPacket(trackingHandler));
                return true;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean isTracking(Player player, int trackingHandler, boolean validate) {
        Map<PositionTrackingStorage, IntSet> tracking = this.tracking.get(player);
        if (tracking == null) {
            return false;
        }

        for (IntSet value : tracking.values()) {
            if (value.contains(trackingHandler)) {
                if (validate && !hasTrackingDevice(player, trackingHandler)) {
                    stopTracking(player, trackingHandler);
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    public synchronized void recheckPlayers() {
        tracking.keySet().removeIf(p-> !p.isOnline());
        Map<Player, IntList> toRemove = new HashMap<>(2);
        for (Map.Entry<Player, Map<PositionTrackingStorage, IntSet>> entry : tracking.entrySet()) {
            Player player = entry.getKey();
            for (Map.Entry<PositionTrackingStorage, IntSet> entry2 : entry.getValue().entrySet()) {
                entry2.getValue().forEach((IntConsumer) trackingHandler-> {
                    if (!hasTrackingDevice(player, trackingHandler)) {
                        toRemove.computeIfAbsent(player, p-> new IntArrayList(2)).add(trackingHandler);
                    }
                });
            }
        }
        
        toRemove.forEach((player, list) -> 
                list.forEach((IntConsumer) handler -> 
                        stopTracking(player, handler)));
    }
    
    @Nullable
    private synchronized Integer findStorageForHandler(@Nonnull Integer handler) {
        Integer best = null;
        for (Integer startIndex : storage.keySet()) {
            int comp = startIndex.compareTo(handler);
            if (comp == 0) {
                return startIndex;
            }
            if (comp < 0 && (best == null || best.compareTo(comp) < 0)) {
                best = comp;
            }
        }
        return best;
    }
    
    @Nonnull
    private synchronized PositionTrackingStorage loadStorage(@Nonnull Integer startIndex) throws IOException {
        PositionTrackingStorage trackingStorage = storage.get(startIndex).get();
        if (trackingStorage != null) {
            return trackingStorage;
        }
        PositionTrackingStorage positionTrackingStorage = new PositionTrackingStorage(startIndex, new File(folder, startIndex+".pnt"));
        storage.put(startIndex, new WeakReference<>(positionTrackingStorage));
        return positionTrackingStorage;
    }
    
    @Nullable
    private synchronized PositionTrackingStorage getStorageForHandler(@Nonnull Integer trackingHandler) throws IOException{
        Integer startIndex = findStorageForHandler(trackingHandler);
        if (startIndex == null) {
            return null;
        }
        
        PositionTrackingStorage storage = loadStorage(startIndex);
        if (trackingHandler > storage.getMaxHandler()) {
            return null;
        }
        
        return storage;
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
    public synchronized int addOrReusePosition(NamedPosition position) throws IOException {
        checkClosed();
        OptionalInt trackingHandler = findTrackingHandler(position);
        if (trackingHandler.isPresent()) {
            return trackingHandler.getAsInt();
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
    public synchronized int addNewPosition(NamedPosition position) throws IOException {
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
    public synchronized int addNewPosition(NamedPosition position, boolean enabled) throws IOException {
        checkClosed();
        int next = 1;
        if (!storage.isEmpty()) {
            PositionTrackingStorage trackingStorage = loadStorage(storage.firstKey());
            OptionalInt handler = trackingStorage.addNewPosition(position, enabled);
            if (handler.isPresent()) {
                return handler.getAsInt();
            }
            next = trackingStorage.getMaxHandler();
        }

        PositionTrackingStorage trackingStorage = new PositionTrackingStorage(next, new File(folder, next + ".pnt"));
        storage.put(next, new WeakReference<>(trackingStorage));
        return trackingStorage.addNewPosition(position, enabled).orElseThrow(InternalError::new);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public OptionalInt findTrackingHandler(NamedPosition position) throws IOException {
        IntList handlers = findTrackingHandlers(position, true, 1);
        if (!handlers.isEmpty()) {
            return OptionalInt.of(handlers.getInt(0));
        }
        return OptionalInt.empty();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean invalidateHandler(int trackingHandler) throws IOException {
        checkClosed();
        PositionTrackingStorage storage = getStorageForHandler(trackingHandler);
        if (storage == null) {
            return false;
        }
        
        if (storage.hasPosition(trackingHandler, false)) {
            storage.invalidateHandler(trackingHandler);
            return true;
        }
        
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public PositionTracking getPosition(int trackingHandle) throws IOException {
        return getPosition(trackingHandle, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public PositionTracking getPosition(int trackingHandle, boolean onlyEnabled) throws IOException {
        checkClosed();
        PositionTrackingStorage trackingStorage = getStorageForHandler(trackingHandle);
        if (trackingStorage == null) {
            return null;
        }
        
        return trackingStorage.getPosition(trackingHandle, onlyEnabled);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean isEnabled(int trackingHandler) throws IOException {
        checkClosed();
        PositionTrackingStorage trackingStorage = getStorageForHandler(trackingHandler);
        return trackingStorage != null && trackingStorage.isEnabled(trackingHandler);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean setEnabled(int trackingHandler, boolean enabled) throws IOException {
        checkClosed();
        PositionTrackingStorage trackingStorage = getStorageForHandler(trackingHandler);
        if (trackingStorage == null) {
            return false;
        }
        return trackingStorage.setEnabled(trackingHandler, enabled);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean hasPosition(int trackingHandler) throws IOException {
        return hasPosition(trackingHandler, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean hasPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        checkClosed();
        Integer startIndex = findStorageForHandler(trackingHandler);
        if (startIndex == null) {
            return false;
        }
        
        if (!storage.containsKey(startIndex)) {
            return false;
        }
        
        return loadStorage(startIndex).hasPosition(trackingHandler, onlyEnabled);
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
        checkClosed();
        IntList list = new IntArrayList();
        for (Integer startIndex : storage.descendingKeySet()) {
            list.addAll(loadStorage(startIndex).findTrackingHandlers(pos, onlyEnabled, limit - list.size()));
            if (list.size() >= limit) {
                break;
            }
        }
        return list;
    }

    /**
     * Close all active 
     * @throws IOException If any resource failed to close properly. 
     * The detailed exceptions will be in getCause() and and getSuppressed()
     */
    @Override
    public synchronized void close() throws IOException {
        closed.set(true);
        IOException exception = null;
        for (WeakReference<PositionTrackingStorage> ref : storage.values()) {
            PositionTrackingStorage positionTrackingStorage = ref.get();
            if (positionTrackingStorage != null) {
                try {
                    positionTrackingStorage.close();
                } catch (Throwable e) {
                    if (exception == null) {
                        exception = new IOException(e);
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    private void checkClosed() throws IOException {
        if (closed.get()) {
            throw new IOException("The service is closed");
        }
    }
}
