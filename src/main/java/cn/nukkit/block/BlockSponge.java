package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.CloudParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Angelic47 (Nukkit Project)
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
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Level level = block.getLevel();
        boolean blockSet = level.setBlock(block, this);

        if (blockSet) {
            if (this.getDamage() == WET && level.getDimension() == Level.DIMENSION_NETHER) {
                level.setBlock(block, Block.get(BlockID.SPONGE, DRY));
                this.getLevel().addSound(block.getLocation(), Sound.RANDOM_FIZZ);

                for (int i = 0; i < 8; ++i) {
                    this.getLevel().addParticle(
                            new CloudParticle(block.getLocation().add(Math.random(), 1, Math.random()))
                    );
                }
            } else if (this.getDamage() == DRY && performWaterAbsorb(block)) {
                level.setBlock(block, Block.get(BlockID.SPONGE, WET));

                for (int i = 0; i < 4; i++) {
                    level.addLevelEvent(
                            LevelEventPacket.EVENT_PARTICLE_DESTROY, 
                            GlobalBlockPalette.getOrCreateRuntimeId(BlockID.WATER, 0),
                            block 
                    );
                }
            }
        }
        return blockSet;
    }

    private boolean performWaterAbsorb(Block block) {
        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.values()) {

                Block faceBlock = entry.block.getSide(face);
                if (faceBlock.getId() == BlockID.WATER || faceBlock.getId() == BlockID.STILL_WATER) {
                    this.level.setBlock(faceBlock, Block.get(BlockID.AIR));
                    ++waterRemoved;
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                } else if (faceBlock.getId() == BlockID.AIR) {
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                }
            }
        }
        return waterRemoved > 0;
    }

    private static class Entry {
        private final Block block;
        private final int distance;

        public Entry(Block block, int distance) {
            this.block = block;
            this.distance = distance;
        }
    }
}
