package dev.ftb.mods.ftbevolutioncompanion.compat.sgeconomy;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class ShopCoins {
    private static final Method DEPOSIT = findDeposit();
    private static final Supplier<?> CURRENCY_ATTACHMENT = findAttachment();

    private ShopCoins() {}

    public static boolean deposit(ServerPlayer player, int amount) {
        if (DEPOSIT == null) {
            return false;
        }
        try {
            boolean ok = (Boolean) DEPOSIT.invoke(null, player, (double) amount);
            sync(player);
            return ok;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static void sync(ServerPlayer player) throws ReflectiveOperationException {
        if (CURRENCY_ATTACHMENT == null) {
            return;
        }
        Object data = player.getData((net.neoforged.neoforge.attachment.AttachmentType<?>) CURRENCY_ATTACHMENT.get());
        data.getClass().getMethod("syncToClient", ServerPlayer.class).invoke(data, player);
    }

    private static Method findDeposit() {
        try {
            return Class.forName("net.sirgrantd.sg_economy.api.SGEconomyApi")
                    .getMethod("depositBalance", Entity.class, double.class);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static Supplier<?> findAttachment() {
        try {
            return (Supplier<?>) Class.forName("net.sirgrantd.sg_economy.SGEconomyMod")
                    .getField("CURRENCY_PLAYER")
                    .get(null);
        } catch (ReflectiveOperationException | ClassCastException e) {
            return null;
        }
    }
}
