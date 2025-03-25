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
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
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
    public String getName() {
        return "Campfire";
    }

    @Override
    public int getId() {
        return CAMPFIRE_BLOCK;
    }

    @Override
    public int getLightLevel() {
        return isExtinguished() ? 0 : 15;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] {Item.get(ItemID.COAL, 0, 1 + ThreadLocalRandom.current().nextInt(1))};
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.down().getId() == CAMPFIRE_BLOCK) {
            return false;
        }

        this.setDamage(player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0);
        Block layer1 = block.getLevelBlock(BlockLayer.WATERLOGGED);

        boolean defaultLayerCheck = (block instanceof BlockWater && block.getDamage() == 0 || block.getDamage() >= 8) || block instanceof BlockIceFrosted;
        boolean layer1Check = (layer1 instanceof BlockWater && layer1.getDamage() == 0 || layer1.getDamage() >= 8) || layer1 instanceof BlockIceFrosted;
        if (defaultLayerCheck || layer1Check) {
            this.setExtinguished(true);
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
            this.level.setBlock(this, BlockLayer.WATERLOGGED, defaultLayerCheck ? block : layer1, false, false);
        } else {
            this.level.setBlock(this, BlockLayer.WATERLOGGED, Block.get(Block.AIR), false, false);
        }

        this.getLevel().setBlock(this, this, true, true);
        this.createBlockEntity(item);
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

        return (BlockEntityCampfire) BlockEntity.createBlockEntity(BlockEntity.CAMPFIRE, this.getChunk(), nbt);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!this.isExtinguished() && !entity.isSneaking()) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, this instanceof BlockCampfireSoul ? 2 : 1));
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.isExtinguished()) {
                Block layer1 = this.getLevelBlock(BlockLayer.WATERLOGGED);
                if (layer1 instanceof BlockWater || layer1 instanceof BlockIceFrosted) {
                    this.setExtinguished(true);
                    this.level.setBlock(this, this, true, true);
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
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

        BlockEntity entity = this.level.getBlockEntity(this);
        if (!(entity instanceof BlockEntityCampfire)) {
            return false;
        }

        boolean itemUsed = false;
        if (item.isShovel() && !this.isExtinguished()) {
            this.setExtinguished(true);
            this.level.setBlock(this, this, true, true);
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
            itemUsed = true;
        } else if (item.getId() == ItemID.FLINT_AND_STEEL || item.hasEnchantment(Enchantment.ID_FIRE_ASPECT)) {
            item.useOn(this);
            this.setExtinguished(false);
            this.level.setBlock(this, this, true, true);
            entity.scheduleUpdate();
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
            itemUsed = true;
        }

        BlockEntityCampfire campfire = (BlockEntityCampfire) entity;
        Item cloned = item.clone();
        cloned.setCount(1);
        CampfireInventory inventory = campfire.getInventory();
        if (inventory.canAddItem(cloned)) {
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
    public double getMaxY() {
        return y + 0.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }

    public boolean isExtinguished() {
        return (this.getDamage() & 0x4) == 0x4;
    }

    public void setExtinguished(boolean extinguished) {
        this.setDamage((this.getDamage() & 0x3) | (extinguished? 0x4 : 0x0));
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0x3);
    }

    public void setBlockFace(BlockFace face) {
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            return;
        }

        this.setDamage((this.getDamage() & 0x4) | face.getHorizontalIndex());
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CAMPFIRE);
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

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }
}
