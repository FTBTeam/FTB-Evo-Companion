package dev.ftb.mods.ftbevolutioncompanion.compat.gateways;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RunToPortalGoal extends Goal {
    private final PathfinderMob mob;
    private final Entity portal;
    private final double speed;

    public RunToPortalGoal(PathfinderMob mob, Entity portal, double speed) {
        this.mob = mob;
        this.portal = portal;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return this.portal.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.setTarget(null);
        this.mob.getLookControl().setLookAt(this.portal);
        if (this.mob.tickCount % 5 == 0 || this.mob.getNavigation().isDone()) {
            this.mob.getNavigation().moveTo(this.portal.getX(), this.portal.getY(), this.portal.getZ(), this.speed);
        }
    }
}
