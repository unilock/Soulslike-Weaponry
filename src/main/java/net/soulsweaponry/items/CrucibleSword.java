package net.soulsweaponry.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.util.WeaponUtil;

public class CrucibleSword extends ModdedSword {

    private static final String EMP = "empowered";

    public CrucibleSword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, ConfigConstructor.crucible_sword_normal_damage, ConfigConstructor.crucible_sword_attack_speed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.DOOM);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && !this.isDisabled(stack)) {
            if (!player.getItemCooldownManager().isCoolingDown(this)) {
                this.applyItemCooldown(player, Math.max(ConfigConstructor.crucible_sword_empowered_min_cooldown,
                        ConfigConstructor.crucible_sword_empowered_cooldown - this.getReduceCooldownEnchantLevel(stack) * 20));
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity instanceof PlayerEntity player) {
            this.updateEmpowered(stack, !player.getItemCooldownManager().isCoolingDown(this));
        }
    }

    private void updateEmpowered(ItemStack stack, boolean bl) {
        if (stack.hasNbt()) {
            stack.getNbt().putBoolean(EMP, bl);
        }
    }

    private boolean isEmpowered(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(EMP)) {
            return stack.getNbt().getBoolean(EMP);
        } else {
            return false;
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (this.isEmpowered(stack) && !this.isDisabled(stack) ? ConfigConstructor.crucible_sword_empowered_damage : ConfigConstructor.crucible_sword_normal_damage) - 1, EntityAttributeModifier.Operation.ADD_VALUE));
            builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.getAttackSpeed(), EntityAttributeModifier.Operation.ADD_VALUE));
            attributeModifiers = builder.build();
            return attributeModifiers;
        } else {
            return super.getAttributeModifiers(stack, slot);
        }
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_crucible_sword;
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_crucible_sword;
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ConfigConstructor.crucible_sword_enchant_reduces_cooldown;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return ConfigConstructor.crucible_sword_enchant_reduces_cooldown_id;
    }
}