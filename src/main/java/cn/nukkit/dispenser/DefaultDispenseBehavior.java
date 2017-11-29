package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class DefaultDispenseBehavior implements DispenseBehavior {

    @Override
    public void dispense(BlockDispenser block, Item stack) {

    }

    private int getParticleMetadataForFace(BlockFace face) {
        return face.getXOffset() + 1 + (face.getZOffset() + 1) * 3;
    }

    protected void playDispenseSound(Block block) {
        block.getLevel().add(block.getLocation(), Effect.CLICK2, 0);
    }

    protected void spawnDispenseParticles(GlowBlock block, BlockFace facing) {
        block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, getParticleMetadataForFace(facing));
    }
}
