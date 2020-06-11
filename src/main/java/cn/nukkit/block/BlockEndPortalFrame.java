package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockEndPortalFrame extends BlockTransparentMeta implements Faceable {

    private static final int[] FACES = {2, 3, 0, 1};

    public BlockEndPortalFrame() {
        this(0);
    }

    public BlockEndPortalFrame(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return END_PORTAL_FRAME;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "End Portal Frame";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + ((this.getDamage() & 0x04) > 0 ? 1 : 0.8125);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return (getDamage() & 4) != 0 ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if ((this.getDamage() & 0x04) == 0 && player != null && item.getId() == Item.ENDER_EYE) {
            this.setDamage(this.getDamage() + 4);
            this.getLevel().setBlock(this, this, true, true);
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_END_PORTAL_FRAME_FILL);
            this.createPortal();
            return true;
        }
        return false;
    }

    public void createPortal() {
        for (int i = 0; i < 4; i++) {
            for (int j = -1; j <= 1; j++) {
                Block side = this.getSide(BlockFace.fromHorizontalIndex(i), 2).getSide(BlockFace.fromHorizontalIndex((i + 1) % 4), j);
                if (this.isCompletedPortal(side)) {
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            this.getLevel().setBlock(side.add(k, 0, l), Block.get(Block.END_PORTAL), true);
                        }
                    }
                    this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_END_PORTAL_SPAWN);
                    return;
                }
            }
        }
    }

    public boolean isCompletedPortal(Block center) {
        for (int i = 0; i < 4; i++) {
            for (int j = -1; j <= 1; j++) {
                Block block = center.getSide(BlockFace.fromHorizontalIndex(i), 2).getSide(BlockFace.fromHorizontalIndex((i + 1) % 4), j);
                if (block.getId() != Block.END_PORTAL_FRAME || (block.getDamage() & 0x4) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
