package dev.ftb.mods.ftbevolutioncompanion.metals;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class AiotItem extends Item {
    private static final List<ItemAbility> USE_ORDER = List.of(ItemAbilities.AXE_STRIP, ItemAbilities.AXE_SCRAPE,
            ItemAbilities.AXE_WAX_OFF, ItemAbilities.SHOVEL_FLATTEN, ItemAbilities.SHOVEL_DOUSE);
    private static final List<ItemAbility> SNEAK_ORDER = List.of(ItemAbilities.HOE_TILL);

    public AiotItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        List<ItemAbility> order = player != null && player.isSecondaryUseActive() ? SNEAK_ORDER : USE_ORDER;
        for (ItemAbility ability : order) {
            BlockState modified = state.getToolModifiedState(context, ability, false);
            if (modified == null || modified == state) {
                continue;
            }
            level.playSound(player, pos, sound(ability), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                level.setBlock(pos, modified, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modified));
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, context.getHand());
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static SoundEvent sound(ItemAbility ability) {
        if (ability == ItemAbilities.AXE_STRIP) {
            return SoundEvents.AXE_STRIP;
        }
        if (ability == ItemAbilities.AXE_SCRAPE) {
            return SoundEvents.AXE_SCRAPE;
        }
        if (ability == ItemAbilities.AXE_WAX_OFF) {
            return SoundEvents.AXE_WAX_OFF;
        }
        if (ability == ItemAbilities.SHOVEL_DOUSE) {
            return SoundEvents.GENERIC_EXTINGUISH_FIRE;
        }
        if (ability == ItemAbilities.HOE_TILL) {
            return SoundEvents.HOE_TILL;
        }
        return SoundEvents.SHOVEL_FLATTEN;
    }
}
