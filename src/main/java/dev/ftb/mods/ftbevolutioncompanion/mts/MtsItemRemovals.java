package dev.ftb.mods.ftbevolutioncompanion.mts;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MtsItemRemovals {
    private static final Logger LOGGER = LoggerFactory.getLogger(MtsItemRemovals.class);

    private static final String TARGET_PACK_ID = "mtsofficialpack";

    private static final Set<String> REMOVED_ITEMS = Set.of(
            "bell206_black",
            "bell206_blackstripe",
            "bell206_blue",
            "bell206_brown",
            "bell206_gray",
            "bell206_green",
            "bell206_olive",
            "bell206_orange",
            "bell206_police",
            "bell206_red",
            "bell206_seagreen",
            "bell206_skyblue",
            "bell206_yellow",
            "comanche_blackred",
            "comanche_blackredstripe",
            "comanche_blue",
            "comanche_orangebrown",
            "comanche_red",
            "comanche_yellow",
            "e500_blackred",
            "e500_blue",
            "e500_extravagant",
            "e500_green",
            "e500_red",
            "e500_silver",
            "e500_yellow",
            "firetruck",
            "ft17_blue",
            "ft17_gray",
            "ft17_olive",
            "ft17_tan",
            "gmcbrig_black",
            "gmcbrig_blackwhite",
            "gmcbrig_blue",
            "gmcbrig_brown",
            "gmcbrig_cream",
            "gmcbrig_gray",
            "gmcbrig_green",
            "gmcbrig_maroon",
            "gmcbrig_red",
            "gmcbrig_tan",
            "gmcbrig_white",
            "pzl37los",
            "pzl37los_arctic",
            "pzl37los_brown",
            "pzl37los_green",
            "pzl37los_tan",
            "pzlp11",
            "pzlp11_brown",
            "pzlp11_green",
            "pzlp11_tan",
            "trimotor_black",
            "trimotor_blue",
            "trimotor_red",
            "trimotor_white",
            "brigbedbox",
            "brigbedflat",
            "brigbedlogs",
            "brigbedtanker_blackred",
            "brigbedtanker_blueorange",
            "brigbedtanker_greenred",
            "brigbedtanker_grey",
            "brigbedtransport",
            "brigbedtransport_brown",
            "brigbedtransport_green",
            "brigbedtransport_tan",
            "camera_vulcanair",
            "cratebell47g",
            "double_bombrack",
            "drill",
            "gunft17turret",
            "gunm1919",
            "gunobserver",
            "gunrocketlauncher",
            "gunrocketpod",
            "heavy_bombrack",
            "hoist_bell206",
            "watercannon"
    );

    private MtsItemRemovals() {
    }

    public static boolean shouldDropDefinition(Object definition) {
        String packID = stringField(definition, "packID");
        if (!TARGET_PACK_ID.equals(packID)) {
            return false;
        }

        String systemName = stringField(definition, "systemName");
        if (systemName == null) {
            return false;
        }

        List<?> subDefinitions = subDefinitions(definition);
        if (subDefinitions == null) {
            if (REMOVED_ITEMS.contains(systemName)) {
                LOGGER.info("Dropping MTS pack item {}:{}", packID, systemName);
                return true;
            }
            return false;
        }

        int before = subDefinitions.size();
        subDefinitions.removeIf(sub -> {
            String subName = stringField(sub, "subName");
            return subName != null && REMOVED_ITEMS.contains(systemName + subName);
        });

        int dropped = before - subDefinitions.size();
        if (dropped > 0) {
            LOGGER.info("Dropped {} of {} variants of MTS pack item {}:{}", dropped, before, packID, systemName);
        }

        return before > 0 && subDefinitions.isEmpty();
    }

    private static List<?> subDefinitions(Object definition) {
        try {
            Object value = definition.getClass().getField("definitions").get(definition);
            return value instanceof List<?> list ? list : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private static String stringField(Object target, String name) {
        try {
            Object value = target.getClass().getField(name).get(target);
            return value instanceof String s ? s : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
