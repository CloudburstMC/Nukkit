package cn.nukkit.resourcepacks;

import cn.nukkit.Server;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.UUID;

public abstract class AbstractResourcePack implements ResourcePack {
    protected JsonObject manifest;
    private UUID id = null;
    private ResourcePack.Type type = null;

    protected boolean verifyManifest() {
        if (this.manifest.has("format_version") && this.manifest.has("header") && this.manifest.has("modules")) {
            JsonObject header = this.manifest.getAsJsonObject("header");
            return header.has("description") &&
                    header.has("name") &&
                    header.has("uuid") &&
                    header.has("version") &&
                    header.getAsJsonArray("version").size() == 3;
        } else {
            return false;
        }
    }

    @Override
    public String getPackName() {
        return this.manifest.getAsJsonObject("header")
                .get("name").getAsString();
    }

    @Override
    public UUID getPackId() {
        if (id == null) {
            id = UUID.fromString(this.manifest.getAsJsonObject("header").get("uuid").getAsString());
        }
        return id;
    }

    @Override
    public String getPackVersion() {
        JsonArray version = this.manifest.getAsJsonObject("header")
                .get("version").getAsJsonArray();

        return String.join(".", version.get(0).getAsString(),
                version.get(1).getAsString(),
                version.get(2).getAsString());
    }

    @Override
    public Type getType() {
        if (type == null) {
            JsonArray modules = this.manifest.getAsJsonArray("modules").getAsJsonArray();

            for (int i = 0; i < modules.size(); i++) {
                JsonObject element = modules.get(i).getAsJsonObject();
                String moduleType = element.get("type").getAsString();

                ResourcePack.Type type = null;
                for (ResourcePack.Type possibleType : ResourcePack.Type.values()) {
                    if (Arrays.asList(possibleType.getAllowedModuleTypes()).contains(moduleType)) {
                        type = possibleType;
                        break;
                    }
                }

                if (type == null) {
                    throw new IllegalArgumentException(Server.getInstance().getLanguage()
                            .translateString("nukkit.resources.unsupported-module-type", this.getPackName()));
                }

                this.type = type;
                break;
            }
        }

        return type;
    }
}
