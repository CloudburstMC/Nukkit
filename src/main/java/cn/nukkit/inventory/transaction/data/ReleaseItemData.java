package cn.nukkit.inventory.transaction.data;

import cn.nukkit.item.Item;
import com.nukkitx.math.vector.Vector3f;
import lombok.ToString;

/**
 * @author CreeperFace
 */
@ToString
public class ReleaseItemData implements TransactionData {

    public int actionType;
    public int hotbarSlot;
    public Item itemInHand;
    public Vector3f headRot;
}
