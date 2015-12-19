package cn.nukkit.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.block.RedstoneWire;
import cn.nukkit.math.Vector3;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

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
                updating.getLevel().setBlock(updating, updating, true, false);
                addToQueue(updateQueue, updating);
            }
        }
    }

    public static void updateDeactive(Block source, int updateLevel) {
        //todo: im not finished here now... let's me think
        Queue updateQueue = new PriorityQueue<UpdateObject>(1, orderIsdn);
        Queue sourceQueue = new PriorityQueue<UpdateObject>(1, orderIsdn);
        int currentUpdateLevel = updateLevel - 1;
        if(currentUpdateLevel <= 0) {
            return;
        }
        addToQueue(updateQueue, source);
        while(!updateQueue.isEmpty()) {
            UpdateObject updatingObj = (UpdateObject) (updateQueue.poll());
            Block updating = updatingObj.getLocation();
            currentUpdateLevel = updatingObj.getPopulation();
            if(currentUpdateLevel >= updating.getRedEnergyLevel()) {

            }
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

}
