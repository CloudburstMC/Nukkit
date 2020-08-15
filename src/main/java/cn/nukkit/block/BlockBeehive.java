package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import java.util.List;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

public class BlockBeehive extends BlockSolidMeta implements Faceable {
    public static final IntBlockProperty HONEY_LEVEL = new IntBlockProperty("honey_level", false, 5);
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRECTION, HONEY_LEVEL);
    public BlockBeehive() {
        this(0);
    }

    protected BlockBeehive(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEEHIVE;
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Beehive";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
    
        int honeyLevel = item.hasCustomBlockData() ? item.getCustomBlockData().getByte("HoneyLevel") : 0;
        setHoneyLevel(honeyLevel);
        this.level.setBlock(this, this, true, true);
        
        BlockEntityBeehive beehive = createEntity(item.getCustomBlockData());
        if (beehive == null) {
            return false;
        }
        if (beehive.namedTag.getByte("ShouldSpawnBees") > 0) {
            List<BlockFace> validSpawnFaces = beehive.scanValidSpawnFaces(true);
            for (BlockEntityBeehive.Occupant occupant : beehive.getOccupants()) {
                beehive.spawnOccupant(occupant, validSpawnFaces);
            }
    
            beehive.namedTag.putByte("ShouldSpawnBees", 0);
        }
        return true;
    }
    
    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemID.SHEARS && isFull()) {
            honeyCollected(player);
            level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_BEEHIVE_SHEAR);
            item.useOn(this);
            for(int i = 0; i < 3; ++i) {
                level.dropItem(this, Item.get(ItemID.HONEYCOMB));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    public void honeyCollected(Player player) {
        honeyCollected(player, level.getServer().getDifficulty() > 0 && !player.isCreative());
    }
    
    public void honeyCollected(Player player, boolean angerBees) {
        setHoneyLevel(0);
        if (down().getId() != CAMPFIRE_BLOCK && angerBees) {
            angerBees(player);
        }
    }
    
    public void angerBees(Player player) {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBeehive) {
            BlockEntityBeehive beehive = (BlockEntityBeehive) blockEntity;
            beehive.angerBees(player);
        }
    }
    
    @Override
    public Item toItem() {
        Item item = Item.get(getItemId(), 0, 1);
        if (level != null) {
            BlockEntity entity = level.getBlockEntity(this);
            if (entity instanceof BlockEntityBeehive) {
                BlockEntityBeehive beehive = (BlockEntityBeehive) entity;
                entity.saveNBT();
                if (!beehive.isHoneyEmpty() || !beehive.isEmpty()) {
                    CompoundTag copy = entity.namedTag.copy();
                    copy.putByte("HoneyLevel", getHoneyLevel());
                    item.setCustomBlockData(copy);
                }
            }
        }
        return item;
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean mustSilkTouch(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        if (player != null) {
            BlockEntity blockEntity = getLevel().getBlockEntity(this);
            if (blockEntity instanceof BlockEntityBeehive && !((BlockEntityBeehive) blockEntity).isEmpty()) {
                return true;
            }
        }
        return super.mustSilkTouch(vector, layer, face, item, player);
    }

    @Override
    public boolean mustDrop(Vector3 vector, int layer, BlockFace face, Item item, Player player) {
        return mustSilkTouch(vector, layer, face, item, player) || super.mustDrop(vector, layer, face, item, player);
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ new ItemBlock(new BlockBeehive()) };
    }
    
    public BlockEntityBeehive getOrCreateEntity() {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBeehive) {
            return (BlockEntityBeehive) blockEntity;
        } else {
            return createEntity(null);
        }
    }
    
    private BlockEntityBeehive createEntity(CompoundTag customNbt) {
        CompoundTag nbt = customNbt != null? customNbt.copy() : new CompoundTag();
        nbt.setName("");
        
        nbt.putString("id", BlockEntity.BEEHIVE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
        
        return (BlockEntityBeehive) BlockEntity.createBlockEntity(BlockEntity.BEEHIVE, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
    }
    
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
    }
    
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }

    public void setHoneyLevel(int honeyLevel) {
        setPropertyValue(HONEY_LEVEL, honeyLevel);
    }

    public int getHoneyLevel() {
        return getPropertyValue(HONEY_LEVEL);
    }

    public boolean isEmpty() {
        return getHoneyLevel() == HONEY_LEVEL.getMinValue();
    }

    public boolean isFull() {
        return getPropertyValue(HONEY_LEVEL) == HONEY_LEVEL.getMaxValue();
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride() {
        return getHoneyLevel();
    }
}
