package cn.nukkit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

public interface PlayerDataSerializer {

    /**
     * Read player data from
     *
     * @param name name of player or UUID as String
     * @param uuid uuid of player. Could be null if name is used.
     * @return {@link InputStream} if the player data is present
     */
    Optional<InputStream> read(String name, UUID uuid) throws IOException;

    /**
     * @param name name of player or UUID as String
     * @param uuid uuid of player. Could be null if name is used.
     * @return stream to write player data
     */
    OutputStream write(String name, UUID uuid) throws IOException;
}
