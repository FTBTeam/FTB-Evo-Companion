package dev.ftb.mods.ftbevolutioncompanion;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.athletics.network.AthleticsPayloads;
import dev.ftb.mods.ftbevolutioncompanion.client.AthleticsClientHandler;
import dev.ftb.mods.ftbevolutioncompanion.client.AthleticsKeys;
import dev.ftb.mods.ftbevolutioncompanion.client.SkillsClientHandler;
import dev.ftb.mods.ftbevolutioncompanion.client.SkillsKeys;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsCommand;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.CombatTicker;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.IncomingDamage;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.OutgoingDamage;
import dev.ftb.mods.ftbevolutioncompanion.skills.network.SkillsPayloads;
import dev.ftb.mods.ftbevolutioncompanion.worldgen.MeteoriteSpacing;

import net.minecraft.resources.Identifier;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FTBEvolutionCompanion.MOD_ID)
public class FTBEvolutionCompanion {
    public static final String MOD_ID = "ftbevolutioncompanion";

    private static final Logger LOGGER = LoggerFactory.getLogger(FTBEvolutionCompanion.class);

    public FTBEvolutionCompanion(IEventBus eventBus, ModContainer container, Dist dist) {
        container.registerConfig(ModConfig.Type.COMMON, CompanionConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(MeteoriteSpacing::onServerAboutToStart);

        AthleticsRegistry.ATTRIBUTES.register(eventBus);
        AthleticsRegistry.ATTACHMENTS.register(eventBus);
        eventBus.addListener(AthleticsRegistry::onEntityAttributeModification);

        SkillsRegistry.ATTRIBUTES.register(eventBus);
        SkillsRegistry.EFFECTS.register(eventBus);
        SkillsRegistry.ATTACHMENTS.register(eventBus);
        eventBus.addListener(SkillsRegistry::onEntityAttributeModification);
        eventBus.addListener(SkillsPayloads::register);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(SkillsCommand::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(OutgoingDamage::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(IncomingDamage::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(IncomingDamage::onDamagePre);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onDamagePost);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onProjectileImpact);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onShieldBlock);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingFall);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingVisibility);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingDrops);
        eventBus.addListener(AthleticsPayloads::register);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerTick);

        if (dist == Dist.CLIENT) {
            eventBus.addListener(AthleticsKeys::onRegisterKeyMappings);
            NeoForge.EVENT_BUS.addListener(AthleticsClientHandler::onClientTick);
            eventBus.addListener(SkillsKeys::onRegisterKeyMappings);
            NeoForge.EVENT_BUS.addListener(SkillsClientHandler::onClientTick);
            eventBus.<FMLClientSetupEvent>addListener(event -> clientSetup(event, eventBus));
        }
    }

    private void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        // Client init
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
