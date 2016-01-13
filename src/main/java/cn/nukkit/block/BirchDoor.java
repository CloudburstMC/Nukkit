package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BirchDoor extends WoodDoor{

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
	public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.BIRCH_DOOR, 0, 1}
        };
    }
}
