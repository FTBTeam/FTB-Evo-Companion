package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillToggles;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.network.SkillsPayloads;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class SkillsClientHandler {
    private SkillsClientHandler() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            drainKeys();
            return;
        }

        sendToggle(SkillsKeys.TOGGLE_FASTER_STRIKES, SkillToggles.Toggle.FASTER_STRIKES);
        sendToggle(SkillsKeys.TOGGLE_UNARMED_RAMP, SkillToggles.Toggle.UNARMED_RAMP);
        sendToggle(SkillsKeys.TOGGLE_CHEAT_DEATH, SkillToggles.Toggle.CHEAT_DEATH);
        sendToggle(SkillsKeys.TOGGLE_LIGHTNING, SkillToggles.Toggle.LIGHTNING);
        sendToggle(SkillsKeys.TOGGLE_SHADOW_STEP, SkillToggles.Toggle.SHADOW_STEP);
        sendToggle(SkillsKeys.TOGGLE_BLADEMASTER, SkillToggles.Toggle.BLADEMASTER);
        sendToggle(SkillsKeys.TOGGLE_RAIN_OF_ARROWS, SkillToggles.Toggle.RAIN_OF_ARROWS);
        sendToggle(SkillsKeys.TOGGLE_PIERCING_STRIKE, SkillToggles.Toggle.PIERCING_STRIKE);

        while (SkillsKeys.ACTIVATE_NINJA.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SkillsPayloads.ActivateSkill(SkillsAbilities.ACTIVATE_NINJA));
        }
    }

    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        trySendShadowStep(event.getEntity());
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            trySendShadowStep(event.getEntity());
        }
    }

    private static void trySendShadowStep(Player player) {
        if (player.level().isClientSide() && player.isShiftKeyDown()
                && SkillsHelper.isSword(player.getMainHandItem())
                && SkillsAbilities.toggles(player).shadowStep()) {
            ClientPacketDistributor.sendToServer(new SkillsPayloads.ActivateSkill(SkillsAbilities.ACTIVATE_SHADOW_STEP));
        }
    }

    private static void sendToggle(KeyMapping key, SkillToggles.Toggle toggle) {
        while (key.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SkillsPayloads.ToggleSkill(toggle.ordinal()));
        }
    }

    private static void drainKeys() {
        while (SkillsKeys.ACTIVATE_NINJA.consumeClick()
                || SkillsKeys.TOGGLE_LIGHTNING.consumeClick()
                || SkillsKeys.TOGGLE_FASTER_STRIKES.consumeClick()
                || SkillsKeys.TOGGLE_UNARMED_RAMP.consumeClick()
                || SkillsKeys.TOGGLE_CHEAT_DEATH.consumeClick()
                || SkillsKeys.TOGGLE_SHADOW_STEP.consumeClick()
                || SkillsKeys.TOGGLE_BLADEMASTER.consumeClick()
                || SkillsKeys.TOGGLE_RAIN_OF_ARROWS.consumeClick()
                || SkillsKeys.TOGGLE_PIERCING_STRIKE.consumeClick()) {
        }
    }
}
