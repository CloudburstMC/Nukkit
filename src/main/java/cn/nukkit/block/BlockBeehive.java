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
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import java.util.List;

public class BlockBeehive extends BlockSolidMeta implements Faceable {
    public BlockBeehive() {
        this(0);
    }

    protected BlockBeehive(int meta) {
        super(meta & 0xF);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta & 0xF);
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
        int honeyLevel = item.hasCustomBlockData()? item.getCustomBlockData().getByte("HoneyLevel") : 0;
        setDisplayedHoneyLevel(honeyLevel);
        BlockEntityBeehive beehive = createEntity(item.getCustomBlockData());
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
        if (angerBees) {
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
                    item.setCustomBlockData(entity.namedTag);
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
    public boolean canHarvestWithHand() {
        return true;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ new ItemBlock(new BlockBeehive()) };
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
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
        
        if (!nbt.contains("HoneyLevel")) {
            nbt.putByte("HoneyLevel", getDisplayedHoneyLevel());
        }

        return new BlockEntityBeehive(this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
    }

    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex >= 0) {
            setDamage((getDamage() & 0b1100) | (horizontalIndex & 0b0011));
            level.setBlock(this, this, true, true);
        }
    }

    public void setHoneyLevel(int honeyLevel) {
        BlockEntityBeehive entityBeehive = getOrCreateEntity();
        entityBeehive.setHoneyLevel(honeyLevel);
    }

    public int getHoneyLevel() {
        BlockEntity blockEntity = level.getBlockEntity(this);
        if (blockEntity instanceof BlockEntityBeehive) {
            return ((BlockEntityBeehive) blockEntity).getHoneyLevel();
        } else {
            return getDisplayedHoneyLevel();
        }
    }

    public void setDisplayedHoneyLevel(int honeyLevel) {
        honeyLevel = MathHelper.clamp(honeyLevel, 0, 5);

        int honeyBits;
        switch (honeyLevel) {
            case 0:
                honeyBits = 0;
                break;
            case 1:
            case 2:
                honeyBits = 1;
                break;
            case 3:
            case 4:
                honeyBits = 2;
                break;
            case 5:
            default:
                honeyBits = 3;
                break;
        }

        setDamage(getDamage() & 0b0011 | honeyBits << 2);
        level.setBlock(this, this, true, true);
    }

    public int getDisplayedHoneyLevel() {
        int honeyLevel = getDamage() >> 2;
        switch (honeyLevel) {
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 5;
            case 0:
            default:
                return 0;
        }
    }

    public boolean isEmpty() {
        return (getDamage() & 0b1100) == 0;
    }

    public boolean isFull() {
        return (getDamage() & 0b1100) == 0b1100;
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
