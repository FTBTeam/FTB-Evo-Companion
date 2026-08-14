package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.util.Util;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationSlowJoinMixin {
    @Unique
    private static final Logger ftbevo$LOGGER = LoggerFactory.getLogger("FTBEvoCompanion/SlowJoin");

    @Unique
    private static final long ftbevo$REPORT_AFTER_MS = 30000L;

    @Unique
    private static final long ftbevo$REPORT_EVERY_MS = 10000L;

    @Unique
    private long ftbevo$startedAt = 0L;

    @Unique
    private long ftbevo$lastReportAt = 0L;

    @Unique
    private boolean ftbevo$reported = false;

    @Shadow
    private ConfigurationTask currentTask;

    @Shadow
    protected abstract GameProfile playerProfile();

    @Inject(method = "tick", at = @At("HEAD"))
    private void ftbevo$reportSlowConfiguration(CallbackInfo ci) {
        final long now = Util.getMillis();

        if (ftbevo$startedAt == 0L) {
            ftbevo$startedAt = now;
            return;
        }

        final long elapsed = now - ftbevo$startedAt;

        if (elapsed < ftbevo$REPORT_AFTER_MS || now - ftbevo$lastReportAt < ftbevo$REPORT_EVERY_MS) {
            return;
        }

        ftbevo$lastReportAt = now;
        ftbevo$reported = true;

        final ConfigurationTask task = this.currentTask;
        final GameProfile profile = this.playerProfile();

        ftbevo$LOGGER.warn(
                "{} ({}) has been in the configuration phase for {}s, current task = {}",
                profile.name(),
                profile.id(),
                elapsed / 1000L,
                task == null ? "<none>" : task.type().id());
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void ftbevo$reportSlowConfigurationOutcome(DisconnectionDetails details, CallbackInfo ci) {
        if (!ftbevo$reported) {
            return;
        }

        final ConfigurationTask task = this.currentTask;

        ftbevo$LOGGER.warn(
                "{} never finished the configuration phase after {}s, stuck on task = {}, reason = {}",
                this.playerProfile().name(),
                (Util.getMillis() - ftbevo$startedAt) / 1000L,
                task == null ? "<none>" : task.type().id(),
                details.reason().getString());
    }
}
