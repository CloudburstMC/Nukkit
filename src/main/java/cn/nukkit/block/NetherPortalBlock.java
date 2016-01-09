package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.DataPacket;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.block in project nukkit .
 * The name NetherPortalBlock comes from minecraft wiki.
 */
public class NetherPortalBlock extends Flowable {

    public NetherPortalBlock() {
        this(0);
    }

    public NetherPortalBlock(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Nether Portal Block";
    }

    @Override
    public int getId() {
        return NETHER_PORTAL;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{};
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    public boolean onBreak(Item item) {
        boolean result = super.onBreak(item);
        for (int side = 0; side <= 5; side++) {
            Block b = this.getSide(side);
            if (b != null) {
                if (b instanceof NetherPortalBlock) {
                    result &= b.onBreak(item);
                }
            }
        }
        return result;
    }

    //todo teleport to the nether

    /*
    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof Player) {
            //?
        }
    }
    */

}
