package dev.ftb.mods.ftbevolutioncompanion.skills;

import java.util.UUID;

public final class CombatState {
    public int archerRampStacks;
    public long lastArcherHitTime;
    public int unarmedHitCounter;
    public int fasterStrikesStacks;
    public long lastUnarmedHitTime;
    public int unarmedRampStacks;
    public int axeHitCounter;
    public int swordHitCounter;
    public int crossbowShotCounter;
    public long lastCrossbowShotTick;
    public long riposteReadyUntil;
    public long ninjaUntil;
    public long lastShieldHealTime;
    public UUID pendingEchoTarget;
    public float pendingEchoAmount;
    public long pendingEchoTick;
    public long lastJabTick;
    public int jabTargetsThisTick;
    public float lastScytheHitDamage;
    public boolean lancerLeapPending;
    public long lancerLeapStart;
}
