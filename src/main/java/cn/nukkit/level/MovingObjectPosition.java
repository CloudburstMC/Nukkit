package cn.nukkit.level;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
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
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic numbers and not encapsulated", replaceWith = "getFaceHit(), setFaceHit(BlockFace)")
    public int sideHit;

    public Vector3 hitVector;

    public Entity entityHit;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @SuppressWarnings("java:S1874")
    public BlockFace getFaceHit() {
        switch (sideHit) {
            case 0:
                return BlockFace.DOWN;
            case 1:
                return BlockFace.UP;
            case 2:
                return BlockFace.EAST;
            case 3:
                return BlockFace.WEST;
            case 4:
                return BlockFace.NORTH;
            case 5:
                return BlockFace.SOUTH;
            default:
                return null;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("java:S1874")
    public void setFaceHit(@Nullable BlockFace face) {
        if (face == null) {
            sideHit = -1;
            return;
        }
        
        switch (face) {
            case DOWN:
                sideHit = 0;
                break;
            case UP:
                sideHit = 1;
                break;
            case NORTH:
                sideHit = 4;
                break;
            case SOUTH:
                sideHit = 5;
                break;
            case WEST:
                sideHit = 3;
                break;
            case EAST:
                sideHit = 2;
                break;
            default:
                sideHit = -1;
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static MovingObjectPosition fromBlock(int x, int y, int z, BlockFace face, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.hitVector = new Vector3(hitVector.x, hitVector.y, hitVector.z);
        objectPosition.setFaceHit(face);
        return objectPosition;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed: sideHit not being filled")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic number in side param", replaceWith = "fromBlock(int,int,int,BlockFace,Vector3)")
    public static MovingObjectPosition fromBlock(int x, int y, int z, int side, Vector3 hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.sideHit = side;
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

    @Override
    public String toString() {
        return "MovingObjectPosition{" +
                "typeOfHit=" + typeOfHit +
                ", blockX=" + blockX +
                ", blockY=" + blockY +
                ", blockZ=" + blockZ +
                ", sideHit=" + sideHit +
                ", hitVector=" + hitVector +
                ", entityHit=" + entityHit +
                '}';
    }
}
