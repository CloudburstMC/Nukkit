package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Redstone;
import cn.nukkit.api.util.data.BlockFace;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

/**
 * @author CreeperFace
 */
public class Lever extends Directional implements Redstone {

    @Getter
    @Setter
    private boolean powered;

    private Lever(BlockFace face) {
        this.setFace(face);
    }

    public static Lever of(BlockFace face) {
        Preconditions.checkArgument(face != null, "BlockFace cannot be null");
        return new Lever(face);
    }
}
