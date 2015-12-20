package cn.nukkit.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.block.RedstoneWire;
import cn.nukkit.math.Vector3;

import java.util.*;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Redstone {

    public static final int ENERGY_NONE = 0;
    public static final int ENERGY_WEAK = 1;
    public static final int ENERGY_STRONG = 16;

    //NOTICE: Here ENERGY_STRONG is 16, not 15.
    //I set it to 16 in order to calculate the energy in blocks, such as the redstone torch under the cobblestone.
    //At that time, the cobblestone's energy is 16, not 15. If you put a redstone wire next to it, the redstone wire will got 15 energy.
    //So, ENERGY_WEAK also means that energy in blocks, not redstone wire it self. So set it to 1.

    private static Comparator<UpdateObject> orderIsdn = new Comparator<UpdateObject>() {
        @Override
        public int compare(UpdateObject o1, UpdateObject o2) {
            if(o1.getPopulation() > o2.getPopulation()) {
                return -1;
            }
            else if(o1.getPopulation() < o2.getPopulation()) {
                return 1;
            }
            else {
                return 0;
            }
        }
    };

    public static void updateActive(Block source) {
        Queue updateQueue = new PriorityQueue<UpdateObject>(1, orderIsdn);
        int currentupdatelevel = source.getRedEnergyLevel() - 1;
        if(currentupdatelevel <= 0) {
            return;
        }
        addToQueue(updateQueue, source);
        while(!updateQueue.isEmpty()) {
            UpdateObject updatingObj = (UpdateObject) (updateQueue.poll());
            Block updating = updatingObj.getLocation();
            currentupdatelevel = updatingObj.getPopulation();
            if(currentupdatelevel > updating.getRedEnergyLevel()) {
                updating.setRedEnergyLevel(currentupdatelevel);
                updating.getLevel().setBlock(updating, updating, true, true);
                addToQueue(updateQueue, updating);
            }
        }
    }

    public static void updateActive(Block source, Map<String, Block> allBlocks) {
        Queue updateQueue = new PriorityQueue<UpdateObject>(1, orderIsdn);
        int currentupdatelevel = source.getRedEnergyLevel() - 1;
        if(currentupdatelevel <= 0) {
            return;
        }
        addToQueue(updateQueue, source);
        while(!updateQueue.isEmpty()) {
            UpdateObject updatingObj = (UpdateObject) (updateQueue.poll());
            Block updating = updatingObj.getLocation();
            currentupdatelevel = updatingObj.getPopulation();
            if(currentupdatelevel > updating.getRedEnergyLevel()) {
                updating.setRedEnergyLevel(currentupdatelevel);
                updating.getLevel().setBlock(updating, updating, true, true);
                if(allBlocks.containsKey(updating.getLocationHash())) {
                    allBlocks.remove(updating.getLocationHash());
                }
                addToQueue(updateQueue, updating);
            }
        }
    }

    public static void updateDeactive(Block source, int updateLevel) {
        //Step 1: find blocks which need to update
        Queue updateQueue = new PriorityQueue<UpdateObject>(1, orderIsdn);
        Queue sourceList = new PriorityQueue<UpdateObject>(1, orderIsdn);
        Map<String, Block> updateMap = new HashMap<String, Block>();
        int currentupdatelevel = updateLevel;
        if(currentupdatelevel <= 0) {
            return;
        }
        addToDeactiveQueue(updateQueue, source, updateMap, sourceList, currentupdatelevel);
        while(!updateQueue.isEmpty()) {
            UpdateObject updateObject = (UpdateObject) (updateQueue.poll());
            Block updating = updateObject.getLocation();
            currentupdatelevel = updateObject.getPopulation();
            if(currentupdatelevel >= updating.getRedEnergyLevel()) {
                updating.setRedEnergyLevel(0);
                updateMap.put(updating.getLocationHash(), updating);
                addToDeactiveQueue(updateQueue, updating, updateMap, sourceList, currentupdatelevel);
            }
            else {
                sourceList.add(new UpdateObject(updating.getRedEnergyLevel(), updating));
            }
        }
        //Step 2: recalculate redstone power
        while(!sourceList.isEmpty()) {
            updateActive(((UpdateObject) (sourceList.poll())).getLocation(), updateMap);
        }
        Iterator iter = updateMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Block block = (Block) entry.getValue();
            block.setRedEnergyLevel(0);
            block.getLevel().setBlock(block, block, true, true);
        }
    }

    private static void addToQueue(Queue updateQueue, Block location) {
        if(location.getRedEnergyLevel() <= 0) {
            return;
        }
        if(location.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getRedEnergyLevel() - 1, location.getSide(Vector3.SIDE_NORTH)));
        }
        if(location.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getRedEnergyLevel() - 1, location.getSide(Vector3.SIDE_SOUTH)));
        }
        if(location.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getRedEnergyLevel() - 1, location.getSide(Vector3.SIDE_EAST)));
        }
        if(location.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
            updateQueue.add(new UpdateObject(location.getRedEnergyLevel() - 1, location.getSide(Vector3.SIDE_WEST)));
        }
    }

    private static void addToDeactiveQueue(Queue updateQueue, Block location, Map<String, Block> updateMap, Queue sourceList, int updateLevel) {
        if(updateLevel < 0) {
            return;
        }
        if(location.getSide(Vector3.SIDE_NORTH).isEnergySource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_NORTH).hasRedEnergy())) {
            sourceList.add(new UpdateObject(location.getRedEnergyLevel(Vector3.SIDE_NORTH), location.getSide(Vector3.SIDE_NORTH)));
        }
        else if(location.getSide(Vector3.SIDE_NORTH) instanceof RedstoneWire) {
            if(!updateMap.containsKey(location.getSide(Vector3.SIDE_NORTH).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_NORTH)));
            }
        }
        if(location.getSide(Vector3.SIDE_SOUTH).isEnergySource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_SOUTH).hasRedEnergy())) {
            sourceList.add(new UpdateObject(location.getRedEnergyLevel(Vector3.SIDE_SOUTH), location.getSide(Vector3.SIDE_SOUTH)));
        }
        else if(location.getSide(Vector3.SIDE_SOUTH) instanceof RedstoneWire) {
            if(!updateMap.containsKey(location.getSide(Vector3.SIDE_SOUTH).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_SOUTH)));
            }
        }
        if(location.getSide(Vector3.SIDE_EAST).isEnergySource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_EAST).hasRedEnergy())) {
            sourceList.add(new UpdateObject(location.getRedEnergyLevel(Vector3.SIDE_EAST), location.getSide(Vector3.SIDE_EAST)));
        }
        else if(location.getSide(Vector3.SIDE_EAST) instanceof RedstoneWire) {
            if(!updateMap.containsKey(location.getSide(Vector3.SIDE_EAST).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_EAST)));
            }
        }
        if(location.getSide(Vector3.SIDE_WEST).isEnergySource() || (updateLevel == 0 && location.getSide(Vector3.SIDE_WEST).hasRedEnergy())) {
            sourceList.add(new UpdateObject(location.getRedEnergyLevel(Vector3.SIDE_WEST), location.getSide(Vector3.SIDE_WEST)));
        }
        else if(location.getSide(Vector3.SIDE_WEST) instanceof RedstoneWire) {
            if(!updateMap.containsKey(location.getSide(Vector3.SIDE_WEST).getLocationHash())) {
                updateQueue.add(new UpdateObject(updateLevel - 1, location.getSide(Vector3.SIDE_WEST)));
            }
        }
    }

}
