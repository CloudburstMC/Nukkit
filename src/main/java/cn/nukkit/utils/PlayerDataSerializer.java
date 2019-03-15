package cn.nukkit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

public interface PlayerDataSerializer {

    /**
     * Reads player data from {@link InputStream} if the file exists otherwise it will create the default data.
     *
     * @param name name of player or {@link UUID} as {@link String}
     * @param uuid uuid of player. Could be null if name is used.
     * @return {@link InputStream} if the player data exists
     */
    Optional<InputStream> read(String name, UUID uuid) throws IOException;

    /**
     * Writes player data to given {@link OutputStream}.
     *
     * @param name name of player or {@link UUID} as {@link String}
     * @param uuid uuid of player. Could be null if name is used.
     * @return stream to write player data
     */
    OutputStream write(String name, UUID uuid) throws IOException;
}
