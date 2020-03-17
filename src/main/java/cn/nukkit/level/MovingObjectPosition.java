package cn.nukkit.level;

import cn.nukkit.entity.Entity;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MovingObjectPosition {

    /**
     * 0 = block, 1 = entity
     */
    public int typeOfHit;

    public Vector3i blockPos;

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    public int sideHit;

    public Vector3f hitVector;

    public Entity entityHit;

    public static MovingObjectPosition fromBlock(Vector3i blockPos, int side, Vector3f hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockPos = blockPos;
        objectPosition.hitVector = hitVector;
        return objectPosition;
    }

    public static MovingObjectPosition fromEntity(Entity entity) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 1;
        objectPosition.entityHit = entity;
        objectPosition.hitVector = entity.getPosition();
        return objectPosition;
    }
}
