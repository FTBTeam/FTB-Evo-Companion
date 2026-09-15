package dev.ftb.mods.ftbevolutioncompanion.challenge;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class ChallengeRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChallengeBoardBlockEntity>> CHALLENGE_BOARD =
            BLOCK_ENTITIES.register("challenge_board",
                    () -> new BlockEntityType<>(ChallengeBoardBlockEntity::new, Set.of(CompanionContent.CHALLENGE_BOARD.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChallengeBoardAuxBlockEntity>> CHALLENGE_BOARD_AUX =
            BLOCK_ENTITIES.register("challenge_board_aux",
                    () -> new BlockEntityType<>(ChallengeBoardAuxBlockEntity::new, Set.of(CompanionContent.CHALLENGE_BOARD_AUX.get())));

    private ChallengeRegistry() {
    }
}
