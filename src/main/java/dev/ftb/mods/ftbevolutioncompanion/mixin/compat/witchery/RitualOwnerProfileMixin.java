package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.witchery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.witchery.content.ritual_event.SummonItemWithOwnerRitualEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SummonItemWithOwnerRitualEvent.class, remap = false)
public abstract class RitualOwnerProfileMixin {
    @WrapOperation(
            method = "buildStacks",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/player/Player;getProfile()Lnet/minecraft/world/item/component/ResolvableProfile;"))
    private ResolvableProfile ftbevo$realOwnerProfile(Player owner, Operation<ResolvableProfile> original) {
        if (!(owner instanceof FakePlayer)) {
            return original.call(owner);
        }
        MinecraftServer server = owner.level().getServer();
        ServerPlayer realOwner = server == null ? null : server.getPlayerList().getPlayer(owner.getUUID());
        return realOwner != null ? realOwner.getProfile() : ResolvableProfile.createUnresolved(owner.getUUID());
    }
}
