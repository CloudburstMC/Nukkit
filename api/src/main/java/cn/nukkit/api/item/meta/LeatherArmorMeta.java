package cn.nukkit.api.item.meta;

import java.awt.*;

/**
 * @author CreeperFace
 */
public interface LeatherArmorMeta extends ItemMeta {

    void setColor(Color color);

    Color getColor();
}
