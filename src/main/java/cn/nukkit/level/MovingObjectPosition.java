package cn.nukkit.level;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MovingObjectPosition {

    /**
     * 0 = block, 1 = entity
     */
    public int typeOfHit;

    public int blockX;
    public int blockY;
    public int blockZ;

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    public int sideHit;

    public Vector3 hitVector;

    public Entity entityHit;

    public static MovingObjectPosition fromBlock(int x, int y, int z, int side, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.hitVector = new Vector3(hitVector.x, hitVector.y, hitVector.z);
        return objectPosition;
    }

    public static MovingObjectPosition fromEntity(Entity entity) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 1;
        objectPosition.entityHit = entity;
        objectPosition.hitVector = new Vector3(entity.x, entity.y, entity.z);
        return objectPosition;
    }
}
