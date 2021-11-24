package cn.nukkit.resourcepacks;

import java.util.UUID;

/**
 * Can either be a "Resource Pack" or a "Behavior Pack"
 */
public interface ResourcePack {
    String getPackName();

    UUID getPackId();

    String getPackVersion();

    int getPackSize();

    byte[] getSha256();

    byte[] getPackChunk(int off, int len);

    Type getType();

    boolean requiresScripting();

    enum Type {
        // Module types may not overlap.
        RESOURCE_PACK(new String[]{"resources"}),
        BEHAVIOR_PACK(new String[]{"client_data"}); // only client scripts are supported

        private final String[] allowedModuleTypes;

        Type(String[] allowedModuleTypes) {
            this.allowedModuleTypes = allowedModuleTypes;
        }

        public String[] getAllowedModuleTypes() {
            return this.allowedModuleTypes;
        }
    }
}