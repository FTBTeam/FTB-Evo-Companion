package dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy;

import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.soul.SyphonResult;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class DiscountedAnima implements IAnima {
    private final IAnima delegate;
    private final double discount;
    private final RandomSource random;

    public DiscountedAnima(IAnima delegate, double discount, RandomSource random) {
        this.delegate = delegate;
        this.discount = discount;
        this.random = random;
    }

    @Override
    public UUID getPlayerId() {
        return delegate.getPlayerId();
    }

    @Override
    public int getCurrentEV() {
        return delegate.getCurrentEV();
    }

    @Override
    public int add(AnimaTicket ticket, int maximum) {
        return delegate.add(ticket, maximum);
    }

    @Override
    public int set(AnimaTicket ticket, int maximum) {
        return delegate.set(ticket, maximum);
    }

    @Override
    public int syphon(AnimaTicket ticket) {
        int requested = ticket.getAmount();
        if (requested <= 0) {
            return delegate.syphon(ticket);
        }
        int reduced = reduce(requested);
        int drained = delegate.syphon(AnimaTicket.create(reduced));
        return drained >= reduced ? requested : drained;
    }

    @Override
    public boolean hurtPlayer(Player user, float syphon) {
        return delegate.hurtPlayer(user, syphon);
    }

    @Override
    public SyphonResult syphonAndDamage(Player user, AnimaTicket ticket) {
        int requested = ticket.getAmount();
        if (requested <= 0) {
            return delegate.syphonAndDamage(user, ticket);
        }
        int reduced = reduce(requested);
        SyphonResult result = delegate.syphonAndDamage(user, AnimaTicket.create(reduced));
        return result.success() && result.amount() >= reduced ? SyphonResult.of(true, requested) : result;
    }

    private int reduce(int requested) {
        return Math.min(requested, Math.max(0, HemomancyHooks.roundRandomly(requested * (1.0 - discount), random)));
    }
}
