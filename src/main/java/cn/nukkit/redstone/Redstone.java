package cn.nukkit.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRedstoneWire;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.math.Vector3;

import java.util.*;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Redstone {

    public static final int POWER_NONE = 0;
    public static final int POWER_WEAKEST = 1;
    public static final int POWER_STRONGEST = 16;

    //NOTICE: Here POWER_STRONGEST is 16, not 15.
    //I set it to 16 in order to calculate the energy in blocks, such as the redstone torch under the cobblestone.
    //At that time, the cobblestone's energy is 16, not 15. If you put a redstone wire next to it, the redstone wire will got 15 energy.
    //So, POWER_WEAKEST also means that energy in blocks, not redstone wire it self. So set it to 1.

    private static final Comparator<UpdateObject> orderIsdn = new Comparator<UpdateObject>() {
        @Override
        public int compare(UpdateObject o1, UpdateObject o2) {
            if (o1.getPopulation() > o2.getPopulation()) {
                return -1;
            } else if (o1.getPopulation() < o2.getPopulation()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    public static void active(Block source) {
        Queue<UpdateObject> updateQueue = new PriorityQueue<>(1, orderIsdn);
        int currentLevel = source.getPowerLevel() - 1;
        if (currentLevel <= 0) {
            return;
        }
        addToQueue(updateQueue, source);
        while (!updateQueue.isEmpty()) {
            UpdateObject updatingObj = updateQueue.poll();
            Block updating = updatingObj.getLocation();
            currentLevel = updatingObj.getPopulation();
            if (currentLevel > updating.getPowerLevel()) {
                updating.setPowerLevel(currentLevel);
                updating.getLevel().setBlock(updating, updating, true, true);
                addToQueue(updateQueue, updating);
            }
        }
    }

    public static void active(Block source, Map<String, Block> allBlocks) {
        Queue<UpdateObject> updateQueue = new PriorityQueue<>(1, orderIsdn);
        int currentLevel = source.getPowerLevel() - 1;
        if (currentLevel <= 0) {
            return;
        }
        addToQueue(updateQueue, source);
        while (!updateQueue.isEmpty()) {
            UpdateObject updatingObj = updateQueue.poll();
            Block updating = updatingObj.getLocation();
            currentLevel = updatingObj.getPopulation();
            if (currentLevel > updating.getPowerLevel()) {
                updating.setPowerLevel(currentLevel);
                updating.getLevel().setBlock(updating, updating, true, true);
                if (allBlocks.containsKey(updating.getLocationHash())) {
                    allBlocks.remove(updating.getLocationHash());
                }
                addToQueue(updateQueue, updating);
            }
        }
    }

    public static void deactive(Block source, int updateLevel) {
        //Step 1: find blocks which need to update
        Queue<UpdateObject> updateQueue = new PriorityQueue<>(1, orderIsdn);
        Queue<UpdateObject> sourceList = new PriorityQueue<>(1, orderIsdn);
        Map<String, Block> updateMap = new HashMap<>();
        Map<String, Block> closedMap = new HashMap<>();
        int currentLevel = updateLevel;
        if (currentLevel <= 0) {
            return;
        }
        addToDeactiveQueue(updateQueue, source, closedMap, sourceList, currentLevel);
        while (!updateQueue.isEmpty()) {
            UpdateObject updateObject = updateQueue.poll();
            Block updating = updateObject.getLocation();
            currentLevel = updateObject.getPopulation();
            if (currentLevel >= updating.getPowerLevel()) {
                updating.setPowerLevel(0);
                updateMap.put(updating.getLocationHash(), updating);
                addToDeactiveQueue(updateQueue, updating, closedMap, sourceList, currentLevel);
            } else {
                sourceList.add(new UpdateObject(updating.getPowerLevel(), updating));
            }
        }
        //Step 2: recalculate redstone power
        while (!sourceList.isEmpty()) {
            active(sourceList.poll().getLocation(), updateMap);
        }

        for (Block block : updateMap.values()) {
            block.setPowerLevel(0);
            block.getLevel().setBlock(block, block, true, true);
        }
    }

    private static void addToQueue(Queue<UpdateObject> updateQueue, Block location) {
        if (location.getPowerLevel() <= 0) {
            return;
        }
        for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_SOUTH, Vector3.SIDE_EAST, Vector3.SIDE_WEST, Vector3.SIDE_UP, Vector3.SIDE_DOWN}) {
            if (location.getSide(side) instanceof BlockRedstoneWire) {
                updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, location.getSide(side)));
            }
        }
        if (location instanceof BlockRedstoneWire) {
            Block block = location.getSide(Vector3.SIDE_UP);
            if (!(block instanceof BlockSolid)) {
                for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_SOUTH, Vector3.SIDE_EAST, Vector3.SIDE_WEST}) {
                    if (block.getSide(side) instanceof BlockRedstoneWire) {
                        updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, block.getSide(side)));
                    }
                }
            }
            for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_WEST, Vector3.SIDE_EAST, Vector3.SIDE_SOUTH}) {
                block = location.getSide(side);
                if (!(block instanceof BlockSolid)) {
                    Block blockDown;
                    blockDown = block.getSide(Vector3.SIDE_DOWN);
                    if (blockDown instanceof BlockRedstoneWire) {
                        updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, blockDown));
                    }
                }
            }
        }
    }

    private static void addToDeactiveQueue(Queue<UpdateObject> updateQueue, Block location, Map<String, Block> closedMap, Queue<UpdateObject> sourceList, int updateLevel) {
        if (updateLevel < 0) {
            return;
        }
        for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_SOUTH, Vector3.SIDE_EAST, Vector3.SIDE_WEST, Vector3.SIDE_UP, Vector3.SIDE_DOWN}) {
            if (location.getSide(side).isPowerSource() || (updateLevel == 0 && location.getSide(side).isPowered())) {
                sourceList.add(new UpdateObject(location.getPowerLevel(side), location.getSide(side)));
            } else if (location.getSide(side) instanceof BlockRedstoneWire) {
                if (!closedMap.containsKey(location.getSide(side).getLocationHash())) {
                    closedMap.put(location.getSide(side).getLocationHash(), location.getSide(side));
                    updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(side)));
                }
            }
        }
        if (location instanceof BlockRedstoneWire) {
            Block block = location.getSide(Vector3.SIDE_UP);
            for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_SOUTH, Vector3.SIDE_EAST, Vector3.SIDE_WEST}) {
                if (block.getSide(side) instanceof BlockRedstoneWire) {
                    if (!closedMap.containsKey(block.getSide(side).getLocationHash())) {
                        closedMap.put(block.getSide(side).getLocationHash(), block.getSide(side));
                        updateQueue.add(new UpdateObject(updateLevel - 1, block.getSide(side)));
                    }
                }
            }
            Block blockDown;
            for (int side : new int[]{Vector3.SIDE_NORTH, Vector3.SIDE_SOUTH, Vector3.SIDE_EAST, Vector3.SIDE_WEST}) {
                block = location.getSide(side);
                blockDown = block.getSide(Vector3.SIDE_DOWN);
                if (blockDown instanceof BlockRedstoneWire) {
                    if (!closedMap.containsKey(blockDown.getLocationHash())) {
                        closedMap.put(blockDown.getLocationHash(), blockDown);
                        updateQueue.add(new UpdateObject(updateLevel - 1, blockDown));
                    }
                }
            }
        }
    }

}