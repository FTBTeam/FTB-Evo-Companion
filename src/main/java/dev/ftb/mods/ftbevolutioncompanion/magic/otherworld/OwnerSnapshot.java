package dev.ftb.mods.ftbevolutioncompanion.magic.otherworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record OwnerSnapshot(UUID owner, double value) {
    public static final OwnerSnapshot NONE = new OwnerSnapshot(new UUID(0L, 0L), 0.0);

    public static final MapCodec<OwnerSnapshot> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(OwnerSnapshot::owner),
            Codec.DOUBLE.fieldOf("value").forGetter(OwnerSnapshot::value)
    ).apply(instance, OwnerSnapshot::new));
}
