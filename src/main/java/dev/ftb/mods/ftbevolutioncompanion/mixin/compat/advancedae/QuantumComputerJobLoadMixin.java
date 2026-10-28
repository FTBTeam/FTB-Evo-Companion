package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.advancedae;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPUCluster", remap = false)
public abstract class QuantumComputerJobLoadMixin {
    @ModifyConstant(method = "readFromNBT", constant = @Constant(stringValue = "cpuList"))
    private String ftbevo$readSavedCpuList(String key) {
        return "cpus";
    }

    @WrapOperation(
            method = "readFromNBT",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/storage/ValueInput;childOrEmpty(Ljava/lang/String;)Lnet/minecraft/world/level/storage/ValueInput;"))
    private ValueInput ftbevo$readCpuFromEntry(
            ValueInput input, String key, Operation<ValueInput> original, @Local(ordinal = 1) ValueInput entry) {
        return original.call(entry, key);
    }

    @ModifyArg(
            method = "readFromNBT",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/util/IConfigManager;readFromNBT(Lnet/minecraft/world/level/storage/ValueInput;)Z"))
    private ValueInput ftbevo$readConfigChild(ValueInput input) {
        return input.childOrEmpty("config");
    }
}
