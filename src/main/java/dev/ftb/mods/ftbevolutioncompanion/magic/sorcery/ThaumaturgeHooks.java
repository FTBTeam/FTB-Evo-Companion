package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
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
    private static final String SPELL_CAST_PRE_EVENT =
            "com.leclowndu93150.thaumaturge.api.spell.event.SpellCastEvent$Pre";
    private static final String SPELL_CAST_POST_EVENT =
            "com.leclowndu93150.thaumaturge.api.spell.event.SpellCastEvent$Post";
    private static final String CHANNELED_STYLE = "CHANNELED";
    private static final long CHANNEL_AWARD_INTERVAL = 20L;

    private static final Map<UUID, Long> CHANNEL_AWARDS = new ConcurrentHashMap<>();
    private static volatile boolean spellFailureLogged;

    private ThaumaturgeHooks() {}

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

    static void registerSpellListeners() {
        try {
            Class<?> pre = Class.forName(SPELL_CAST_PRE_EVENT);
            Class<?> post = Class.forName(SPELL_CAST_POST_EVENT);
            Method caster = pre.getMethod("caster");
            Method style = pre.getMethod("style");
            Method power = pre.getMethod("power");
            Method setPower = pre.getMethod("setPower", float.class);
            @SuppressWarnings("unchecked")
            Class<Event> preType = (Class<Event>) pre.asSubclass(Event.class);
            @SuppressWarnings("unchecked")
            Class<Event> postType = (Class<Event>) post.asSubclass(Event.class);
            NeoForge.EVENT_BUS.addListener(
                    EventPriority.NORMAL, false, preType, event -> onSpellCast(event, caster, power, setPower));
            NeoForge.EVENT_BUS.addListener(
                    EventPriority.LOWEST, false, postType, event -> onSpellCompleted(event, caster, style));
        } catch (ReflectiveOperationException | ClassCastException e) {
            LOGGER.warn("Could not listen for Thaumaturge spell casts", e);
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

    private static void onSpellCast(Event event, Method caster, Method power, Method setPower) {
        try {
            if (!(caster.invoke(event) instanceof ServerPlayer player)) {
                return;
            }
            double bonus = MagicRegistry.value(player, MagicRegistry.THAUM_FOCUS_POWER);
            if (bonus > 0.0) {
                setPower.invoke(event, (Float) power.invoke(event) * (float) (1.0 + bonus));
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            logSpellFailure(e);
        }
    }

    private static void onSpellCompleted(Event event, Method caster, Method style) {
        try {
            if (!(caster.invoke(event) instanceof ServerPlayer player)) {
                return;
            }
            if (style.invoke(event) instanceof Enum<?> castStyle && CHANNELED_STYLE.equals(castStyle.name())) {
                long now = player.level().getGameTime();
                Long last = CHANNEL_AWARDS.get(player.getUUID());
                if (last != null && now - last < CHANNEL_AWARD_INTERVAL && now >= last) {
                    return;
                }
                CHANNEL_AWARDS.put(player.getUUID(), now);
            }
            MagicRegistry.award(player, MagicRegistry.SPELLS_CAST);
        } catch (ReflectiveOperationException | RuntimeException e) {
            logSpellFailure(e);
        }
    }

    private static void logSpellFailure(Exception e) {
        if (!spellFailureLogged) {
            spellFailureLogged = true;
            LOGGER.warn("Could not apply Thaumaturge spell bonuses", e);
        }
    }
}
