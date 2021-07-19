package cn.nukkit.network.protocol.types;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class StructureSettings {

    public String paletteName;
    public boolean ignoreEntities;
    public boolean ignoreBlocks;
    public int structureSizeX;
    public int structureSizeY;
    public int structureSizeZ;
    public int structureOffsetX;
    public int structureOffsetY;
    public int structureOffsetZ;
    public long lastTouchedByPlayerID;
    public byte rotation;
    public byte mirror;
    public float integrityValue;
    public int integritySeed;
    public Vector3f pivot;
}
