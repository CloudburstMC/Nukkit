package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsMelon;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Faceable;

/**
 * @author Pub4Game
 * @since 15.01.2016
 * 
 * @apiNote Implements {@link Faceable} only on PowerNukkit since 1.3.0.0-PN
 */
@PowerNukkitOnly("Implements Faceable only on PowerNukkit since 1.3.0.0-PN")
public class BlockStemMelon extends BlockCrops implements Faceable {

    public BlockStemMelon() {
        this(0);
    }

    public BlockStemMelon(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MELON_STEM;
    }

    @Override
    public String getName() {
        return "Melon Stem";
    }

    @PowerNukkitOnly("Implements Faceable only on PowerNukkit since 1.3.0.0-PN")
    @Since("1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() >> 3 & 0b111);
    }
    
    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlockFace(BlockFace face) {
        setDamage(getDamage() & ~(0b111 << 3) | (face.getIndex() << 3));
    }

    @PowerNukkitDifference(info = "Will bind to the melon by the server-side", since = "1.3.0.0-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            NukkitRandom random = new NukkitRandom();
            if (random.nextRange(1, 2) == 1 && getLevel().getFullLight(this) >= MINIMUM_LIGHT_LEVEL) {
                if (this.getDamage() < 0x07) {
                    Block block = this.clone();
                    block.setDamage(block.getDamage() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true);
                    }
                    return Level.BLOCK_UPDATE_RANDOM;
                } else {
                    for (BlockFace face : Plane.HORIZONTAL) {
                        Block b = this.getSide(face);
                        if (b.getId() == MELON_BLOCK) {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    }
                    BlockFace sideFace = Plane.HORIZONTAL.random(random);
                    Block side = this.getSide(sideFace);
                    Block d = side.down();
                    if (side.getId() == AIR && (d.getId() == FARMLAND || d.getId() == GRASS || d.getId() == DIRT)) {
                        BlockGrowEvent ev = new BlockGrowEvent(side, Block.get(BlockID.MELON_BLOCK));
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(side, ev.getNewState(), true);
                            setBlockFace(sideFace);
                            this.getLevel().setBlock(this, this, true);
                        }
                    }
                }
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemSeedsMelon();
    }

    @Override
    public Item[] getDrops(Item item) {
        NukkitRandom random = new NukkitRandom();
        return new Item[]{
                new ItemSeedsMelon(0, random.nextRange(0, 3))
        };
    }
}
