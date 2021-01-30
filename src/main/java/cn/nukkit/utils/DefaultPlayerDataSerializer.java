package cn.nukkit.utils;

import cn.nukkit.Server;
import com.google.common.base.Preconditions;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

public class DefaultPlayerDataSerializer implements PlayerDataSerializer {
    private String dataPath;
    
    public DefaultPlayerDataSerializer(Server server) {
        this(server.getDataPath());
    }
    
    public DefaultPlayerDataSerializer(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public Optional<InputStream> read(String name, UUID uuid) throws IOException {
        String path = dataPath + "players/" + name + ".dat";
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }
        return Optional.of(new FileInputStream(file));

    }

    @Override
    public OutputStream write(String name, UUID uuid) throws IOException {
        Preconditions.checkNotNull(name, "name");
        String path = dataPath + "players/" + name + ".dat";
        File file = new File(path);
        return new FileOutputStream(file);
    }
}
