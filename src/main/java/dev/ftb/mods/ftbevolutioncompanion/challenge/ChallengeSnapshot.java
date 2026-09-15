package dev.ftb.mods.ftbevolutioncompanion.challenge;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public record ChallengeSnapshot(long chapterId, List<Entry> entries) {
    public static final ChallengeSnapshot EMPTY = new ChallengeSnapshot(0L, List.of());

    public record Entry(int rank, Component teamName, int percent, int completed, int total) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Entry::rank,
                ComponentSerialization.STREAM_CODEC, Entry::teamName,
                ByteBufCodecs.VAR_INT, Entry::percent,
                ByteBufCodecs.VAR_INT, Entry::completed,
                ByteBufCodecs.VAR_INT, Entry::total,
                Entry::new
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ChallengeSnapshot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ChallengeSnapshot::chapterId,
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ChallengeSnapshot::entries,
            ChallengeSnapshot::new
    );

    public boolean hasChapter() {
        return chapterId != 0L;
    }

    public Optional<Entry> entry(int rank) {
        return entries.stream().filter(entry -> entry.rank() == rank).findFirst();
    }
}
