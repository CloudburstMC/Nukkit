package cn.nukkit.server.inventory.transaction.data;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.math.Vector3;

/**
 * @author CreeperFace
 */
public class ReleaseItemData implements TransactionData {

    public int actionType;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3 headRot;
}
