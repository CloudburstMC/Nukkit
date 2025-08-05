package cn.nukkit.item;

import lombok.Getter;

public interface ItemTrimPattern {

    Type getPattern();

    enum Type {

        BOLT("bolt"),
        COAST("coast"),
        DUNE("dune"),
        EYE("eye"),
        FLOW("flow"),
        HOST("host"),
        RAISER("raiser"),
        RIB("rib"),
        SENTRY("sentry"),
        SHAPER("shaper"),
        SILENCE("silence"),
        SNOUT("snout"),
        SPIRE("spire"),
        TIDE("tide"),
        VEX("vex"),
        WARD("ward"),
        WAYFINDER("wayfinder"),
        WILD("wild");

        @Getter
        private final String trimPattern;

        Type(String name) {
            this.trimPattern = name;
        }
    }
}
