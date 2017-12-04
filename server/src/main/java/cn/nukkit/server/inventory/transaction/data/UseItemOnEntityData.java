package cn.nukkit.server.inventory.transaction.data;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.math.Vector3;

/**
 * @author CreeperFace
 */
public class UseItemOnEntityData implements TransactionData {

    public long entityRuntimeId;
    public int actionType;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3 vector1;
    public Vector3 vector2;

}
