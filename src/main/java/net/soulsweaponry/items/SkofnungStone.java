package net.soulsweaponry.items;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.SoundRegistry;
import net.soulsweaponry.util.WeaponUtil;

import java.util.List;

public class SkofnungStone extends Item implements IConfigDisable {

    public SkofnungStone(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stoneStack = user.getStackInHand(hand);
        if (this.isDisabled(stoneStack)) {
            this.notifyDisabled(user);
            return TypedActionResult.fail(stoneStack);
        }
        boolean shouldDamage = false;
        for (Hand offHand : Hand.values()) {
            ItemStack swordStack = user.getStackInHand(offHand);
            if (swordStack.getItem() instanceof Skofnung && swordStack.hasNbt()) {
                swordStack.getNbt().putInt(Skofnung.EMPOWERED, ConfigConstructor.skofnung_stone_additional_empowered_strikes);
                shouldDamage = true;
                world.playSound(user, user.getBlockPos(), SoundRegistry.SHARPEN_EVENT, SoundCategory.PLAYERS, .5f, 1f);
                world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, .5f, .5f);
            }
        }
        for (StatusEffect effect : Registries.STATUS_EFFECT) {
            if (effect.getCategory().equals(StatusEffectCategory.HARMFUL) && user.hasStatusEffect(effect)) {
                user.removeStatusEffect(effect);
                world.playSound(user, user.getBlockPos(), SoundRegistry.RESTORE_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                shouldDamage = true;
            }
        }

        if (shouldDamage) {
            stoneStack.damage(1, user, e -> e.sendToolBreakStatus(hand));
            return TypedActionResult.success(user.getStackInHand(hand));
        } else {
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipType context) {
        if (this.isDisabled(stack)) {
            tooltip.add(Text.translatableWithFallback("tooltip.soulsweapons.disabled","Disabled"));
        }
        if (Screen.hasShiftDown()) {
            WeaponUtil.addAbilityTooltip(WeaponUtil.TooltipAbilities.DISABLE_DEBUFS, stack, tooltip);
        } else {
            tooltip.add(Text.translatable("tooltip.soulsweapons.shift"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_skofnung_stone;
    }
}