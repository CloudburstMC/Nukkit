package cn.nukkit.resourcepacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class AbstractResourcePack implements ResourcePack {
    protected JsonObject manifest;

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
    public String getPackId() {
        return this.manifest.getAsJsonObject("header")
                .get("uuid").getAsString();
    }

    @Override
    public String getPackVersion() {
        JsonArray version = this.manifest.getAsJsonObject("header")
                .get("version").getAsJsonArray();

        return String.join(".", version.get(0).getAsString(),
                version.get(1).getAsString(),
                version.get(2).getAsString());
    }
}
