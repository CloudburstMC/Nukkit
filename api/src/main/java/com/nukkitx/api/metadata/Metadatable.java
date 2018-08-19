package com.nukkitx.api.metadata;

import java.util.Optional;

/**
 * @author CreeperFace
 */
public interface Metadatable {

    Optional<Metadata> getMetadata();

    default <T extends Metadata> T ensureMetadata(Class<T> clazz) {
        try {
            return (T) getMetadata().orElseThrow(() -> new IllegalArgumentException("Metadatable instance has no data"));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid class supplied", e);
        }
    }
}
