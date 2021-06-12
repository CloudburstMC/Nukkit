package cn.nukkit.positiontracking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCompassLodestone;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PositionTrackingDBServerBroadcastPacket;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.extern.log4j.Log4j2;

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

/**
 * A position tracking db service. It holds file resources that needs to be closed when not needed anymore. 
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
@Log4j2
public class PositionTrackingService implements Closeable {
    private static final Pattern FILENAME_PATTERN = Pattern.compile("^\\d+\\.pnt$", Pattern.CASE_INSENSITIVE);
    private static final FilenameFilter FILENAME_FILTER = (dir, name) -> FILENAME_PATTERN.matcher(name).matches() && new File(dir, name).isFile();
    private final TreeMap<Integer, WeakReference<PositionTrackingStorage>> storage = new TreeMap<>(Comparator.reverseOrder());
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final File folder;
    private final Map<Player, Map<PositionTrackingStorage, IntSet>> tracking = new MapMaker().weakKeys().makeMap();

    /**
     * Creates position tracking db service. The service is ready to be used right after the creation.
     * @param folder The folder that will hold the position tracking db files
     * @throws FileNotFoundException If the folder does not exists and can't be created 
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
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
    
    private boolean hasTrackingDevice(Player player, @Nullable Inventory inventory, int trackingHandler) throws IOException {
        if (inventory == null) {
            return false;
        }
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (isTrackingDevice(player, inventory.getItem(i), trackingHandler)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTrackingDevice(Player player, @Nullable Item item, int trackingHandler) throws IOException {
        if (!(item != null && item.getId() == ItemID.LODESTONE_COMPASS && item instanceof ItemCompassLodestone)) {
            return false;
        }
        ItemCompassLodestone compassLodestone = (ItemCompassLodestone) item;
        if (compassLodestone.getTrackingHandle() != trackingHandler) {
            return false;
        }
        PositionTracking position = getPosition(trackingHandler);
        return position != null && position.getLevelName().equals(player.getLevelName());
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasTrackingDevice(Player player, int trackingHandler) throws IOException {
        for (Inventory inventory: inventories(player)) {
            if (hasTrackingDevice(player, inventory, trackingHandler)) {
                return true;
            }
        }
        return false;
    }
    
    private void sendTrackingUpdate(Player player, int trackingHandler, PositionTracking pos) {
        if (player.getLevelName().equals(pos.getLevelName())) {
            PositionTrackingDBServerBroadcastPacket packet = new PositionTrackingDBServerBroadcastPacket();
            packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.UPDATE);
            packet.setPosition(pos);
            packet.setDimension(player.getLevel().getDimension());
            packet.setTrackingId(trackingHandler);
            packet.setStatus(0);
            player.dataPacket(packet);
        } else {
            sendTrackingDestroy(player, trackingHandler);
        }
    }
    
    private void sendTrackingDestroy(Player player, int trackingHandler) {
        PositionTrackingDBServerBroadcastPacket packet = destroyPacket(trackingHandler);
        player.dataPacket(packet);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public synchronized PositionTracking startTracking(Player player, int trackingHandler, boolean validate) throws IOException {
        Preconditions.checkArgument(trackingHandler >= 0, "Tracking handler must be positive");
        if (isTracking(player, trackingHandler, validate)) {
            PositionTracking position = getPosition(trackingHandler);
            if (position != null) {
                sendTrackingUpdate(player, trackingHandler, position);
                return position;
            }
            stopTracking(player, trackingHandler);
            return null;
        }
        
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
        PositionTrackingDBServerBroadcastPacket packet = new PositionTrackingDBServerBroadcastPacket();
        packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.DESTROY);
        packet.setTrackingId(trackingHandler);
        packet.setDimension(0);
        packet.setPosition(0, 0, 0);
        packet.setStatus(2);
        return packet;
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
                player.dataPacket(destroyPacket(trackingHandler));
                return true;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean isTracking(Player player, int trackingHandler, boolean validate) throws IOException {
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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized void forceRecheckAllPlayers() {
        tracking.keySet().removeIf(p-> !p.isOnline());
        Map<Player, IntList> toRemove = new HashMap<>(2);
        for (Map.Entry<Player, Map<PositionTrackingStorage, IntSet>> entry : tracking.entrySet()) {
            Player player = entry.getKey();
            for (Map.Entry<PositionTrackingStorage, IntSet> entry2 : entry.getValue().entrySet()) {
                entry2.getValue().forEach((IntConsumer) trackingHandler-> {
                    try {
                        if (!hasTrackingDevice(player, trackingHandler)) {
                            toRemove.computeIfAbsent(player, p -> new IntArrayList(2)).add(trackingHandler);
                        }
                    } catch (IOException e) {
                        log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e);
                    }
                });
            }
        }
        
        toRemove.forEach((player, list) -> 
                list.forEach((IntConsumer) handler -> 
                        stopTracking(player, handler)));

        Server.getInstance().getOnlinePlayers().values().forEach(this::detectNeededUpdates);
    }
    
    private Iterable<Inventory> inventories(Player player) {
        return () -> new Iterator<Inventory>() {
            int next = 0;
            @Override
            public boolean hasNext() {
                return next <= 4;
            }

            @Override
            public Inventory next() {
                switch (next++) {
                    case 0: return player.getInventory();
                    case 1: return player.getCursorInventory();
                    case 2: return player.getOffhandInventory();
                    case 3: return player.getCraftingGrid();
                    case 4: return player.getTopWindow().orElse(null);
                    default: throw new NoSuchElementException(); 
                }
            }
        };
    }
    
    private void detectNeededUpdates(Player player) {
        for (Inventory inventory: inventories(player)) {
            if (inventory == null) {
                continue;
            }
            int size = inventory.getSize();
            for (int slot = 0; slot < size; slot++) {
                Item item = inventory.getItem(slot);
                if (item.getId() == ItemID.LODESTONE_COMPASS && item instanceof ItemCompassLodestone) {
                    ItemCompassLodestone compass = (ItemCompassLodestone) item;
                    int trackingHandle = compass.getTrackingHandle();
                    if (trackingHandle != 0) {
                        PositionTracking pos;
                        try {
                            pos = getPosition(trackingHandle);
                            if (pos != null && pos.getLevelName().equals(player.getLevelName())) {
                                startTracking(player, trackingHandle, false);
                            }
                        } catch (IOException e) {
                            log.error("Failed to get the position of the tracking handler {}", trackingHandle, e);
                        }
                    }
                }
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void forceRecheck(Player player) {
        Map<PositionTrackingStorage, IntSet> tracking = this.tracking.get(player);
        if (tracking != null) {
            IntList toRemove = new IntArrayList(2);
            for (Map.Entry<PositionTrackingStorage, IntSet> entry2 : tracking.entrySet()) {
                entry2.getValue().forEach((IntConsumer) trackingHandler-> {
                    try {
                        if (!hasTrackingDevice(player, trackingHandler)) {
                            toRemove.add(trackingHandler);
                        }
                    } catch (IOException e) {
                        log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e);
                    }
                });
            }
            toRemove.forEach((IntConsumer) handler-> stopTracking(player, handler));
        }
        
        detectNeededUpdates(player);
    }
    
    @Nullable
    private synchronized Integer findStorageForHandler(@Nonnull Integer handler) {
        Integer best = null;
        for (Integer startIndex : storage.keySet()) {
            int comp = startIndex.compareTo(handler);
            if (comp == 0) {
                return startIndex;
            }
            if (comp < 0 && (best == null || best.compareTo(startIndex) < 0)) {
                best = startIndex;
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

        if (!storage.hasPosition(trackingHandler, false)) {
            return false;
        }
        storage.invalidateHandler(trackingHandler);
        
        handlerDisabled(trackingHandler);
        
        return true;
    }
    
    private void handlerDisabled(int trackingHandler) {
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Player, Map<PositionTrackingStorage, IntSet>> playerMapEntry : tracking.entrySet()) {
            for (IntSet value : playerMapEntry.getValue().values()) {
                if (value.contains(trackingHandler)) {
                    players.add(playerMapEntry.getKey());
                    break;
                }
            }
        }

        if (!players.isEmpty()) {
            Server.getInstance().batchPackets(players.toArray(Player.EMPTY_ARRAY), new DataPacket[]{destroyPacket(trackingHandler)});
        }
    }
    
    private void handlerEnabled(int trackingHandler) throws IOException {
        Server server = Server.getInstance();
        for (Player player : server.getOnlinePlayers().values()) {
            if (hasTrackingDevice(player, trackingHandler) && !isTracking(player, trackingHandler, false)) {
                startTracking(player, trackingHandler, false);
            }
        }
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
        if (trackingStorage.setEnabled(trackingHandler, enabled)) {
            if (enabled) {
                handlerEnabled(trackingHandler);
            } else {
                handlerDisabled(trackingHandler);
            }
            return true;
        }
        
        return false;
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
