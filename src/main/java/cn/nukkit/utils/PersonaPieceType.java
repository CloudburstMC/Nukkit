package cn.nukkit.utils;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum PersonaPieceType {

    UNKNOWN("unknown", "persona_unknown"),
    SKELETON("skeleton", "persona_skeleton"),
    BODY("body", "persona_body"),
    SKIN("skin", "persona_skin"),
    BOTTOM("bottom", "persona_bottom"),
    FEET("feet", "persona_feet"),
    DRESS("dress", "persona_dress"),
    TOP("top", "persona_top"),
    HIGH_PANTS("high_pants", "persona_high_pants"),
    HANDS("hands", "persona_hand"),
    OUTERWEAR("outerwear", "persona_outerwear"),
    FACIAL_HAIR("facial_hair", "persona_facial_hair"),
    MOUTH("mouth", "persona_mouth"),
    EYES("eyes", "persona_eyes"),
    HAIR("hair", "persona_hair"),
    HOOD("hood", "persona_hood"),
    BACK("back", "persona_back"),
    FACE_ACCESSORY("face_accessory", "persona_face_accessory"),
    HEAD("head", "persona_head"),
    LEGS("legs", "persona_legs"),
    LEFT_LEG("left_leg", "persona_left_leg"),
    RIGHT_LEG("right_leg", "persona_right_leg"),
    ARMS("arms", "persona_arms"),
    LEFT_ARM("left_arm", "persona_left_arm"),
    RIGHT_ARM("right_arm", "persona_right_arm"),
    CAPES("capes", "persona_capes"),
    CLASSIC_SKIN("classic_skin", "persona_classic_skin"),
    EMOTE("emote", "persona_emote"),
    UNSUPPORTED("unsupported", "unsupported");

    @Getter
    private final String serializeName;
    private final String type;

    private static final Map<String, PersonaPieceType> serializeNames = new HashMap<>(values().length * 2, 1);
    static {
        for (PersonaPieceType value : values()) {
            serializeNames.put(value.serializeName, value);
            serializeNames.put(value.type, value);
        }
    }

    PersonaPieceType(String serializeName, String type) {
        this.serializeName = serializeName;
        this.type = type;
    }

    public static PersonaPieceType fromName(String serializeName) {
        PersonaPieceType value = serializeNames.get(serializeName);
        if (value == null) {
            throw new IllegalArgumentException(serializeName);
        }
        return value;
    }
}
