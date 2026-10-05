package dev.ftb.mods.ftbevolutioncompanion.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public final class RecipePlacementLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipePlacementLog.class);
    private static final AtomicInteger UNPLACEABLE = new AtomicInteger();

    private RecipePlacementLog() {}

    public static void unplaceable(Object recipeId) {
        UNPLACEABLE.incrementAndGet();
        LOGGER.debug("Recipe {} has no recipe book placement", recipeId);
    }

    public static void summary() {
        int count = UNPLACEABLE.getAndSet(0);
        if (count > 0) {
            LOGGER.info(
                    "{} recipes have no recipe book placement (machine recipes or custom ingredients); "
                            + "they still work, enable DEBUG for {} to list them",
                    count,
                    RecipePlacementLog.class.getName());
        }
    }
}
