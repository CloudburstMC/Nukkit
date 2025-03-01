package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCaveVines extends BlockTransparentMeta {

    private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11f;

    public BlockCaveVines() {
        this(0);
    }

    public BlockCaveVines(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cave Vines";
    }

    @Override
    public int getId() {
        return CAVE_VINES;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canPlaceOn(Block floor, Position pos) {
        Block up = floor.up(2);
        return (up.isSolid() || up instanceof BlockCaveVines) && super.canPlaceOn(floor, pos);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!this.canPlaceOn(block.down(), target)) {
            return false;
        }

        Block support = block.up();
        if (isCaveVine(support)) {
            this.setVineAge(Math.min(this.getMaxAge(), ((BlockCaveVines) support).getVineAge() + 1));
        } else {
            this.setVineAge(0);
        }
        return this.getLevel().setBlock(this, this, true, true);
    }

    @Override
    public int onUpdate(int type) {
        switch (type) {
            case Level.BLOCK_UPDATE_NORMAL:
                Block up = this.up();
                if (!isCaveVine(up) && !up.isSolid()) {
                    this.getLevel().scheduleUpdate(this, 1);
                }
                break;
            case Level.BLOCK_UPDATE_SCHEDULED:
                this.getLevel().useBreakOn(this, null, null, true);
                break;
            case Level.BLOCK_UPDATE_RANDOM:
                this.tryGrow();
                break;
        }
        return type;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (this.tryPickupBerries()) {
            return true;
        }

        if (item.getId() != Item.DYE || item.getDamage() != ItemDye.BONE_MEAL) {
            return false;
        }

        Block bottom = this;
        BlockCaveVines plantHead = null;
        while (bottom instanceof BlockCaveVines) {
            plantHead = (BlockCaveVines) bottom;
            bottom = bottom.down();
        }

        if (!plantHead.tryGrow()) {
            return false;
        }

        this.level.addParticle(new BoneMealParticle(plantHead));
        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    private boolean tryPickupBerries() {
        if (!this.hasBerries()) {
            return false;
        }

        BlockCaveVines blockCaveVines = this.getStateWithoutBerries(this);
        this.getLevel().setBlock(this, blockCaveVines, false, true);

        Item item = Item.get(ItemID.GLOW_BERRIES, 0, 1);
        this.getLevel().dropItem(this.add(0.5, 0.5, 0.5), item);
        return true;
    }

    private boolean tryGrow() {
        if (this.getVineAge() >= this.getMaxAge()) {
            return false;
        }

        Block down = this.down();
        if (down.getY() <= this.getLevel().getMinBlockY() || down.getId() != Block.AIR) {
            return false;
        }

        Block topBlock = this;
        while (topBlock instanceof BlockCaveVines) {
            if (topBlock.getDamage() < this.getVineAge()) {
                break;
            }
            topBlock = topBlock.up();
        }

        if (topBlock.getDamage() >= this.getMaxAge()) {
            return false;
        }

        boolean withBerries = ThreadLocalRandom.current().nextFloat() < CHANCE_OF_BERRIES_ON_GROWTH;
        BlockCaveVines head = withBerries ? this.getStateWithBerries(down) : this.getStateWithoutBerries(down);

        BlockGrowEvent event = new BlockGrowEvent(this, head);
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        this.getLevel().setBlock(down, event.getNewState(), true, true);

        BlockCaveVines support = (BlockCaveVines) Block.get(this.hasBerries() ? CAVE_VINES_BODY_WITH_BERRIES : CAVE_VINES, this.getDamage());
        this.getLevel().setBlock(this, support, true, true);
        return true;
    }

    public BlockCaveVines getStateWithBerries(Position position) {
        if (this.getDamage() == 0) {
            return (BlockCaveVines) Block.get(CAVE_VINES_HEAD_WITH_BERRIES, 0, position);
        }
        return (BlockCaveVines) Block.get(CAVE_VINES_HEAD_WITH_BERRIES, this.getDamage(), position);
    }

    public BlockCaveVines getStateWithoutBerries(Position position) {
        return (BlockCaveVines) Block.get(CAVE_VINES, this.getDamage(), position);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!this.hasBerries()) {
            return new Item[0];
        }
        return new Item[]{ Item.get(ItemID.GLOW_BERRIES, 0, 1) };
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    public void setVineAge(int age) {
        this.setDamage(age);
    }

    public int getVineAge() {
        return this.getDamage();
    }

    public int getMaxAge() {
        return 25;
    }

    public boolean hasBerries() {
        return false;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    public static boolean isCaveVine(Block block) {
        switch (block.getId()) {
            case CAVE_VINES:
            case CAVE_VINES_BODY_WITH_BERRIES:
            case CAVE_VINES_HEAD_WITH_BERRIES:
                return true;
        }
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }
}
