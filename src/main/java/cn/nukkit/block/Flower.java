package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Flower extends Flowable {
    public static final int TYPE_POPPY = 0;
    public static final int TYPE_BLUE_ORCHID = 1;
    public static final int TYPE_ALLIUM = 2;
    public static final int TYPE_AZURE_BLUET = 3;
    public static final int TYPE_RED_TULIP = 4;
    public static final int TYPE_ORANGE_TULIP = 5;
    public static final int TYPE_WHITE_TULIP = 6;
    public static final int TYPE_PINK_TULIP = 7;
    public static final int TYPE_OXEYE_DAISY = 8;

    public Flower() {
        this(0);
    }

    public Flower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FLOWER;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Poppy",
                "Blue Orchid",
                "Allium",
                "Azure Bluet",
                "Red Tulip",
                "Orange Tulip",
                "White Tulip",
                "Pink Tulip",
                "Oxeye Daisy",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown"
        };
        return names[this.meta & 0x0f];
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (down.getId() == Block.GRASS || down.getId() == Block.DIRT || down.getId() == Block.FARMLAND || down.getId() == Block.PODZOL) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(Vector3.SIDE_DOWN).isTransparent()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }
    
    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{this.getId(), this.meta, 1}};
    }
}
