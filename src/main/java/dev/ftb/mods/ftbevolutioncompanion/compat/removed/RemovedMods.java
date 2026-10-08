package dev.ftb.mods.ftbevolutioncompanion.compat.removed;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class RemovedMods {
    private static final List<String> MODS = List.of("thaumaturge");
    private static final Identifier STAND_IN_MODEL = FTBEvolutionCompanion.id("missing_mod_item");
    private static final Identifier AIR = Identifier.withDefaultNamespace("air");
    private static final Identifier EMPTY_FLUID = Identifier.withDefaultNamespace("empty");
    private static final MapCodec<Dynamic<?>> RAW_MAP = MapCodec.assumeMapUnsafe(Codec.PASSTHROUGH);

    private RemovedMods() {}

    public static void register(IEventBus eventBus) {
        for (String mod : MODS) {
            if (!ModList.get().isLoaded(mod)) {
                registerMod(mod, load(mod), eventBus);
            }
        }
    }

    private static JsonObject load(String mod) {
        try (InputStream stream = RemovedMods.class.getResourceAsStream(mod + ".json")) {
            if (stream == null) {
                throw new IllegalStateException(mod + ".json is missing");
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + mod + ".json", e);
        }
    }

    private static void registerMod(String mod, JsonObject data, IEventBus eventBus) {
        DeferredRegister<Block> blocks = DeferredRegister.create(Registries.BLOCK, FTBEvolutionCompanion.MOD_ID);
        data.getAsJsonObject("blocks")
                .entrySet()
                .forEach(e -> blocks.addAlias(
                        Identifier.parse(e.getKey()),
                        Identifier.parse(e.getValue().getAsString())));
        strings(data.getAsJsonArray("air_blocks")).forEach(id -> blocks.addAlias(Identifier.parse(id), AIR));
        blocks.register(eventBus);

        DeferredRegister<Fluid> fluids = DeferredRegister.create(Registries.FLUID, FTBEvolutionCompanion.MOD_ID);
        strings(data.getAsJsonArray("fluids")).forEach(id -> fluids.addAlias(Identifier.parse(id), EMPTY_FLUID));
        fluids.register(eventBus);

        DeferredRegister<Item> itemAliases = DeferredRegister.create(Registries.ITEM, FTBEvolutionCompanion.MOD_ID);
        data.getAsJsonObject("items")
                .entrySet()
                .forEach(e -> itemAliases.addAlias(
                        Identifier.parse(e.getKey()),
                        Identifier.parse(e.getValue().getAsString())));
        itemAliases.register(eventBus);

        Map<String, DeferredRegister.Items> standIns = new HashMap<>();
        Component modName = Component.translatable("ftbevolutioncompanion.removed_mod." + mod);
        for (String id : strings(data.getAsJsonArray("stand_in_items"))) {
            Identifier key = Identifier.parse(id);
            standIns.computeIfAbsent(key.getNamespace(), DeferredRegister::createItems)
                    .registerItem(
                            key.getPath(),
                            properties -> new StandInItem(properties, key, modName),
                            properties -> properties.stacksTo(99).component(DataComponents.ITEM_MODEL, STAND_IN_MODEL));
        }
        standIns.values().forEach(register -> register.register(eventBus));

        DeferredRegister<DataComponentType<?>> components =
                DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, mod);
        for (String id : strings(data.getAsJsonArray("components"))) {
            components.register(
                    Identifier.parse(id).getPath(),
                    () -> DataComponentType.<Dynamic<?>>builder()
                            .persistent(Codec.PASSTHROUGH)
                            .networkSynchronized(ByteBufCodecs.fromCodec(Codec.PASSTHROUGH))
                            .build());
        }
        components.register(eventBus);

        DeferredRegister<AttachmentType<?>> attachments =
                DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, mod);
        for (String id : strings(data.getAsJsonArray("attachments"))) {
            attachments.register(
                    Identifier.parse(id).getPath(),
                    () -> AttachmentType.<Dynamic<?>>builder(() -> new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()))
                            .serialize(RAW_MAP)
                            .copyOnDeath()
                            .build());
        }
        attachments.register(eventBus);
    }

    private static List<String> strings(JsonArray array) {
        return array.asList().stream().map(JsonElement::getAsString).toList();
    }

    public static final class StandInItem extends Item {
        private final Identifier originalId;
        private final Component modName;

        StandInItem(Properties properties, Identifier originalId, Component modName) {
            super(properties);
            this.originalId = originalId;
            this.modName = modName;
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable(
                    "item.ftbevolutioncompanion.missing_mod_item", Component.literal(originalId.toString()));
        }

        @Override
        public void appendHoverText(
                ItemStack stack,
                TooltipContext context,
                TooltipDisplay display,
                Consumer<Component> tooltip,
                TooltipFlag flag) {
            tooltip.accept(Component.translatable("tooltip.ftbevolutioncompanion.missing_mod_item", modName));
        }
    }
}
