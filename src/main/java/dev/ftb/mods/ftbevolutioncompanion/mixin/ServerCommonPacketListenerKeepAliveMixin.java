package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerKeepAliveMixin {
    @ModifyConstant(method = "keepConnectionAlive", constant = @Constant(longValue = 15000L))
    private long ftbevo$extendKeepAliveDuringConfiguration(long original) {
        return (Object) this instanceof ServerConfigurationPacketListenerImpl ? 45000L : original;
    }
}
