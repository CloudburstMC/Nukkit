package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.math.Vector3;

/**
 * Created by Snake1999 on 2016/1/22.
 * Package cn.nukkit.utils in project nukkit.
 */
public final class Doors {

    public static boolean setOpen(Block door, Player player, boolean trueForOpenFalseForClose) {
        if (door == null || player == null) return false;
        boolean opened = isOpen(door.getId(), door.getDamage());
        if (trueForOpenFalseForClose && opened) return false;
        if (!trueForOpenFalseForClose && !opened) return false;
        return toggleOpenState(door, player);
    }

    public static boolean toggleOpenState(Block door, Player player) {
        DoorToggleEvent event = new DoorToggleEvent(door, player);
        if (door == null) return false;
        door.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        door = event.getBlock();
        player = event.getPlayer();
        if (door == null || player == null) return false;
        if (isNormalDoorBlock(door.getId())) return toggleOpenStateNormalDoor(door);
        else if (isFenceGateBlock(door.getId())) return toggleOpenStateFenceGate(door, player.yaw);
        else if (isTrapdoorBlock(door.getId())) return toggleOpenStateTrapdoor(door);
        return false;
    }

    public static boolean isOpen(int blockID, int blockMeta) {
        if(!isDoorBlock(blockID)) return false;
        if (isNormalDoorBlock(blockID)) return (blockMeta & 0x04) > 0;
        if (isFenceGateBlock(blockID)) return (blockMeta & 0x04) > 0;
        if (isTrapdoorBlock(blockID)) return (blockMeta & 0x08) > 0;//why 0x08 here, ask mojang, don't ask me
        return false;
    }

    public static boolean isDoorBlock(int blockID) {
        return isNormalDoorBlock(blockID) || isTrapdoorBlock(blockID) || isFenceGateBlock(blockID);
    }

    public static boolean isNormalDoorBlock(int blockID) {
        switch (blockID) {
            case Block.DOOR_BLOCK:
            case Block.ACACIA_DOOR_BLOCK:
            case Block.BIRCH_DOOR_BLOCK:
            case Block.DARK_OAK_DOOR_BLOCK:
            case Block.JUNGLE_DOOR_BLOCK:
            case Block.SPRUCE_DOOR_BLOCK:
            case Block.IRON_DOOR_BLOCK:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFenceGateBlock(int blockID) {
        switch (blockID) {
            case Block.FENCE_GATE:
            case Block.FENCE_GATE_ACACIA:
            case Block.FENCE_GATE_BIRCH:
            case Block.FENCE_GATE_DARK_OAK:
            case Block.FENCE_GATE_JUNGLE:
            case Block.FENCE_GATE_SPRUCE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTrapdoorBlock(int blockID) {
        switch (blockID) {
            case Block.TRAPDOOR:
            case Block.IRON_TRAPDOOR:
                return true;
            default:
                return false;
        }
    }

    /************************************
     *  private part, for internal use  *
     ************************************/

    private static boolean toggleOpenStateNormalDoor(Block door) {
        if (door == null) return false;
        if (!isNormalDoorBlock(door.getId())) return false;
        if ((door.getDamage() & 0x08) == 0x08) { //Top
            Block down = door.getSide(Vector3.SIDE_DOWN);
            if (down.getId() != door.getId()) return false;
            door.getLevel().setBlock(down, Block.get(door.getId(), down.getDamage() ^ 0x04), true);
        } else { //Down
            door.setDamage(door.getDamage() ^ 0x04);
            door.getLevel().setBlock(door, door, true);
        }
        return true;
    }

    private static boolean toggleOpenStateFenceGate(Block fenceGate, double playerYaw) {
        if (fenceGate == null) return false;
        if (!isFenceGateBlock(fenceGate.getId())) return false;

        double rotation = (playerYaw - 90) % 360;
        if (rotation < 0) rotation += 360.0;

        int originDirection = fenceGate.getDamage() & 0x01;
        int direction;
        if (originDirection == 0) {
            if (rotation >= 0 && rotation < 180) direction = 2;
            else direction = 0;
        } else {
            if (rotation >= 90 && rotation < 270) direction = 3;
            else direction = 1;
        }

        fenceGate.setDamage(direction | ((~fenceGate.getDamage()) & 0x04));
        return true;
    }

    private static boolean toggleOpenStateTrapdoor(Block trapdoor) {
        if (trapdoor == null) return false;
        if (!isTrapdoorBlock(trapdoor.getId())) return false;
        int sideBit = trapdoor.getDamage() & 0b0111;
        int openBit = trapdoor.getDamage() & 0b1000;
        openBit = (~openBit) & 0b1000;
        trapdoor.setDamage(sideBit | openBit);
        return true;
    }
}
