package cn.nukkit.utils;

import lombok.ToString;

import java.util.UUID;

/**
 * Persona skin piece
 */
@ToString
public class PersonaPiece {

    public final String id;
    public final PersonaPieceType type;
    public final UUID packId;
    public final boolean isDefault;
    public final String productId;

    public PersonaPiece(String id, PersonaPieceType type, UUID packId, boolean isDefault, String productId) {
        this.id = id;
        this.type = type;
        this.packId = packId;
        this.isDefault = isDefault;
        this.productId = productId;
    }
}
