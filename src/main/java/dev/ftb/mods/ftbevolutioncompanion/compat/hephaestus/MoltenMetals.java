package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class MoltenMetals {
    private static final String NAMESPACE = "ftb";

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, NAMESPACE);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, NAMESPACE);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NAMESPACE);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NAMESPACE);

    public static final List<Entry> ENTRIES = load();

    private MoltenMetals() {}

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        eventBus.addListener(MoltenMetals::onBuildCreativeTabs);
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ENTRIES.forEach(entry -> event.accept(entry.bucket));
        }
    }

    private static List<Entry> load() {
        List<Entry> entries = new ArrayList<>();
        try (InputStream stream = MoltenMetals.class.getResourceAsStream("molten_metals.json")) {
            if (stream == null) {
                throw new IllegalStateException("molten_metals.json is missing");
            }
            JsonArray array = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                if (object.has("requires")
                        && !ModList.get().isLoaded(object.get("requires").getAsString())) {
                    continue;
                }
                entries.add(new Entry(
                        object.get("name").getAsString(),
                        Integer.decode(object.get("colour").getAsString()),
                        object.get("temperature").getAsInt()));
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not read molten_metals.json", e);
        }
        return List.copyOf(entries);
    }

    public static final class Type extends FluidType {
        private final int tint;

        public Type(Properties properties, int tint) {
            super(properties);
            this.tint = tint;
        }

        public int tint() {
            return tint;
        }
    }

    public static final class Entry {
        public final String name;
        public final DeferredHolder<FluidType, Type> type;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
        public final DeferredBlock<LiquidBlock> block;
        public final DeferredItem<BucketItem> bucket;

        Entry(String name, int colour, int temperature) {
            this.name = name;
            int tint = 0xFF000000 | colour;
            this.type = FLUID_TYPES.register(
                    name,
                    () -> new Type(
                            FluidType.Properties.create()
                                    .descriptionId("fluid_type.ftb." + name)
                                    .temperature(temperature)
                                    .density(2000)
                                    .viscosity(10000)
                                    .lightLevel(10)
                                    .canSwim(false)
                                    .canDrown(false)
                                    .canExtinguish(false)
                                    .canConvertToSource(false)
                                    .supportsBoating(false)
                                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
                            tint));
            this.source = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(properties()));
            this.flowing = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(properties()));
            this.block = BLOCKS.registerBlock(
                    name + "_block",
                    properties -> new LiquidBlock(source.get(), properties),
                    properties -> properties
                            .noCollision()
                            .replaceable()
                            .strength(100.0F)
                            .lightLevel(state -> 10)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY));
            this.bucket = ITEMS.registerItem(
                    name + "_bucket",
                    properties -> new BucketItem(source.get(), properties),
                    properties -> properties.craftRemainder(Items.BUCKET).stacksTo(1));
        }

        private BaseFlowingFluid.Properties properties() {
            return new BaseFlowingFluid.Properties(type, source, flowing)
                    .bucket(bucket)
                    .block(block)
                    .tickRate(30)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2);
        }
    }
}
