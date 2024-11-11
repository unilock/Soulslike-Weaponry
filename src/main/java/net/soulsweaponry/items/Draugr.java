package net.soulsweaponry.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.util.WeaponUtil;

public class Draugr extends ModdedSword {

    public static final String NIGHT = "night";

    public Draugr(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, 1, ConfigConstructor.draugr_attack_speed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.NIGHT_PROWLER);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        this.refreshDayTime(world, stack);
    }

    private void refreshDayTime(World world, ItemStack stack) {
        if (stack.hasNbt() && !world.isClient) {
            stack.getNbt().putBoolean(NIGHT, world.getDimension().hasSkyLight() && world.isNight());
        }
    }

    private boolean isNight(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(NIGHT)) {
            return stack.getNbt().getBoolean(NIGHT);
        } else {
            return false;
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
        if (slot == EquipmentSlot.MAINHAND) {
            Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.isNight(stack) && !this.isDisabled(stack) ? ConfigConstructor.draugr_damage_at_night - 1 : 0, EntityAttributeModifier.Operation.ADD_VALUE));
            builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.getAttackSpeed(), EntityAttributeModifier.Operation.ADD_VALUE));
            attributeModifiers = builder.build();
            return attributeModifiers;
        } else {
            return super.getAttributeModifiers(slot);
        }
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_draugr;
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_draugr;
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return false;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return null;
    }
}