package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBed;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.MainLogger;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockBed extends BlockTransparentMeta implements Faceable, BlockEntityHolder<BlockEntityBed> {

    public BlockBed() {
        this(0);
    }

    public BlockBed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BED_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityBed> getBlockEntityClass() {
        return BlockEntityBed.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.BED;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public String getName() {
        return this.getDyeColor().getName() + " Bed Block";
    }

    @Override
    public double getMaxY() {
        return this.y + 0.5625;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean onActivate(@Nonnull Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {

        if (this.level.getDimension() == Level.DIMENSION_NETHER || this.level.getDimension() == Level.DIMENSION_THE_END) {
            CompoundTag tag = EntityPrimedTNT.getDefaultNBT(this).putShort("Fuse", 0);
            new EntityPrimedTNT(this.level.getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4), tag);
            return true;
        }

        Block blockNorth = this.north();
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block b;
        if ((this.getDamage() & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == this.getId() && (blockNorth.getDamage() & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getDamage() & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == this.getId() && (blockEast.getDamage() & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == this.getId() && (blockWest.getDamage() & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                }

                return true;
            }
        }

        if (player != null) {
            Location spawn = Location.fromObject(b.add(0.5, 0.5, 0.5), player.getLevel(), player.getYaw(), player.getPitch());
            if (!player.getSpawn().equals(spawn)) {
                player.setSpawn(spawn);
                player.sendMessage(new TranslationContainer("tile.bed.respawnSet"));
            }
        }

        int time = this.getLevel().getTime() % Level.TIME_FULL;

        boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);

        if (player != null && !isNight) {
            player.sendMessage(new TranslationContainer("tile.bed.noSleep"));
            return true;
        }

        if (player != null && !player.sleepOn(b)) {
            player.sendMessage(new TranslationContainer("tile.bed.occupied"));
        }


        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (!(BlockLever.isSupportValid(down, BlockFace.UP) || down instanceof BlockCauldron)) {
            return false;
        }
        
        Block next = this.getSide(player.getDirection());
        Block downNext = next.down();

        if (!next.canBeReplaced() || !(BlockLever.isSupportValid(downNext, BlockFace.UP) || downNext instanceof BlockCauldron)) {
            return false;
        }
        
        Block thisLayer0 = level.getBlock(this, 0);
        Block thisLayer1 = level.getBlock(this, 1);
        Block nextLayer0 = level.getBlock(next, 0);
        Block nextLayer1 = level.getBlock(next, 1);
        
        int meta = player.getDirection().getHorizontalIndex();

        this.getLevel().setBlock(block, Block.get(this.getId(), meta), true, true);
        if (next instanceof BlockLiquid && ((BlockLiquid) next).usesWaterLogging()) {
            this.getLevel().setBlock(next, 1, next, true, false);
        }
        this.getLevel().setBlock(next, Block.get(this.getId(), meta | 0x08), true, true);
        
        BlockEntityBed thisBed = null;
        try {
            thisBed = createBlockEntity(new CompoundTag().putByte("color", item.getDamage()));
            BlockEntityHolder<?> nextBlock = (BlockEntityHolder<?>) next.getLevelBlock();
            nextBlock.createBlockEntity(new CompoundTag().putByte("color", item.getDamage()));
        } catch (Exception e) {
            MainLogger.getLogger().warning("Failed to create the block entity "+getBlockEntityType()+" at "+getLocation()+" and "+next.getLocation(), e);
            if (thisBed != null) {
                thisBed.close();
            }
            level.setBlock(thisLayer0, 0, thisLayer0, true);
            level.setBlock(thisLayer1, 1, thisLayer1, true);
            level.setBlock(nextLayer0, 0, nextLayer0, true);
            level.setBlock(nextLayer1, 1, nextLayer1, true);
            return false;
        }
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        Block blockNorth = this.north(); //Gets the blocks around them
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        if ((this.getDamage() & 0x08) == 0x08) { //This is the Top part of bed
            if (blockNorth.getId() == BED_BLOCK && (blockNorth.getDamage() & 0x08) != 0x08) { //Checks if the block ID&&meta are right
                this.getLevel().setBlock(blockNorth, Block.get(BlockID.AIR), true, true);
            } else if (blockSouth.getId() == BED_BLOCK && (blockSouth.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockSouth, Block.get(BlockID.AIR), true, true);
            } else if (blockEast.getId() == BED_BLOCK && (blockEast.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockEast, Block.get(BlockID.AIR), true, true);
            } else if (blockWest.getId() == BED_BLOCK && (blockWest.getDamage() & 0x08) != 0x08) {
                this.getLevel().setBlock(blockWest, Block.get(BlockID.AIR), true, true);
            }
        } else { //Bottom Part of Bed
            if (blockNorth.getId() == this.getId() && (blockNorth.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockNorth, Block.get(BlockID.AIR), true, true);
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockSouth, Block.get(BlockID.AIR), true, true);
            } else if (blockEast.getId() == this.getId() && (blockEast.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockEast, Block.get(BlockID.AIR), true, true);
            } else if (blockWest.getId() == this.getId() && (blockWest.getDamage() & 0x08) == 0x08) {
                this.getLevel().setBlock(blockWest, Block.get(BlockID.AIR), true, true);
            }
        }

        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, false); // Do not update both parts to prevent duplication bug if there is two fallable blocks top of the bed

        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBed(this.getDyeColor().getWoolData());
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntityBed blockEntity = getBlockEntity();

            if (blockEntity != null) {
                return blockEntity.getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
