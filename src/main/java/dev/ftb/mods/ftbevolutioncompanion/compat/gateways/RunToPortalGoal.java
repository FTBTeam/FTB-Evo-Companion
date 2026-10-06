package dev.ftb.mods.ftbevolutioncompanion.compat.gateways;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RunToPortalGoal extends Goal {
    private static final double PLAYER_RANGE = 16.0;
    private static final double MAX_PORTAL_DISTANCE_SQ = 14.0 * 14.0;

    private final PathfinderMob mob;
    private final Entity portal;
    private final double speed;
    private final long dashAt;

    public RunToPortalGoal(PathfinderMob mob, Entity portal, double speed, long dashAt) {
        this.mob = mob;
        this.portal = portal;
        this.speed = speed;
        this.dashAt = dashAt;
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
        if (this.mob.level().getGameTime() >= this.dashAt) {
            this.mob.getLookControl().setLookAt(this.portal);
            if (this.mob.tickCount % 5 == 0 || this.mob.getNavigation().isDone()) {
                this.mob.getNavigation().moveTo(this.portal.getX(), this.portal.getY(), this.portal.getZ(), this.speed);
            }
            return;
        }
        if (this.mob.tickCount % 10 != 0 && !this.mob.getNavigation().isDone()) {
            return;
        }
        Vec3 target = null;
        if (this.mob.distanceToSqr(this.portal) > MAX_PORTAL_DISTANCE_SQ) {
            target = DefaultRandomPos.getPosTowards(this.mob, 10, 5, this.portal.position(), Math.PI / 2.0);
        } else {
            Player player = this.mob.level().getNearestPlayer(this.mob, PLAYER_RANGE);
            target = player != null
                    ? DefaultRandomPos.getPosAway(this.mob, 10, 5, player.position())
                    : DefaultRandomPos.getPos(this.mob, 8, 4);
        }
        if (target != null) {
            this.mob.getNavigation().moveTo(target.x, target.y, target.z, this.speed);
        }
    }
}
