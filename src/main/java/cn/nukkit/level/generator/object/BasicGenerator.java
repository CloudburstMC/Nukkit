package cn.nukkit.level.generator.object;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3i;

public abstract class BasicGenerator {

    //also autism, see below
    public abstract boolean generate(ChunkManager level, BedrockRandom random, Vector3i position);

    public void setDecorationDefaults() {
    }
}
