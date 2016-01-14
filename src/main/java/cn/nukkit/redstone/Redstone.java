package cn.nukkit.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.block.RedstoneWire;
import cn.nukkit.block.Solid;
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

    private static Comparator<UpdateObject> orderIsdn = new Comparator<UpdateObject>() {
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
        int currentLevel = updateLevel;
        if (currentLevel <= 0) {
            return;
        }
        addToDeactiveQueue(updateQueue, source, updateMap, sourceList, currentLevel);
        while (!updateQueue.isEmpty()) {
            UpdateObject updateObject = updateQueue.poll();
            Block updating = updateObject.getLocation();
            currentLevel = updateObject.getPopulation();
            if (currentLevel >= updating.getPowerLevel()) {
                updating.setPowerLevel(0);
                updateMap.put(updating.getLocationHash(), updating);
                addToDeactiveQueue(updateQueue, updating, updateMap, sourceList, currentLevel);
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
        if (location.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, location.getSide(Vector3.SIDE_NORTH)));
        }
        if (location.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, location.getSide(Vector3.SIDE_SOUTH)));
        }
        if (location.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, location.getSide(Vector3.SIDE_EAST)));
        }
        if (location.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, location.getSide(Vector3.SIDE_WEST)));
        }
        if (location instanceof RedstoneWire) {
            Block block = location.getSide(Vector3.SIDE_UP);
            if (!(block instanceof Solid)) {
                if (block.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, block.getSide(Vector3.SIDE_NORTH)));
                }
                if (block.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, block.getSide(Vector3.SIDE_SOUTH)));
                }
                if (block.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, block.getSide(Vector3.SIDE_EAST)));
                }
                if (block.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, block.getSide(Vector3.SIDE_WEST)));
                }
            }
            block = location.getSide(Vector3.SIDE_NORTH);
            if (!(block instanceof Solid)) {
                Block blockDown;
                blockDown = block.getSide(Vector3.SIDE_DOWN);
                if (blockDown instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_SOUTH);
            if (!(block instanceof Solid)) {
                Block blockDown;
                blockDown = block.getSide(Vector3.SIDE_DOWN);
                if (blockDown instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_EAST);
            if (!(block instanceof Solid)) {
                Block blockDown;
                blockDown = block.getSide(Vector3.SIDE_DOWN);
                if (blockDown instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_WEST);
            if (!(block instanceof Solid)) {
                Block blockDown;
                blockDown = block.getSide(Vector3.SIDE_DOWN);
                if (blockDown instanceof RedstoneWire) {
                    updateQueue.add(new UpdateObject(location.getPowerLevel() - 1, blockDown));
                }
            }
        }
    }

    private static void addToDeactiveQueue(Queue<UpdateObject> updateQueue, Block location, Map<String, Block> updateMap, Queue<UpdateObject> sourceList, int updateLevel) {
        if (updateLevel < 0) {
            return;
        }
        if (location.getSide(Vector3.SIDE_NORTH).isPowerSource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_NORTH).isPowered())) {
            sourceList.add(new UpdateObject(location.getPowerLevel(Vector3.SIDE_NORTH), location.getSide(Vector3.SIDE_NORTH)));
        } else if (location.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
            if (!updateMap.containsKey(location.getSide(Vector3.SIDE_NORTH).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_NORTH)));
            }
        }
        if (location.getSide(Vector3.SIDE_SOUTH).isPowerSource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_SOUTH).isPowered())) {
            sourceList.add(new UpdateObject(location.getPowerLevel(Vector3.SIDE_SOUTH), location.getSide(Vector3.SIDE_SOUTH)));
        } else if (location.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
            if (!updateMap.containsKey(location.getSide(Vector3.SIDE_SOUTH).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_SOUTH)));
            }
        }
        if (location.getSide(Vector3.SIDE_EAST).isPowerSource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_EAST).isPowered())) {
            sourceList.add(new UpdateObject(location.getPowerLevel(Vector3.SIDE_EAST), location.getSide(Vector3.SIDE_EAST)));
        } else if (location.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
            if (!updateMap.containsKey(location.getSide(Vector3.SIDE_EAST).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_EAST)));
            }
        }
        if (location.getSide(Vector3.SIDE_WEST).isPowerSource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_WEST).isPowered())) {
            sourceList.add(new UpdateObject(location.getPowerLevel(Vector3.SIDE_WEST), location.getSide(Vector3.SIDE_WEST)));
        } else if (location.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
            if (!updateMap.containsKey(location.getSide(Vector3.SIDE_WEST).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_WEST)));
            }
        }
        if (location instanceof RedstoneWire) {
            Block block = location.getSide(Vector3.SIDE_UP);
            if (block.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
                if (!updateMap.containsKey(block.getSide(Vector3.SIDE_NORTH).getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, block.getSide(Vector3.SIDE_NORTH)));
                }
            }
            if (block.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
                if (!updateMap.containsKey(block.getSide(Vector3.SIDE_SOUTH).getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, block.getSide(Vector3.SIDE_SOUTH)));
                }
            }
            if (block.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
                if (!updateMap.containsKey(block.getSide(Vector3.SIDE_EAST).getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, block.getSide(Vector3.SIDE_EAST)));
                }
            }
            if (block.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
                if (!updateMap.containsKey(block.getSide(Vector3.SIDE_WEST).getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, block.getSide(Vector3.SIDE_WEST)));
                }
            }
            block = location.getSide(Vector3.SIDE_NORTH);
            Block blockDown;
            blockDown = block.getSide(Vector3.SIDE_DOWN);
            if (blockDown instanceof RedstoneWire) {
                if (!updateMap.containsKey(blockDown.getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_SOUTH);
            blockDown = block.getSide(Vector3.SIDE_DOWN);
            if (blockDown instanceof RedstoneWire) {
                if (!updateMap.containsKey(blockDown.getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_EAST);
            blockDown = block.getSide(Vector3.SIDE_DOWN);
            if (blockDown instanceof RedstoneWire) {
                if (!updateMap.containsKey(blockDown.getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, blockDown));
                }
            }
            block = location.getSide(Vector3.SIDE_WEST);
            blockDown = block.getSide(Vector3.SIDE_DOWN);
            if (blockDown instanceof RedstoneWire) {
                if (!updateMap.containsKey(blockDown.getLocationHash())) {
                    updateQueue.add(new UpdateObject(updateLevel - 1, blockDown));
                }
            }
        }
    }

}
