package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.SpongeType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.CloudParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockSponge extends BlockSolidMeta {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<SpongeType> SPONGE_TYPE = new ArrayBlockProperty<>("sponge_type", true, SpongeType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SPONGE_TYPE);

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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
    public int getToolType() {
        return ItemTool.TYPE_HOE;
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
        if (this.getDamage() == WET && level.getDimension() == Level.DIMENSION_NETHER) {
            level.setBlock(block, Block.get(BlockID.SPONGE, DRY), true, true);
            this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE);

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; ++i) {
                level.addParticle(new CloudParticle(block.getLocation().add(random.nextDouble(), 1, random.nextDouble())));
            }

            return true;
        } else if (this.getDamage() == DRY && block instanceof BlockWater && performWaterAbsorb(block)) {
            level.setBlock(block, Block.get(BlockID.SPONGE, WET), true, true);

            for (int i = 0; i < 4; i++) {
                LevelEventPacket packet = new LevelEventPacket();
                packet.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY;
                packet.x = (float) block.getX() + 0.5f;
                packet.y = (float) block.getY() + 1f;
                packet.z = (float) block.getZ() + 0.5f;
                packet.data = GlobalBlockPalette.getOrCreateRuntimeId(BlockID.WATER, 0);
                level.addChunkPacket(getChunkX(), getChunkZ(), packet);
            }

            return true;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    private boolean performWaterAbsorb(Block block) {
        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.values()) {
                Block faceBlock = entry.block.getSideAtLayer(0, face);
                Block faceBlock1 = faceBlock.getLevelBlockAtLayer(1);
                
                if (faceBlock instanceof BlockWater) {
                    this.getLevel().setBlockStateAt(faceBlock.getFloorX(), faceBlock.getFloorY(), faceBlock.getFloorZ(), BlockState.AIR);
                    this.getLevel().updateAround(faceBlock);
                    waterRemoved++;
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                } else if (faceBlock1 instanceof BlockWater) {
                    if (faceBlock.getId() == BlockID.BLOCK_KELP || faceBlock.getId() == BlockID.SEAGRASS || faceBlock.getId() == BlockID.SEA_PICKLE || faceBlock instanceof BlockCoralFan) {
                        faceBlock.getLevel().useBreakOn(faceBlock);
                    }
                    this.getLevel().setBlockStateAt(faceBlock1.getFloorX(), faceBlock1.getFloorY(), faceBlock1.getFloorZ(), 1, BlockState.AIR);
                    this.getLevel().updateAround(faceBlock1);
                    waterRemoved++;
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock1, entry.distance + 1));
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
