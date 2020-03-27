package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class DefaultDispenseBehavior implements DispenseBehavior {

    public boolean success = true;

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Vector3f dispensePos = block.getDispensePosition();

        if (face.getAxis() == Axis.Y) {
            dispensePos = dispensePos.sub(0, 0.125, 0);
        } else {
            dispensePos = dispensePos.sub(0, 0.15625, 0);
        }

        Random rand = ThreadLocalRandom.current();

        float offset = rand.nextFloat() * 0.1f + 0.2f;

        float motionX = face.getXOffset() * offset;
        float motionY = 0.2f;
        float motionZ = face.getZOffset() * offset;

        motionX += rand.nextGaussian() * 0.0075f * 6;
        motionY += rand.nextGaussian() * 0.0075f * 6;
        motionZ += rand.nextGaussian() * 0.0075f * 6;

        Item clone = item.clone();
        clone.setCount(1);

        block.getLevel().dropItem(dispensePos, clone, Vector3f.from(motionX, motionY, motionZ));
        return null;
    }

    private int getParticleMetadataForFace(BlockFace face) {
        return face.getXOffset() + 1 + (face.getZOffset() + 1) * 3;
    }
}
