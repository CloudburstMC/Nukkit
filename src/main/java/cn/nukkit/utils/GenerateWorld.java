package cn.nukkit.utils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;

/**
 *      created by jangel3 on 2020/3/2. 
 *      Package cn.nukkit.utils in project Nukkit
 */
@Log4j2
public class GenerateWorld implements IGenerateWorld {
    private Server server;

    public GenerateWorld(Server server) {
        this.server = server;
    }

    public Map<String, Object> SetWorldNames() {
        Map<String, Object> worldNames = server.getConfig("worlds", Collections.emptyMap());
        if (worldNames.size() == 0)
            worldNames = new Hashtable<String, Object>();
        // throw new IllegalStateException("No worlds configured! Add a world to
        // nukkit.yml and try again!");

        String defaultWorld = server.getProperty("level-name", "world");
        if (defaultWorld == null || defaultWorld.trim().isEmpty()) {
            log.warn("level-name cannot be null, using default");
            defaultWorld = "world";
        }
        worldNames.put(defaultWorld, Collections.emptyMap());
        Boolean useNether = server.getProperty("allow-nether").equalsIgnoreCase("on");
        if (useNether && !worldNames.containsKey("nether")) {
            worldNames.put("nether", Collections.emptyMap());
        }
        return worldNames;
    }

    public long SetWorldSeed(String name) {
        // fallback to level name if no seed is set
        Object seedObj = server.getConfig("worlds." + name + ".seed", name);
        long seed;
        if (seedObj instanceof Number) {
            seed = ((Number) seedObj).longValue();
        } else if (seedObj instanceof String) {
            if (seedObj == name) {
                log.warn("World \"{}\" does not have a seed! Using a the name as the seed", name);
            }

            // this internally generates an MD5 hash of the seed string
            UUID uuid = UUID.nameUUIDFromBytes(((String) seedObj).getBytes(StandardCharsets.UTF_8));
            seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        } else {
            throw new IllegalStateException("Seed for world \"" + name + "\" is invalid: "
                    + (seedObj == null ? "null" : seedObj.getClass().getCanonicalName()));
        }
        return seed;
    }

    public Identifier SetWorldGenerator(String name){
        String worldGenerator = server.getConfig("worlds." + name + ".generator");
        Identifier generator;
        if (worldGenerator != null)
            generator = Identifier.fromString(worldGenerator);
        else{
            generator = Identifier.fromString(name);
        }
        return generator;
    }

}