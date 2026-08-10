package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.ship.ShipBinding;
import dev.ftb.mods.ftbevolutioncompanion.ship.ShipBindings;
import dev.ftb.mods.ftbevolutioncompanion.ship.ShipHomeData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Unique
    private static final Logger ftbevolutioncompanion$LOG = LoggerFactory.getLogger("ShipHome");

    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    private void ftbevolutioncompanion$captureBedBinding(ResourceKey<Level> dimension, BlockPos position, float angle,
                                                 boolean forced, boolean broadcast, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        MinecraftServer server = self.getServer();
        if (server == null) return;
        ShipHomeData data = ShipHomeData.get(server);

        if (position == null) {
            data.clearBed(self.getUUID());
            ftbevolutioncompanion$LOG.debug("[setBed] {} cleared (null position)", self.getName().getString());
            return;
        }

        ServerLevel targetLevel = server.getLevel(dimension);
        if (targetLevel == null) {
            data.clearBed(self.getUUID());
            return;
        }

        Vec3 worldPos = self.position();
        ShipBindings.dumpDiagnostics("setBed", self);
        ftbevolutioncompanion$LOG.debug("[setBed] {} bed BlockPos arg={} player world pos={} (using player pos for capture, since on a sub-level the bed BlockPos is storage coords)",
                self.getName().getString(), position, worldPos);
        Optional<ShipBinding> binding = ShipBindings.captureForPlayer(self, worldPos, angle, self.getXRot());
        if (binding.isEmpty()) {
            data.clearBed(self.getUUID());
            ftbevolutioncompanion$LOG.debug("[setBed] {} no sub-level/contraption — vanilla BlockPos only",
                    self.getName().getString());
            return;
        }
        data.setBed(self.getUUID(), binding.get());
        ftbevolutioncompanion$LOG.debug("[setBed] {} captured anchor={} localOffset={}",
                self.getName().getString(), binding.get().shipUuid(), binding.get().localOffset());
    }
}
