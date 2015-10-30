package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bed extends Transparent {

    public Bed() {
        super(BED_BLOCK);
    }

    public Bed(int meta) {
        super(BED_BLOCK, meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public String getName() {
        return "Bed Block";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.5625,
                this.z + 1
        );
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        /*int time = this.getLevel().getTime() % Level.TIME_FULL;

        boolean isNight = (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE);

        if (player != null && !isNight) {
            player.sendMessage(TextFormat.GRAY + "You can only sleep at night");
            return true;
        }

        Block blockNorth = this.getSide(2);
        Block blockSouth = this.getSide(3);
        Block blockEast = this.getSide(5);
        Block blockWest = this.getSide(4);

        Block b;
        if ((this.meta & 0x08) == 0x08) {
            b = this;
        } else {
            if (blockNorth.getId() == this.id && (blockNorth.meta & 0x08) == 0x08) {
                b = blockNorth;
            } else if (blockSouth.getId() == this.id && (blockSouth.meta & 0x08) == 0x08) {
                b = blockSouth;
            } else if (blockEast.getId() == this.id && (blockEast.meta & 0x08) == 0x08) {
                b = blockEast;
            } else if (blockWest.getId() == this.id && (blockWest.meta & 0x08) == 0x08) {
                b = blockWest;
            } else {
                if (player != null) {
                    player.sendMessage(TextFormat.GRAY + "This bed is incomplete");
                }

                return true;
            }
        }
*/
        //todo
        return false;
    }
}
