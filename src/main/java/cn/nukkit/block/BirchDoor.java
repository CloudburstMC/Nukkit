package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

public class BirchDoor extends Door{

  public BirchDoor(){
		this(0);
	}

	public BirchDoor(int meta){
		super(meta);
	}
	
	@Override
	public String getName(){
		return "Birch Door Block";
	}
	
	@Override
	public int getId(){
	    return BIRCH_DOOR_BLOCK;
	}
	
	@Override
	public boolean canBeActivated(){
		return true;
	}
	
	@Override
	public double getHardness(){
		return 3;
	}
	
	@Override
	public int getToolType(){
		return Tool.TYPE_AXE;
	}
	
	@Override
	public double getResistance() {
        return 15;
    }
	
	@Override
	public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.BIRCH_DOOR, 0, 1}
        };
    }
}
