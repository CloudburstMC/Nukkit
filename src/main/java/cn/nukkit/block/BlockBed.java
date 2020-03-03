package cn.nukkit.block;

import cn.nukkit.blockentity.Bed;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.BED;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockBed extends BlockTransparent implements Faceable {

    public BlockBed(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.5625f;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {

        Block blockNorth = this.north();
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block b;
        if ((this.getMeta() & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == this.getId() && (blockNorth.getMeta() & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getMeta() & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == this.getId() && (blockEast.getMeta() & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == this.getId() && (blockWest.getMeta() & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage(new TranslationContainer("tile.bed.notValid"));
                }

                return true;
            }
        }

        Location spawn = Location.from(b.getPosition().toFloat().add(0.5, 0.5, 0.5), this.level);
        if (player != null && !player.getSpawn().equals(spawn)) {
            player.setSpawn(spawn);
            player.sendMessage(new TranslationContainer("tile.bed.respawnSet"));
        }

        int time = this.getLevel().getTime() % Level.TIME_FULL;

        boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);

        if (player != null && !isNight) {
            player.sendMessage(new TranslationContainer("tile.bed.noSleep"));
            return true;
        }

        if (player != null && !player.sleepOn(b.getPosition())) {
            player.sendMessage(new TranslationContainer("tile.bed.occupied"));
        }


        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = this.down();
        if (!down.isTransparent() || down instanceof BlockSlab) {
            Block next = this.getSide(player.getHorizontalFacing());
            Block downNext = next.down();

            if (next.canBeReplaced() && (!downNext.isTransparent() || downNext instanceof BlockSlab)) {
                int meta = player.getDirection().getHorizontalIndex();

                this.getLevel().setBlock(block.getPosition(), Block.get(this.getId(), meta), true, true);
                if (next instanceof BlockLiquid && ((BlockLiquid) next).usesWaterLogging()) {
                    this.getLevel().setBlock(next.getPosition(), 1, next.clone(), true, false);
                }
                this.getLevel().setBlock(next.getPosition(), Block.get(this.getId(), meta | 0x08), true, true);

                createBlockEntity(this.getPosition(), item.getMeta());
                createBlockEntity(next.getPosition(), item.getMeta());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        Block blockNorth = this.north(); //Gets the blocks around them
        Block blockSouth = this.south();
        Block blockEast = this.east();
        Block blockWest = this.west();

        Block air = Block.get(AIR);
        Block otherPart = null;
        if ((this.getMeta() & 0x08) == 0x08) { //This is the Top part of bed
            if (blockNorth.getId() == BED && (blockNorth.getMeta() & 0x08) != 0x08) { //Checks if the block ID&&meta are right
                otherPart = blockNorth;
            } else if (blockSouth.getId() == BED && (blockSouth.getMeta() & 0x08) != 0x08) {
                otherPart = blockSouth;
            } else if (blockEast.getId() == BED && (blockEast.getMeta() & 0x08) != 0x08) {
                otherPart = blockEast;
            } else if (blockWest.getId() == BED && (blockWest.getMeta() & 0x08) != 0x08) {
                otherPart = blockWest;
            }
        } else { //Bottom Part of Bed
            if (blockNorth.getId() == this.getId() && (blockNorth.getMeta() & 0x08) == 0x08) {
                otherPart = blockNorth;
            } else if (blockSouth.getId() == this.getId() && (blockSouth.getMeta() & 0x08) == 0x08) {
                otherPart = blockSouth;
            } else if (blockEast.getId() == this.getId() && (blockEast.getMeta() & 0x08) == 0x08) {
                otherPart = blockEast;
            } else if (blockWest.getId() == this.getId() && (blockWest.getMeta() & 0x08) == 0x08) {
                otherPart = blockWest;
            }
        }

        if (otherPart instanceof BlockBed) {
            otherPart.removeBlock(false); // Do not update both parts to prevent duplication bug if there is two fallable blocks top of the bed
        }

        removeBlock(true);
        return true;
    }

    private void createBlockEntity(Vector3i pos, int color) {
        Bed bed = BlockEntityRegistry.get().newEntity(BlockEntityTypes.BED, this.level.getChunk(pos), pos);
        bed.setColor(DyeColor.getByDyeData(color));
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.BED, this.getDyeColor().getWoolData());
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

            if (blockEntity instanceof Bed) {
                return ((Bed) blockEntity).getColor();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x7);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
