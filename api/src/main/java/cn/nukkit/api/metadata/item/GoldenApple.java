package cn.nukkit.api.metadata.item;

import cn.nukkit.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GoldenApple implements Metadata {
    private final boolean enchanted;

    public static GoldenApple of(boolean isEnchanted) {
        return new GoldenApple(isEnchanted);
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(enchanted);
    }
}
