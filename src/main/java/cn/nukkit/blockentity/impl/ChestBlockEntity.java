package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Chest;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.DoubleChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChestBlockEntity extends BaseBlockEntity implements Chest {

    private final ChestInventory inventory = new ChestInventory(this);

    private DoubleChestInventory doubleInventory = null;
    private Vector3i pairPosition;
    private boolean pairlead;
    private boolean findable;

    public ChestBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                Item item = ItemUtils.deserializeItem(itemTag);
                this.inventory.setItem(itemTag.getByte("Slot"), item);
            }
        });
        if (tag.contains("pairx") && tag.contains("pairz")) {
            this.pairPosition = Vector3i.from(tag.getInt("pairx"), this.getPosition().getY(), tag.getInt("pairz"));
        }
        tag.listenForBoolean("pairlead", this::setPairlead);
        tag.listenForBoolean("Findable", this::setFindable);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.inventory.getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);

        if (this.pairPosition != null) {
            tag.intTag("pairx", this.pairPosition.getX());
            tag.intTag("pairz", this.pairPosition.getZ());
        }
        tag.booleanTag("pairlead", this.pairlead);
        tag.booleanTag("Findable", this.findable);
    }

    private void setPairlead(boolean pairlead) {
        this.pairlead = pairlead;
    }

    public boolean isFindable() {
        return findable;
    }

    public void setFindable(boolean findable) {
        this.findable = findable;
    }

    @Override
    public void close() {
        if (!closed) {
            unpair();

            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }

            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getRealInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            this.getLevel().dropItem(this.getPosition(), content);
        }
        inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }

    @Override
    public boolean isValid() {
        Identifier blockId = this.getBlock().getId();
        return blockId == BlockIds.CHEST || blockId == BlockIds.TRAPPED_CHEST;
    }


    @Override
    public ContainerInventory getInventory() {
        if (this.doubleInventory == null && this.isPaired()) {
            this.checkPairing();
        }

        return this.doubleInventory != null ? this.doubleInventory : this.inventory;
    }

    public ChestInventory getRealInventory() {
        return inventory;
    }

    protected void checkPairing() {
        ChestBlockEntity pair = this.getPair();

        if (pair != null) {
            if (!pair.isPaired()) {
                pair.createPair(this);
                pair.checkPairing();
            }

            if (pair.doubleInventory != null) {
                this.doubleInventory = pair.doubleInventory;
            } else if (this.doubleInventory == null) {
                if ((pair.pairPosition.getX() + pair.pairPosition.getZ() << 15) >
                        (this.pairPosition.getX() + this.pairPosition.getZ() << 15)) { //Order them correctly
                    this.doubleInventory = new DoubleChestInventory(pair, this);
                } else {
                    this.doubleInventory = new DoubleChestInventory(this, pair);
                }
            }
        } else {
            if (this.getLevel().isChunkLoaded(this.pairPosition)) {
                this.doubleInventory = null;
                this.pairPosition = null;
            }
        }
    }

    public boolean isPaired() {
        return this.pairPosition != null;
    }

    public ChestBlockEntity getPair() {
        if (this.isPaired()) {
            BlockEntity blockEntity = this.getLevel().getLoadedBlockEntity(this.pairPosition);
            if (blockEntity instanceof ChestBlockEntity) {
                return (ChestBlockEntity) blockEntity;
            }
        }

        return null;
    }

    public boolean pairWith(Chest chest) {
        if (this.isPaired() || chest.isPaired() || this.getBlock().getId() != chest.getBlock().getId()) {
            return false;
        }

        this.createPair((ChestBlockEntity) chest);

        chest.spawnToAll();
        this.spawnToAll();
        this.checkPairing();

        return true;
    }

    private void createPair(ChestBlockEntity chest) {
        this.pairPosition = chest.getPosition();
        chest.pairPosition = this.getPosition();
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }

        ChestBlockEntity chest = this.getPair();

        this.doubleInventory = null;
        this.pairPosition = null;

        this.spawnToAll();

        if (chest != null) {
            chest.pairPosition = null;
            chest.doubleInventory = null;
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
