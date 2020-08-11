package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.List;

public class BlockBeehive extends BlockSolidMeta implements Faceable {
    public BlockBeehive() {
        this(0);
    }

    protected BlockBeehive(int meta) {
        super(meta);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
    }

    @Override
    public int getId() {
        return BEEHIVE;
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
            setDamage(0);
        } else {
            setDamage(player.getDirection().getOpposite().getHorizontalIndex());
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
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }
    
    @Override
    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            face = BlockFace.NORTH;
        }
        setDamage((getDamage() & (DATA_MASK & ~0b11)) | (face.getHorizontalIndex() & 0b11));
    }

    public void setHoneyLevel(int honeyLevel) {
        honeyLevel = NukkitMath.clamp(honeyLevel, 0, 5);
        setDamage(getDamage() & (DATA_MASK & ~0b11100) | honeyLevel << 2);
    }

    public int getHoneyLevel() {
        return getDamage() >> 2 & 0b111;
    }

    public boolean isEmpty() {
        return (getDamage() & 0b11100) == 0;
    }

    public boolean isFull() {
        return (getDamage() & 0b11100) == 0b11100;
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
