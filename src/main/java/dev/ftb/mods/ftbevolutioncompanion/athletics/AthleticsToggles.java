package dev.ftb.mods.ftbevolutioncompanion.athletics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AthleticsToggles(boolean extraJumps, boolean wallClimb, boolean airDash) {
    public static final AthleticsToggles DEFAULT = new AthleticsToggles(true, true, true);

    public static final MapCodec<AthleticsToggles> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("extra_jumps", true).forGetter(AthleticsToggles::extraJumps),
            Codec.BOOL.optionalFieldOf("wall_climb", true).forGetter(AthleticsToggles::wallClimb),
            Codec.BOOL.optionalFieldOf("air_dash", true).forGetter(AthleticsToggles::airDash)
    ).apply(instance, AthleticsToggles::new));

    public static final StreamCodec<ByteBuf, AthleticsToggles> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AthleticsToggles::extraJumps,
            ByteBufCodecs.BOOL, AthleticsToggles::wallClimb,
            ByteBufCodecs.BOOL, AthleticsToggles::airDash,
            AthleticsToggles::new);

    public boolean get(AthleticsAbilities.Ability ability) {
        return switch (ability) {
            case EXTRA_JUMPS -> extraJumps;
            case WALL_CLIMB -> wallClimb;
            case AIR_DASH -> airDash;
        };
    }

    public AthleticsToggles toggled(AthleticsAbilities.Ability ability) {
        return switch (ability) {
            case EXTRA_JUMPS -> new AthleticsToggles(!extraJumps, wallClimb, airDash);
            case WALL_CLIMB -> new AthleticsToggles(extraJumps, !wallClimb, airDash);
            case AIR_DASH -> new AthleticsToggles(extraJumps, wallClimb, !airDash);
        };
    }
}
