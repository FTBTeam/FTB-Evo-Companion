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
    public long ninjaUntil;
    public long lastShieldHealTime;
    public UUID pendingEchoTarget;
    public float pendingEchoAmount;
    public long pendingEchoTick;
    public UUID skillLightningBoltId;
}
