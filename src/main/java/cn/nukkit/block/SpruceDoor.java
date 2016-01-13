package cn.nukkit.block;

import cn.nukkit.item.Item;

public class SpruceDoor extends WoodDoor {

    public SpruceDoor(){
		this(0);
	}

	public SpruceDoor(int meta){
		super(meta);
	}
	
	@Override
	public String getName(){
		return "Spruce Door Block";
	}
	
	@Override
	public int getId(){
	    return SPRUCE_DOOR_BLOCK;
	}

	@Override
	public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.SPRUCE_DOOR, 0, 1}
        };
    }
}
