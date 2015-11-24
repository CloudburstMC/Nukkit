package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Carpet extends Flowable{
    public Carpet() {
        this(0);
    }

    public Carpet(int meta) {
        super(CARPET, meta);
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "White Carpet",
                "Orange Carpet",
                "Magenta Carpet",
                "Light Blue Carpet",
                "Yellow Carpet",
                "Lime Carpet",
                "Pink Carpet",
                "Gray Carpet",
                "Light Gray Carpet",
                "Cyan Carpet",
                "Purple Carpet",
                "Blue Carpet",
                "Brown Carpet",
                "Green Carpet",
                "Red Carpet",
                "Black Carpet"
        };
        return names[this.meta & 0x0f];
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.0625,
                this.z + 1
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if(down.getId() != Item.AIR){
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if(type == Level.BLOCK_UPDATE_NORMAL){
            if(this.getSide(0).getId() == Item.AIR){
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }
}
