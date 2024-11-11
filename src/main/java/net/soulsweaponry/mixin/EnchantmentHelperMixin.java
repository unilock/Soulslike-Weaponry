package net.soulsweaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.TrickWeapon;
import net.soulsweaponry.registry.WeaponRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getDamage", at = @At("RETURN"))
    private static float interceptGetDamage(float original, ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseDamage) {
        if (stack.isOf(WeaponRegistry.STING) && target.getType().isIn(EntityTypeTags.ARTHROPOD)) {
            return original + ConfigConstructor.sting_bonus_arthropod_damage;
        }
        if (target.getType().isIn(EntityTypeTags.UNDEAD) && (stack.getItem() instanceof TrickWeapon trickWeapon && trickWeapon.hasUndeadBonus() && !trickWeapon.isDisabled(stack) || stack.isOf(WeaponRegistry.MASTER_SWORD))) {
            return original + ConfigConstructor.righteous_undead_bonus_damage + (float) EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FIRE_ASPECT).orElseThrow(), stack);
        }
        return original;
    }
}
