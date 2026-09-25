package dev.ftb.mods.ftbevolutioncompanion;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CompanionSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> FABRICATOR_WORKING = register("block.ftb_fabricator.working");
    public static final DeferredHolder<SoundEvent, SoundEvent> FABRICATOR_COMPLETE = register("block.ftb_fabricator.complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> SKYLINE_LAUNCH_START = register("block.ftb_skyline.launch_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> SKYLINE_LIFTOFF = register("block.ftb_skyline.liftoff");

    private CompanionSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(FTBEvolutionCompanion.id(name)));
    }
}
