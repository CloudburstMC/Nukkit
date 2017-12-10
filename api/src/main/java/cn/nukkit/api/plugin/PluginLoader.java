package cn.nukkit.api.plugin;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface PluginLoader {

    @Nonnull
    PluginDescription loadPlugin(Path file) throws Exception;

    @Nonnull
    PluginContainer createPlugin(PluginDescription description) throws Exception;

    void enablePlugin(Plugin plugin);

    void disablePlugin(Plugin plugin);

    @Nonnull
    String getPluginFileExtension();
}
