package cn.nukkit.utils;

import java.util.Map;

public interface IGenerateWorld {
    Map<String, Object> SetWorldNames();
    long SetWorldSeed(String name);
    Identifier SetWorldGenerator(String name);
}