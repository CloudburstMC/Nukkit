package cn.nukkit.tile;

import cn.nukkit.item.Item;

/**
 * 表达一个容器的接口。<br>
 * An interface describes a container.
 * <p>
 * <p>{@code Container}容器必须包含物品的{@code Item}对象。<br>
 * A {@code Container} must contain items as {@code Item} objects.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.tile.Chest
 * @see cn.nukkit.tile.Furnace
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Container {

    /**
     * 返回一个存储在容器里的物品的{@code Item}对象。<br>
     * Returns an item that stores in this container, as an {@code Item} object.
     *
     * @param index 这个物品的索引序号。<br>The index number of this item.
     * @return 这个物品的 {@code Item}对象。<br>An {@code Item} object for this item.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Item getItem(int index);

    /**
     * 把一个物品存储进容器。<br>
     * Sets or stores this item into this container.
     * <p>
     * <p>注意：如果这个容器相应的索引序号已经有了物品，那么新存储的物品将会替换原有的物品。<br>
     * Notice: If there is already an item for this index number, the new item being stored will REPLACE the old one.</p>
     *
     * @param index 这个物品的索引序号。<br>The index number of this item.
     * @param item  描述这个物品的 {@code Item}对象。<br>The {@code Item} object that describes this item.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setItem(int index, Item item);

    /**
     * 返回这个容器最多能包含的物品数量。<br>
     * Returns the max number of items that this container can contain.
     *
     * @return 最多能包含的物品数量。<br>The max number.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    int getSize();
}
