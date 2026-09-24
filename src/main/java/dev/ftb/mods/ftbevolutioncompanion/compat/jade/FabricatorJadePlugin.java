package dev.ftb.mods.ftbevolutioncompanion.compat.jade;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorBlock;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.Locale;

@WailaPlugin
public final class FabricatorJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ServerProvider.INSTANCE, FabricatorBlockEntity.class);
    }
    @Override public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ClientProvider.INSTANCE, FabricatorBlock.class);
    }
    private enum ServerProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public Identifier getUid() { return FTBEvolutionCompanion.id("fabricator"); }
        @Override public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof FabricatorBlockEntity machine)) return;
            data.putString("status", machine.status().name().toLowerCase(Locale.ROOT));
            data.putInt("energy", machine.energy().getAmountAsInt());
            data.putInt("progress", machine.progress());
            data.putInt("duration", machine.duration());
            data.putInt("usage", machine.powerUsage());
            data.putString("stage", machine.requiredStage());
            data.putString("owner", machine.ownerName());
            for (int i = 0; i < 12; i++) {
                ItemStack stack = machine.items().getResource(i).toStack(machine.items().getAmountAsInt(i));
                if (!stack.isEmpty()) data.putString("item" + i, stack.getCount() + " × " + stack.getHoverName().getString());
            }
            for (int i = 0; i < 3; i++) {
                FluidStack stack = machine.fluids().getResource(i).toStack(machine.fluids().getAmountAsInt(i));
                if (!stack.isEmpty()) data.putString("fluid" + i, stack.getAmount() + " mB " + stack.getHoverName().getString());
            }
        }
    }
    private enum ClientProvider implements IBlockComponentProvider {
        INSTANCE;
        private static final String LANG = "fabricator.ftbevolutioncompanion.";
        @Override public Identifier getUid() { return FTBEvolutionCompanion.id("fabricator"); }
        @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains("status")) return;
            tooltip.add(Component.translatable(LANG + "status." + data.getStringOr("status", "idle")));
            tooltip.add(Component.translatable(LANG + "energy", data.getIntOr("energy", 0), FabricatorBlockEntity.ENERGY_CAPACITY));
            if (data.getIntOr("duration", 0) > 0) tooltip.add(Component.translatable(LANG + "process",
                    data.getIntOr("progress", 0), data.getIntOr("duration", 0), data.getIntOr("usage", 0)));
            if (!data.getStringOr("stage", "").isEmpty()) tooltip.add(Component.translatable(LANG + "stage", data.getStringOr("stage", "")));
            tooltip.add(Component.translatable(LANG + "owner", data.getStringOr("owner", "—")));
            for (int i = 0; i < 12; i++) {
                if (data.contains("item" + i)) tooltip.add(Component.translatable(LANG + (i < 9 ? "input_contents" : "output_contents"), data.getStringOr("item" + i, "")));
            }
            for (int i = 0; i < 3; i++) {
                if (data.contains("fluid" + i)) tooltip.add(Component.translatable(LANG + (i < 2 ? "input_contents" : "output_contents"), data.getStringOr("fluid" + i, "")));
            }
        }
    }
}
