package cn.nukkit.api.level.gamerule;

public interface GameRule<T> {

    String getName();

    T getValue();
}
