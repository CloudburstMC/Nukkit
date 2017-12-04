package cn.nukkit.server.inventory.transaction.data;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.BlockVector3;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.math.Vector3f;

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
