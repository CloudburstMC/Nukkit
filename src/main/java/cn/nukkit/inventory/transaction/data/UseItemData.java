package cn.nukkit.inventory.transaction.data;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

/**
 * @author CreeperFace
 */
public class UseItemData implements TransactionData {

    public int actionType;
    public BlockVector3 blockPos;
    public BlockFace face;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3 playerPos;
    public Vector3f clickPos;

}
