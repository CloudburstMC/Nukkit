package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

public class DarkOakDoor extends Door{

  public DarkOakDoor(){
		this(0);
	}

	public DarkOakDoor(int meta){
		super(meta);
	}
	
	@Override
	public String getName(){
		return "Dark Oak Door Block";
	}
	
	@Override
	public int getId(){
	    return DARK_OAK_DOOR_BLOCK;
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
                {Item.ACACIA_DOOR, 0, 1}
        };
    }
}
