package dev.ftb.mods.ftbevolutioncompanion.challenge;

public final class ChallengeClientData {
    private static ChallengeSnapshot snapshot = ChallengeSnapshot.EMPTY;

    private ChallengeClientData() {
    }

    public static ChallengeSnapshot snapshot() {
        return snapshot;
    }

    public static void accept(ChallengeSnapshot next) {
        snapshot = next;
    }

    public static void clear() {
        snapshot = ChallengeSnapshot.EMPTY;
    }
}
