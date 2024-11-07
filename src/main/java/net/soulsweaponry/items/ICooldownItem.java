package net.soulsweaponry.items;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.util.WeaponUtil;

public interface ICooldownItem {

    default void applyItemCooldown(PlayerEntity player, int cooldown) {
        if (!player.isCreative()) {
            player.getItemCooldownManager().set((Item) this, cooldown); //NOTE: Will crash if the class is not an item
        }
    }

    default void applyEffectCooldown(PlayerEntity player, int cooldown) {
        if (!player.isCreative()) {
            player.addStatusEffect(new StatusEffectInstance(EffectRegistry.COOLDOWN, cooldown, 0));
        }
    }

    default int getReduceCooldownEnchantLevel(ItemStack stack) {
        if (this.canEnchantReduceCooldown(stack)) {
            String string = this.getReduceCooldownEnchantId(stack);
            return this.getReducedCooldownWithoutCheck(stack, string);
        }
        return 0;
    }

    default int getReduceLifeStealCooldownEnchantLevel(ItemStack stack) {
        if (ConfigConstructor.lifesteal_item_enchant_reduces_cooldown) {
            String string = ConfigConstructor.lifesteal_item_enchant_reduces_cooldown_id;
            return this.getReducedCooldownWithoutCheck(stack, string);
        }
        return 0;
    }

    default int getReducedCooldownWithoutCheck(ItemStack stack, String enchantId) {
        if (enchantId.equals("damage")) {
            return WeaponUtil.getEnchantDamageBonus(stack);
        } else {
            Identifier id = new Identifier(enchantId);
            Enchantment enchantment = Registries.ENCHANTMENT.get(id);
            if (enchantment != null) {
                return EnchantmentHelper.getLevel(enchantment, stack);
            }
        }
        return 0;
    }

    boolean canEnchantReduceCooldown(ItemStack stack);
    String getReduceCooldownEnchantId(ItemStack stack);
}
