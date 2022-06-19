package cn.nukkit.entity;

import cn.nukkit.Player;

/**
 * @author Adam Matthew
 */
public interface EntityInteractable {

    String getInteractButtonText();

    //TODO: Mob entity stuff (e.g. feed)
    default String getInteractButtonText(Player player) {
        return this.getInteractButtonText();
    }

    boolean canDoInteraction();

}
