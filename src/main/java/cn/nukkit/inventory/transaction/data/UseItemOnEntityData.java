package cn.nukkit.inventory.transaction.data;

import cn.nukkit.item.Item;
import com.nukkitx.math.vector.Vector3f;

/**
 * @author CreeperFace
 */
public class UseItemOnEntityData implements TransactionData {

    public long entityRuntimeId;
    public int actionType;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3f playerPos;
    public Vector3f clickPos;

}
