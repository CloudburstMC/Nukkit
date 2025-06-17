package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Faceable;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolidMeta implements Faceable {

    public BlockDispenser() {
        this(0);
    }

    public BlockDispenser(int meta) {
        super(meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public String getName() {
        return "Dispenser";
    }

    @Override
    public int getId() {
        return DISPENSER;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(Block.DISPENSER));
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityDispenser) {
            return ContainerInventory.calculateRedstone(((BlockEntityDispenser) blockEntity).getInventory());
        }

        return 0;
    }

    public boolean isTriggered() {
        return (this.getDamage() & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getBlockFace().getIndex();

        if (value) {
            i |= 8;
        }

        this.setDamage(i);
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

        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (!(blockEntity instanceof BlockEntityDispenser)) {
            return false;
        }

        if (blockEntity.namedTag.contains("Lock") && blockEntity.namedTag.get("Lock") instanceof StringTag) {
            if (!blockEntity.namedTag.getString("Lock").equals(item.getCustomName())) {
                return true;
            }
        }

        player.addWindow(((BlockEntityDispenser) blockEntity).getInventory());
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex());
            }
        }

        this.getLevel().setBlock(block, this, true);

        BlockEntity.createBlockEntity(BlockEntity.DISPENSER, this.getChunk(), BlockEntity.getDefaultCompound(this, BlockEntity.DISPENSER));
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.setTriggered(false);
            this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client
            dispense();
            return type;
        } else if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!isTriggered() && (level.isBlockPowered(this) || level.isBlockPowered(this.getSideVec(BlockFace.UP)))) {
                this.setTriggered(true);
                this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client
                level.scheduleUpdate(this, this, 4);
            }

            return type;
        }

        return 0;
    }

    public void dispense() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (!(blockEntity instanceof BlockEntityDispenser)) {
            return;
        }

        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_CLICK);

        int r = 1;
        int slot = -1;
        Item original = null;

        Inventory inv = ((BlockEntityDispenser) blockEntity).getInventory();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Entry<Integer, Item> entry : inv.getContents().entrySet()) {
            Item item = entry.getValue();

            if (!item.isNull() && random.nextInt(r++) == 0) {
                original = item;
                slot = entry.getKey();
            }
        }

        if (original == null) {
            return;
        }

        Item target = original.clone();

        DispenseBehavior behavior = DispenseBehaviorRegister.getBehavior(target.getId());
        Item result = behavior.dispense(this, getBlockFace(), target);

        if (result == null) {
            target.count--;
            inv.setItem(slot, target);
        } else if (!result.equals(target)) {
            inv.setItem(slot, result);

            // TODO: Better solution. Give back empty buckets if a stack was in original slot.
            if (result.getId() == Item.HONEY_BOTTLE || result.getId() == Item.GLASS_BOTTLE || (result.getId() == Item.BUCKET && result.getDamage() > 0)) {
                Item[] invFull = inv.addItem(original.decrement(result.count));
                for (Item drop : invFull) {
                    DispenseBehaviorRegister.getBehavior(-1).dispense(this, getBlockFace(), drop);
                }
            }
        }
    }

    public Vector3 getDispensePosition() {
        BlockFace facing = getBlockFace();
        return this.add(
                0.5 + 0.7 * facing.getXOffset(),
                0.5 + 0.7 * facing.getYOffset(),
                0.5 + 0.7 * facing.getZOffset()
        );
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
