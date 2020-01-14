package cn.nukkit.level.generator.object;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3i;

public abstract class BasicGenerator {

    //also autism, see below
    public abstract boolean generate(ChunkManager level, NukkitRandom rand, Vector3i position);

    public void setDecorationDefaults() {
    }
}
