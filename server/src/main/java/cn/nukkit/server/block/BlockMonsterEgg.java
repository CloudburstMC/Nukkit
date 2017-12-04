package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;

public class BlockMonsterEgg extends BlockSolid {
    public static final int STONE = 0;
    public static final int COBBLESTONE = 1;
    public static final int STONE_BRICK = 2;
    public static final int MOSSY_BRICK = 3;
    public static final int CRACKED_BRICK = 4;
    public static final int CHISELED_BRICK = 5;

    public BlockMonsterEgg() {
        this(0);
    }

    public BlockMonsterEgg(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MONSTER_EGG;
    }

    @Override
    public double getHardness() {
        return 0.75;
    }

    @Override
    public double getResistance() {
        return 3.75;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Stone",
                "Cobblestone",
                "Stone Brick",
                "Mossy Stone Brick",
                "Cracked Stone Brick",
                "Chiseled Stone Brick"
        };

        return names[this.meta & 0x07] + " Monster Egg";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
