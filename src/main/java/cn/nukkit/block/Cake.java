package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;

/**
 * @author Nukkit Project Team
 */
public class Cake extends Transparent {

    public Cake(int meta) {
        super(meta);
    }

    public Cake() {
        this(0);
    }

    @Override
    public String getName() {
        return "Cake Block";
    }

    @Override
    public int getId() {
        return Block.CAKE_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                x + (1 + getDamage() * 2) / 16,
                y,
                z + 0.0625,
                x - 0.0625 + 1,
                y + 0.5,
                z - 0.0625 + 1
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (getSide(0).getId() != Block.AIR) {
            getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (getSide(0).getId() == Block.AIR) {
                getLevel().setBlock(this, new Air(), true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[0][];
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null && player.getHealth() < player.getMaxHealth()) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, 3, EntityRegainHealthEvent.CAUSE_EATING);
            player.heal(3, event);
            if (!event.isCancelled()) {
                if (++meta >= 0x06) {
                    getLevel().setBlock(this, new Air(), true);
                } else {
                    getLevel().setBlock(this, this, true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Color getMapColor() {
        return Color.airColor;
    }
}
