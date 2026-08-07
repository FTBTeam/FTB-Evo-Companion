package dev.ftb.mods.ftbevolutioncompanion.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SkillToggles(boolean fasterStrikes, boolean unarmedRamp, boolean cheatDeath,
                           boolean lightning, boolean shadowStep, boolean blademaster) {
    public static final SkillToggles DEFAULT = new SkillToggles(true, true, true, true, true, true);

    public static final MapCodec<SkillToggles> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("faster_strikes", true).forGetter(SkillToggles::fasterStrikes),
            Codec.BOOL.optionalFieldOf("unarmed_ramp", true).forGetter(SkillToggles::unarmedRamp),
            Codec.BOOL.optionalFieldOf("cheat_death", true).forGetter(SkillToggles::cheatDeath),
            Codec.BOOL.optionalFieldOf("lightning", true).forGetter(SkillToggles::lightning),
            Codec.BOOL.optionalFieldOf("shadow_step", true).forGetter(SkillToggles::shadowStep),
            Codec.BOOL.optionalFieldOf("blademaster", true).forGetter(SkillToggles::blademaster)
    ).apply(instance, SkillToggles::new));

    public static final StreamCodec<ByteBuf, SkillToggles> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SkillToggles::fasterStrikes,
            ByteBufCodecs.BOOL, SkillToggles::unarmedRamp,
            ByteBufCodecs.BOOL, SkillToggles::cheatDeath,
            ByteBufCodecs.BOOL, SkillToggles::lightning,
            ByteBufCodecs.BOOL, SkillToggles::shadowStep,
            ByteBufCodecs.BOOL, SkillToggles::blademaster,
            SkillToggles::new);

    public enum Toggle {
        FASTER_STRIKES("faster_strikes"),
        UNARMED_RAMP("unarmed_ramp"),
        CHEAT_DEATH("cheat_death"),
        LIGHTNING("lightning"),
        SHADOW_STEP("shadow_step"),
        BLADEMASTER("blademaster");

        private final String key;

        Toggle(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }

        public static Toggle byIndex(int index) {
            Toggle[] values = values();
            return values[Math.floorMod(index, values.length)];
        }
    }

    public boolean get(Toggle toggle) {
        return switch (toggle) {
            case FASTER_STRIKES -> fasterStrikes;
            case UNARMED_RAMP -> unarmedRamp;
            case CHEAT_DEATH -> cheatDeath;
            case LIGHTNING -> lightning;
            case SHADOW_STEP -> shadowStep;
            case BLADEMASTER -> blademaster;
        };
    }

    public SkillToggles toggled(Toggle toggle) {
        return switch (toggle) {
            case FASTER_STRIKES -> new SkillToggles(!fasterStrikes, unarmedRamp, cheatDeath, lightning, shadowStep, blademaster);
            case UNARMED_RAMP -> new SkillToggles(fasterStrikes, !unarmedRamp, cheatDeath, lightning, shadowStep, blademaster);
            case CHEAT_DEATH -> new SkillToggles(fasterStrikes, unarmedRamp, !cheatDeath, lightning, shadowStep, blademaster);
            case LIGHTNING -> new SkillToggles(fasterStrikes, unarmedRamp, cheatDeath, !lightning, shadowStep, blademaster);
            case SHADOW_STEP -> new SkillToggles(fasterStrikes, unarmedRamp, cheatDeath, lightning, !shadowStep, blademaster);
            case BLADEMASTER -> new SkillToggles(fasterStrikes, unarmedRamp, cheatDeath, lightning, shadowStep, !blademaster);
        };
    }
}
