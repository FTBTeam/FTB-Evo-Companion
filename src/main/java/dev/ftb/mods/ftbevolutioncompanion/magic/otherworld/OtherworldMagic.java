package dev.ftb.mods.ftbevolutioncompanion.magic.otherworld;

import com.klikli_dev.occultism.common.blockentity.GoldenSacrificialBowlBlockEntity;
import com.klikli_dev.occultism.common.entity.spirit.SpiritEntity;
import dev.anima.MachineBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.Nullable;

public final class OtherworldMagic {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<OwnerSnapshot>> RITUAL_HASTE_SNAPSHOT =
            snapshot("otherworld_ritual_haste");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<OwnerSnapshot>> SPIRIT_DILIGENCE_SNAPSHOT =
            snapshot("otherworld_spirit_diligence");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<OwnerSnapshot>> SPIRIT_BOUNTY_SNAPSHOT =
            snapshot("otherworld_spirit_bounty");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<OwnerSnapshot>> WORKER_ENDURANCE_SNAPSHOT =
            snapshot("otherworld_worker_endurance");

    private static final double EPSILON = 1.0E-6;
    private static final long WORKER_SNAPSHOT_INTERVAL = 100L;

    private OtherworldMagic() {}

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, AnimaReaping::onLivingDeath);
    }

    private static DeferredHolder<AttachmentType<?>, AttachmentType<OwnerSnapshot>> snapshot(String name) {
        return ATTACHMENTS.register(
                name,
                () -> AttachmentType.builder(() -> OwnerSnapshot.NONE)
                        .serialize(OwnerSnapshot.CODEC)
                        .build());
    }

    public static double ritualHaste(GoldenSacrificialBowlBlockEntity bowl) {
        return ownerValue(
                bowl.getLevel(), bowl.castingPlayerId, MagicRegistry.OCCULT_RITUAL_HASTE, bowl, RITUAL_HASTE_SNAPSHOT);
    }

    public static void snapshotSummoned(LivingEntity living, @Nullable Player caster) {
        if (!(living instanceof SpiritEntity spirit)
                || caster == null
                || spirit.level().isClientSide()) {
            return;
        }
        UUID ownerId = ownerId(spirit);
        if (ownerId == null || !ownerId.equals(caster.getUUID())) {
            return;
        }
        remember(
                spirit,
                SPIRIT_DILIGENCE_SNAPSHOT,
                ownerId,
                MagicRegistry.value(caster, MagicRegistry.OCCULT_SPIRIT_DILIGENCE));
        remember(
                spirit,
                SPIRIT_BOUNTY_SNAPSHOT,
                ownerId,
                MagicRegistry.value(caster, MagicRegistry.OCCULT_SPIRIT_BOUNTY));
    }

    public static int diligentTime(SpiritEntity spirit, int time) {
        if (time <= 1) {
            return time;
        }
        double diligence = ownerValue(
                spirit.level(),
                ownerId(spirit),
                MagicRegistry.OCCULT_SPIRIT_DILIGENCE,
                spirit,
                SPIRIT_DILIGENCE_SNAPSHOT);
        if (diligence <= 0.0) {
            return time;
        }
        return Math.max(1, (int) Math.round(time / (1.0 + diligence)));
    }

    public static int bountifulCount(SpiritEntity spirit, ItemStack result, int count) {
        if (count <= 0 || count >= result.getMaxStackSize()) {
            return count;
        }
        double bounty = ownerValue(
                spirit.level(), ownerId(spirit), MagicRegistry.OCCULT_SPIRIT_BOUNTY, spirit, SPIRIT_BOUNTY_SNAPSHOT);
        if (bounty <= 0.0 || spirit.getRandom().nextDouble() >= bounty) {
            return count;
        }
        return count + 1;
    }

    public static int bondedAmplifier(@Nullable LivingEntity owner, int amplifier) {
        if (amplifier < 0 || owner == null || owner.level().isClientSide()) {
            return amplifier;
        }
        int bond = (int) Math.floor(MagicRegistry.value(owner, MagicRegistry.OCCULT_FAMILIAR_BOND) + EPSILON);
        return bond <= 0 ? amplifier : amplifier + bond;
    }

    public static boolean refundsSouls(Player player) {
        double thrift = MagicRegistry.value(player, MagicRegistry.ANIMA_SOUL_THRIFT);
        return thrift > 0.0
                && !player.level().isClientSide()
                && player.getRandom().nextDouble() < thrift;
    }

    public static int hastenedCooldown(Player player, int cooldown) {
        if (cooldown <= 0) {
            return cooldown;
        }
        double haste = MagicRegistry.value(player, MagicRegistry.ANIMA_SPELL_HASTE);
        if (haste <= 0.0) {
            return cooldown;
        }
        return Math.max(1, (int) Math.round(cooldown * (1.0 - haste)));
    }

    public static int enduringDurability(MachineBlockEntity machine, @Nullable String owner, int durability) {
        double endurance = ownerValue(
                machine.getLevel(),
                parseOwner(owner),
                MagicRegistry.ANIMA_WORKER_ENDURANCE,
                machine,
                WORKER_ENDURANCE_SNAPSHOT);
        if (endurance <= 0.0) {
            return durability;
        }
        return Math.max(durability, (int) Math.round(durability * (1.0 + endurance)));
    }

    public static void refreshWorkerSnapshot(MachineBlockEntity machine, @Nullable String owner) {
        Level level = machine.getLevel();
        if (level == null || level.getGameTime() % WORKER_SNAPSHOT_INTERVAL != 0L) {
            return;
        }
        ownerValue(level, parseOwner(owner), MagicRegistry.ANIMA_WORKER_ENDURANCE, machine, WORKER_ENDURANCE_SNAPSHOT);
    }

    private static double ownerValue(
            @Nullable Level level,
            @Nullable UUID ownerId,
            Holder<Attribute> attribute,
            IAttachmentHolder holder,
            Supplier<AttachmentType<OwnerSnapshot>> type) {
        if (ownerId == null || !(level instanceof ServerLevel serverLevel)) {
            return 0.0;
        }
        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerId);
        if (owner != null) {
            double value = MagicRegistry.value(owner, attribute);
            remember(holder, type, ownerId, value);
            return value;
        }
        OwnerSnapshot snapshot = holder.getExistingDataOrNull(type);
        if (snapshot == null
                || CompanionConfig.RITUAL_OWNER_OFFLINE_FULL_COST.get()
                || !ownerId.equals(snapshot.owner())) {
            return 0.0;
        }
        return snapshot.value();
    }

    private static void remember(
            IAttachmentHolder holder, Supplier<AttachmentType<OwnerSnapshot>> type, UUID ownerId, double value) {
        OwnerSnapshot current = holder.getExistingDataOrNull(type);
        if (current == null ? value <= 0.0 : ownerId.equals(current.owner()) && current.value() == value) {
            return;
        }
        holder.setData(type, new OwnerSnapshot(ownerId, value));
    }

    private static @Nullable UUID ownerId(TamableAnimal animal) {
        EntityReference<LivingEntity> reference = animal.getOwnerReference();
        return reference == null ? null : reference.getUUID();
    }

    private static @Nullable UUID parseOwner(@Nullable String owner) {
        if (owner == null || owner.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(owner);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
