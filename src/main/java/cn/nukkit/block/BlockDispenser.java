package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.impl.DispenserBlockEntity;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolid implements Faceable {

    public BlockDispenser(Identifier id) {
        super(id);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public int getComparatorInputOverride() {
        InventoryHolder blockEntity = this.getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return 0;
    }

    public boolean isTriggered() {
        return (this.getMeta() & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getBlockFace().getIndex();

        if (value) {
            i |= 8;
        }

        this.setMeta(i);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) {
            return false;
        }

        InventoryHolder blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return false;
        }

        player.addWindow(blockEntity.getInventory());
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (player != null) {
            if (Math.abs(player.getX() - this.getX()) < 2 && Math.abs(player.getZ() - this.getZ()) < 2) {
                double y = player.getY() + player.getEyeHeight();

                if (y - this.getY() > 2) {
                    this.setMeta(BlockFace.UP.getIndex());
                } else if (this.getY() - y > 0) {
                    this.setMeta(BlockFace.DOWN.getIndex());
                } else {
                    this.setMeta(player.getHorizontalFacing().getOpposite().getIndex());
                }
            } else {
                this.setMeta(player.getHorizontalFacing().getOpposite().getIndex());
            }
        }

        this.getLevel().setBlock(block.getPosition(), this, true);

        createBlockEntity();
        return true;
    }

    protected void createBlockEntity() {
        BlockEntityRegistry.get().newEntity(BlockEntityTypes.DISPENSER, getChunk(), position);
    }

    protected InventoryHolder getBlockEntity() {
        BlockEntity blockEntity = this.level.getBlockEntity(position);

        if (!(blockEntity instanceof DispenserBlockEntity)) {
            return null;
        }

        return (InventoryHolder) blockEntity;
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.setTriggered(false);
            this.level.setBlock(position, this, false, false);

            dispense();
            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean powered = level.isBlockPowered(position) || level.isBlockPowered(position.up());
            boolean triggered = isTriggered();

            if (powered && !triggered) {
                this.setTriggered(true);
                this.level.setBlock(position, this, false, false);
                level.scheduleUpdate(this, position, 4);
            }

            return type;
        }

        return 0;
    }

    public void dispense() {
        InventoryHolder blockEntity = getBlockEntity();

        if (blockEntity == null) {
            return;
        }

        Random rand = ThreadLocalRandom.current();
        int r = 1;
        int slot = -1;
        Item target = null;

        Inventory inv = blockEntity.getInventory();
        for (Entry<Integer, Item> entry : inv.getContents().entrySet()) {
            Item item = entry.getValue();

            if (!item.isNull() && rand.nextInt(r++) == 0) {
                target = item;
                slot = entry.getKey();
            }
        }

        LevelEventPacket pk = new LevelEventPacket();

        BlockFace facing = getBlockFace();

        Vector3f pkPos = Vector3f.from(
                0.5f + facing.getXOffset() * 0.7f,
                0.5f + facing.getYOffset() * 0.7f,
                0.5f + facing.getZOffset() * 0.7f
        );
        Chunk chunk = getChunk();
        pk.setPosition(pkPos);

        if (target == null) {
            pk.setType(LevelEventType.SOUND_CLICK_FAIL);
            pk.setData(1200);

            this.level.addChunkPacket(chunk.getX(), chunk.getZ(), pk);
            return;
        }

        pk.setType(LevelEventType.SOUND_CLICK);
        pk.setData(1000);
        this.level.addChunkPacket(chunk.getX(), chunk.getZ(), pk);

        pk = new LevelEventPacket();
        pk.setPosition(pkPos);
        pk.setType(LevelEventType.SHOOT);
        pk.setData(7);
        this.level.addChunkPacket(chunk.getX(), chunk.getZ(), pk);

        Item origin = target;
        target = target.clone();

        DispenseBehavior behavior = getDispenseBehavior(target);
        Item result = behavior.dispense(position, this, facing, target);

        target.decrementCount();
        inv.setItem(slot, target);

        if (result != null) {
            if (!result.equals(origin, true, false)) {
                Item[] fit = inv.addItem(result);

                if (fit.length > 0) {
                    for (Item drop : fit) {
                        this.level.dropItem(position, drop);
                    }
                }
            } else {
                inv.setItem(slot, result);
            }
        }
    }

    protected DispenseBehavior getDispenseBehavior(Item item) {
        return DispenseBehaviorRegister.getBehavior(item.getId());
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public Vector3f getDispensePosition() {
        BlockFace facing = getBlockFace();
        return position.toFloat().add(
                0.5 + 0.7 * facing.getXOffset(),
                0.5 + 0.7 * facing.getYOffset(),
                0.5 + 0.7 * facing.getZOffset()
        );
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
