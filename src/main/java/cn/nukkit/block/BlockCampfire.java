package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.CampfireInventory;
import cn.nukkit.inventory.CampfireRecipe;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlockCampfire extends BlockTransparentMeta implements Faceable {
    public BlockCampfire() {
        this(0);
    }

    public BlockCampfire(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAMPFIRE_BLOCK;
    }

    @Override
    public int getLightLevel() {
        return isExtinguished()? 0 : 15;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 2.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { new ItemCoal(0, 1 + ThreadLocalRandom.current().nextInt(1)) };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (down().getId() == CAMPFIRE_BLOCK) {
            return false;
        }

        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        Block layer1 = block.getLevelBlockAtLayer(1);
        if (block instanceof BlockWater || block instanceof BlockIceFrosted || layer1 instanceof BlockWater || layer1 instanceof BlockIceFrosted) {
            setExtinguished(true);
            this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
        }

        this.level.setBlock(block, this, true, true);
        createBlockEntity(item);
        return true;
    }

    private BlockEntityCampfire createBlockEntity(Item item) {
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.CAMPFIRE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return (BlockEntityCampfire) BlockEntity.createBlockEntity(BlockEntity.CAMPFIRE, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!isExtinguished() && !entity.isSneaking()) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 1));
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isExtinguished()) {
                Block layer1 = getLevelBlockAtLayer(1);
                if (layer1 instanceof BlockWater || layer1 instanceof BlockIceFrosted) {
                    setExtinguished(true);
                    this.level.setBlock(this, this, true, true);
                    this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == BlockID.AIR || item.getCount() <= 0) {
            return false;
        }

        BlockEntity entity = level.getBlockEntity(this);
        if (!(entity instanceof BlockEntityCampfire)) {
            entity = createBlockEntity(Item.get(0));
        }

        boolean itemUsed = false;
        if (item.isShovel() && !isExtinguished()) {
            setExtinguished(true);
            this.level.setBlock(this, this, true, true);
            this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
            itemUsed = true;
        } else if (item.getId() == ItemID.FLINT_AND_STEEL) {
            item.useOn(this);
            setExtinguished(false);
            this.level.setBlock(this, this, true, true);
            if (entity != null) {
                entity.scheduleUpdate();
            }
            this.level.addSound(this, Sound.FIRE_IGNITE);
            itemUsed = true;
        }

        if (entity == null) {
            return itemUsed;
        }

        BlockEntityCampfire campfire = (BlockEntityCampfire) entity;
        Item cloned = item.clone();
        cloned.setCount(1);
        CampfireInventory inventory = campfire.getInventory();
        if(inventory.canAddItem(cloned)) {
            CampfireRecipe recipe = this.level.getServer().getCraftingManager().matchCampfireRecipe(cloned);
            if (recipe != null) {
                inventory.addItem(cloned);
                item.setCount(item.getCount() - 1);
                return true;
            }
        }

        return itemUsed;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public double getMaxY() {
        return y + 0.4371948;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }

    public boolean isExtinguished() {
        return (getDamage() & 0x4) == 0x4;
    }

    public void setExtinguished(boolean extinguished) {
        setDamage((getDamage() & 0x3) | (extinguished? 0x4 : 0x0));
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0x3);
    }

    public void setBlockFace(BlockFace face) {
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            return;
        }

        setDamage((getDamage() & 0x4) | face.getHorizontalIndex());
    }

    @Override
    public String getName() {
        return "Campfire";
    }

    @Override
    public Item toItem() {
        return new ItemCampfire();
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityCampfire) {
            return ContainerInventory.calculateRedstone(((BlockEntityCampfire) blockEntity).getInventory());
        }

        return super.getComparatorInputOverride();
    }
}
