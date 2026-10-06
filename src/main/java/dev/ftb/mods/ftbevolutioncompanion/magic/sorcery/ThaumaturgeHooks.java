package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThaumaturgeHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThaumaturgeHooks.class);
    private static final String MOD_ID = "thaumaturge";
    private static final String RESEARCH_COMPLETED_EVENT =
            "com.leclowndu93150.thaumaturge.api.research.ResearchEvent$Completed";

    private static volatile Constructor<?> packConstructor;
    private static volatile Method[] packAccessors;
    private static volatile int powerIndex = -1;
    private static volatile boolean packFailureLogged;

    private ThaumaturgeHooks() {}

    public static Object onFocusCast(LivingEntity caster, Object pack) {
        if (!(caster instanceof ServerPlayer player) || pack == null) {
            return pack;
        }
        MagicRegistry.award(player, MagicRegistry.SPELLS_CAST);
        double power = MagicRegistry.value(player, MagicRegistry.THAUM_FOCUS_POWER);
        return power > 0.0 ? scalePower(pack, (float) (1.0 + power)) : pack;
    }

    public static int wardWarp(ServerPlayer player, int amount) {
        if (amount <= 0 || player == null) {
            return amount;
        }
        double ward = MagicRegistry.value(player, MagicRegistry.THAUM_WARP_WARD);
        if (ward <= 0.0) {
            return amount;
        }
        int kept = 0;
        for (int i = 0; i < amount; i++) {
            if (player.getRandom().nextDouble() >= ward) {
                kept++;
            }
        }
        return kept;
    }

    public static int stabilize(Player player, int instability) {
        if (instability <= 0 || player == null) {
            return instability;
        }
        double stability = MagicRegistry.value(player, MagicRegistry.THAUM_INFUSION_STABILITY);
        if (stability <= 0.0) {
            return instability;
        }
        return Math.max(0, instability - (int) Math.floor(stability + 1.0E-6));
    }

    static void registerResearchListener() {
        if (!ModList.get().isLoaded(MOD_ID)) {
            return;
        }
        try {
            Class<?> type = Class.forName(RESEARCH_COMPLETED_EVENT);
            Method playerAccessor = type.getMethod("player");
            @SuppressWarnings("unchecked")
            Class<Event> eventType = (Class<Event>) type.asSubclass(Event.class);
            NeoForge.EVENT_BUS.addListener(
                    EventPriority.LOWEST, false, eventType, event -> onResearchCompleted(event, playerAccessor));
        } catch (ReflectiveOperationException | ClassCastException e) {
            LOGGER.warn("Could not listen for Thaumaturge research completion", e);
        }
    }

    private static void onResearchCompleted(Event event, Method playerAccessor) {
        try {
            if (playerAccessor.invoke(event) instanceof ServerPlayer player) {
                MagicRegistry.award(player, MagicRegistry.RESEARCH_COMPLETED);
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Could not read the player from a Thaumaturge research event", e);
        }
    }

    private static Object scalePower(Object pack, float factor) {
        try {
            resolvePackType(pack.getClass());
            Method[] accessors = packAccessors;
            Object[] values = new Object[accessors.length];
            for (int i = 0; i < accessors.length; i++) {
                values[i] = accessors[i].invoke(pack);
            }
            values[powerIndex] = (Float) values[powerIndex] * factor;
            return packConstructor.newInstance(values);
        } catch (ReflectiveOperationException | RuntimeException e) {
            if (!packFailureLogged) {
                packFailureLogged = true;
                LOGGER.warn("Could not scale Thaumaturge focus power", e);
            }
            return pack;
        }
    }

    private static void resolvePackType(Class<?> type) throws ReflectiveOperationException {
        if (packConstructor != null) {
            return;
        }
        RecordComponent[] components = type.getRecordComponents();
        if (components == null) {
            throw new NoSuchMethodException(type.getName() + " is not a record");
        }
        Method[] accessors = new Method[components.length];
        Class<?>[] types = new Class<?>[components.length];
        int power = -1;
        for (int i = 0; i < components.length; i++) {
            accessors[i] = components[i].getAccessor();
            types[i] = components[i].getType();
            if ("power".equals(components[i].getName()) && types[i] == float.class) {
                power = i;
            }
        }
        if (power < 0) {
            throw new NoSuchFieldException(type.getName() + ".power");
        }
        Constructor<?> constructor = type.getDeclaredConstructor(types);
        packAccessors = accessors;
        powerIndex = power;
        packConstructor = constructor;
    }
}
