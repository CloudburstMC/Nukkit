package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockPinkPetals extends BlockFlowable {

    private static final int DIRECTION_MASK = 0b00011;
    private static final int GROWTH_MASK = 0b11100;

    public BlockPinkPetals() {
        this(0);
    }

    public BlockPinkPetals(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PINK_PETALS;
    }

    @Override
    public String getName() {
        return "Pink Petals";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == PINK_PETALS && face == BlockFace.UP) {
            int damage = target.getDamage();
            int growth = (damage & GROWTH_MASK) >> 2;
            if (growth < 3) {
                growth++;
                target.setDamage((damage & ~GROWTH_MASK) | ((growth << 2) & GROWTH_MASK));
                this.getLevel().setBlock(target, target, true, true);
                return true;
            }
            return false;
        }

        Block down = this.down();
        int id = down.getId();
        if (id == Block.GRASS || id == Block.DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS) {
            int direction = player != null ? player.getDirection().getOpposite().getHorizontalIndex() : 0;
            this.setDamage(direction & DIRECTION_MASK);
            this.getLevel().setBlock(this, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == Item.AIR) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, this.getFlowerCount());
    }

    public int getFlowerCount() {
        return ((this.getDamage() & GROWTH_MASK) >> 2) + 1;
    }
}
