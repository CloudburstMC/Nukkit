package cn.nukkit.test;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsJsonObjectsAsConfigSections() throws IOException {
        Path file = tempDir.resolve("groups.json");
        Files.write(file, "{\"groups\":{\"admin\":{\"prefix\":\"[Admin]\"},\"members\":[{\"name\":\"Alex\"}]}}"
                .getBytes(StandardCharsets.UTF_8));

        Config config = new Config(file.toString(), Config.JSON);
        ConfigSection groups = config.getSection("groups");
        ConfigSection admin = groups.getSection("admin");

        assertInstanceOf(ConfigSection.class, config.get("groups"));
        assertInstanceOf(ConfigSection.class, groups.get("admin"));
        assertInstanceOf(ConfigSection.class, groups.getList("members").get(0));
        assertEquals("[Admin]", admin.getString("prefix"));
    }
}
