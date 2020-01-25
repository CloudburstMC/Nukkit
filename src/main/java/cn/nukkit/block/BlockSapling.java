package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.Vector3i;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.DYE;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSapling extends FloodableBlock {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    /**
     * placeholder
     */
    public static final int BIRCH_TALL = 8 | BIRCH;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public BlockSapling(Identifier id) {
        super(id);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = this.down();
        if (down.getId() == GRASS || down.getId() == DIRT || down.getId() == FARMLAND || down.getId() == PODZOL) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getDamage() == 0x0F) { //BoneMeal
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.decrementCount();
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            this.grow();

            return true;
        }
        return false;
    }

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) == 1) {
                if ((this.getDamage() & 0x08) == 0x08) {
                    this.grow();
                } else {
                    this.setDamage(this.getDamage() | 0x08);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return Level.BLOCK_UPDATE_NORMAL;
    }

    private void grow() {
        //porktodo: fix this
        /*
        BasicGenerator generator = null;
        boolean bigTree = false;

        int x = 0;
        int z = 0;

        switch (this.getDamage() & 0x07) {
            case JUNGLE:
                loop:
                for (; x >= -1; --x) {
                    for (; z >= -1; --z) {
                        if (this.findSaplings(x, z, JUNGLE)) {
                            generator = new ObjectJungleBigTree(10, 20,
                                    Block.get(LOG, BlockLog.JUNGLE), Block.get(LEAVES, BlockLeaves.JUNGLE));
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    x = 0;
                    z = 0;
                    generator = new NewJungleTree(4, 7);
                }
                break;
            case ACACIA:
                generator = new ObjectSavannaTree();
                break;
            case DARK_OAK:
                loop:
                for (; x >= -1; --x) {
                    for (; z >= -1; --z) {
                        if (this.findSaplings(x, z, DARK_OAK)) {
                            generator = new ObjectDarkOakTree();
                            bigTree = true;
                            break loop;
                        }
                    }
                }

                if (!bigTree) {
                    return;
                }
                break;
            //TODO: big spruce
            default:
                ObjectTree.growTree(this.level, this.getX(), this.getY(), this.getZ(), new BedrockRandom(), this.getDamage() & 0x07);
                return;
        }

        if (bigTree) {
            this.level.setBlock(this.add(x, 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1, 0, z), get(AIR), true, false);
            this.level.setBlock(this.add(x, 0, z + 1), get(AIR), true, false);
            this.level.setBlock(this.add(x + 1, 0, z + 1), get(AIR), true, false);
        } else {
            this.level.setBlock(this, get(AIR), true, false);
        }

        if (!generator.generate(this.level, new BedrockRandom(), this.add(x, 0, z).asVector3i())) {
            if (bigTree) {
                this.level.setBlock(this.add(x, 0, z), this, true, false);
                this.level.setBlock(this.add(x + 1, 0, z), this, true, false);
                this.level.setBlock(this.add(x, 0, z + 1), this, true, false);
                this.level.setBlock(this.add(x + 1, 0, z + 1), this, true, false);
            } else {
                this.level.setBlock(this, this, true, false);
            }
        }*/
    }

    private boolean findSaplings(int x, int z, int type) {
        return this.isSameType(this.add(x, 0, z), type) && this.isSameType(this.add(x + 1, 0, z), type) && this.isSameType(this.add(x, 0, z + 1), type) && this.isSameType(this.add(x + 1, 0, z + 1), type);
    }

    public boolean isSameType(Vector3i pos, int type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && (block.getDamage() & 0x07) == (type & 0x07);
    }

    @Override
    public Item toItem() {
        return Item.get(SAPLING, this.getDamage() & 0x7);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
