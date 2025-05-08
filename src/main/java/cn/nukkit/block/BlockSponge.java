package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockSponge extends BlockSolidMeta {

    public static final int DRY = 0;
    public static final int WET = 1;

    private static final String[] NAMES = {
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
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getDamage() == WET && level.getDimension() == Level.DIMENSION_NETHER) {
            level.setBlock(block, Block.get(BlockID.SPONGE, DRY), true, true);
            level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_FIZZ);
            level.addParticle(new ExplodeParticle(block.add(0.5, 1, 0.5)));
            return true;
        } else if (this.getDamage() == DRY && performWaterAbsorb(block)) {
            level.setBlock(block, Block.get(BlockID.SPONGE, WET), true, true);

            Map<Integer, Player> players = this.level.getChunkPlayers(block.getChunkX(), block.getChunkZ());
            level.addParticle(new DestroyBlockParticle(block.add(0.5, 0.5, 0.5), Block.get(BlockID.WATER)), players.values().toArray(new Player[0]));
            return true;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    private boolean performWaterAbsorb(Block block) {
        boolean waterFound = false;
        for (BlockFace side : BlockFace.values()) {
            if (getSide(side) instanceof BlockWater) {
                waterFound = true;
                break;
            }
        }
        if (!waterFound) {
            return false;
        }

        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.values()) {

                Block faceBlock = entry.block.getSide(face);
                if (Block.isWater(faceBlock.getId())) {
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
