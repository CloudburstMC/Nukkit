package cn.nukkit.command.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author lukeeey
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum BuiltinCommandEnums {
    ITEM("Item"),
    BLOCK("Block"),
    COMMAND("commandName"),
    ENCHANTMENT("enchantmentType"),
    ENTITY("entityType"),
    EFFECT("effectType"),
    PARTICLE("particleType");

    private final String networkName;
}
