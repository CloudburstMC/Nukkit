package cn.nukkit.item.custom;

import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.ItemComponentPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Handles custom item registry.
 * <p>
 * See <a href="https://github.com/PetteriM1/CustomItemExample">CustomItemExample</a> for example usage
 */
public class CustomItemManager {

    /**
     * Lowest allowed Nukkit save id for custom items
     */
    public static final int LOWEST_CUSTOM_ITEM_ID = 5000;

    private static final CustomItemManager INSTANCE = new CustomItemManager();

    /**
     * Get CustomItemManager instance
     */
    public static CustomItemManager get() {
        return INSTANCE;
    }

    private final Map<String, ItemDefinition> itemDefinitions = new HashMap<>();
    private final Int2ObjectMap<ItemDefinition> legacyDefinitions = new Int2ObjectOpenHashMap<>();

    private volatile boolean closed;

    private BatchPacket cachedPacket;

    private CustomItemManager() {

    }

    public BatchPacket getCachedPacket() {
        if (this.cachedPacket == null) {
            ItemComponentPacket pk = new ItemComponentPacket();
            pk.itemDefinitions = new ArrayList<>(this.itemDefinitions.values());
            pk.tryEncode();
            this.cachedPacket = pk.compress(Deflater.BEST_COMPRESSION);
        }
        return this.cachedPacket;
    }

    @SuppressWarnings("unused")
    public void registerDefinition(ItemDefinition definition) {
        if (this.closed) {
            throw new IllegalStateException("Item registry was already closed");
        }

        if (definition.getLegacyId() < LOWEST_CUSTOM_ITEM_ID) {
            throw new IllegalArgumentException("Custom item ID can not be lower than " + LOWEST_CUSTOM_ITEM_ID);
        }

        if (definition.getLegacyId() > 65534) {
            throw new IllegalArgumentException("Custom item ID can not be higher than 65534");
        }

        if (this.legacyDefinitions.containsKey(definition.getLegacyId())) {
            throw new IllegalArgumentException("Custom item " + definition.getIdentifier() + " cannot be registered because legacy ID " +
                    definition.getLegacyId() +" is already used by " + this.getDefinition(definition.getLegacyId()).getIdentifier());
        }

        if (this.itemDefinitions.containsKey(definition.getIdentifier())) {
            throw new IllegalArgumentException("Custom item " + definition.getIdentifier() + " was already registered");
        }

        this.itemDefinitions.put(definition.getIdentifier(), definition);
        this.legacyDefinitions.put(definition.getLegacyId(), definition);

        Item.list[definition.getLegacyId()] = definition.getImplementation();

        RuntimeItems.getMapping().registerItem(definition.getIdentifier(), definition.getLegacyId(), definition.getLegacyId(), 0);

        if (definition.getCreativeCategory() != null && definition.getCreativeCategory() != ItemDefinition.CreativeCategory.NONE) {
            try {
                Item item = definition.getImplementation().getConstructor(Integer.class, int.class).newInstance(0, 1);
                if (!(item instanceof CustomItem)) {
                    throw new IllegalStateException("Implementation of " + definition.getIdentifier() + " does not implement CustomItem");
                }
                Item.addCreativeItem(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ItemDefinition getDefinition(String identifier) {
        return this.itemDefinitions.get(identifier);
    }

    public ItemDefinition getDefinition(int legacyId) {
        return this.legacyDefinitions.get(legacyId);
    }

    public int getLegacyId(String identifier) {
        ItemDefinition definition = this.itemDefinitions.get(identifier);
        if (definition == null) {
            return -1;
        }
        return definition.getLegacyId();
    }

    public boolean hasCustomItems() {
        return !this.itemDefinitions.isEmpty();
    }

    /**
     * Internal: close registry to prepare for data generation
     */
    public boolean closeRegistry() {
        if (this.closed) {
            throw new IllegalStateException("Item registry was already closed");
        }

        this.closed = true;

        boolean hasItems = this.hasCustomItems();

        if (hasItems) {
            this.getCachedPacket();
        }

        return hasItems;
    }
}
