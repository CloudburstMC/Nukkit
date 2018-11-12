package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSponge extends BlockSolidMeta {

    public static final int DRY = 0;
    public static final int WET = 1;
    private static final String[] NAMES = new String[]{
            "Sponge",
            "Wet sponge"
    };

    public BlockSponge() {
        this(0);
    }

    public BlockSponge(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPONGE;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0b1];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getDamage() == 0 && (block.getId() == Block.WATER || block.getId() == Block.STILL_WATER)) {
            Vector3 vector = new Vector3(0, 0, 0);
            Vector3 vBlock = new Vector3(0, 0, 0);
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    for (int k = 0; k < 16; ++k) {
                        if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                            vector.setComponents((double) i / (double) 15 * 2d - 1, (double) j / (double) 15 * 2d - 1, (double) k / (double) 15 * 2d - 1);
                            double len = vector.length();
                            vector.setComponents((vector.x / len) * 0.3d, (vector.y / len) * 0.3d, (vector.z / len) * 0.3d);
                            double pointerX = this.x;
                            double pointerY = this.y;
                            double pointerZ = this.z;
                            for (double rand = 3 * (ThreadLocalRandom.current().nextInt(700, 1301)) / 1000d; rand > 0; rand -= 0.3d * 0.75d) {
                                int x = (int) pointerX;
                                int y = (int) pointerY;
                                int z = (int) pointerZ;
                                vBlock.x = pointerX >= x ? x : x - 1;
                                vBlock.y = pointerY >= y ? y : y - 1;
                                vBlock.z = pointerZ >= z ? z : z - 1;
                                if (vBlock.y < 0 || vBlock.y > 255) {
                                    break;
                                }
                                Block b = this.level.getBlock(vBlock);
                                if (b.getId() == Block.WATER || b.getId() == Block.STILL_WATER) {
                                    if (rand > 0) {
                                        this.level.setBlock(b, Block.get(0));
                                    }
                                }
                                pointerX += vector.x;
                                pointerY += vector.y;
                                pointerZ += vector.z;
                            }
                        }
                    }
                }
            }
            this.level.setBlock(this, Block.get(Block.SPONGE, 1), true, true);
        } else {
            this.level.setBlock(this, Block.get(Block.SPONGE, this.getDamage()), true, true);
        }
        return true;
    }
}
