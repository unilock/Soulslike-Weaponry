package net.soulsweaponry.items.armor;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.ArmorRegistry;
import net.soulsweaponry.registry.EffectRegistry;

public class SoulRobesArmor extends SetBonusArmor {

    public SoulRobesArmor(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    protected void tickAdditionalSetEffects(ItemStack stack, PlayerEntity player) {}

    @Override
    protected Item getMatchingBoots() {
        return ArmorRegistry.SOUL_ROBES_BOOTS;
    }

    @Override
    protected Item getMatchingLegs() {
        return ArmorRegistry.SOUL_ROBES_LEGGINGS;
    }

    @Override
    protected Item getMatchingChest() {
        return ArmorRegistry.SOUL_ROBES_CHESTPLATE;
    }

    @Override
    protected Item getMatchingHead() {
        return ArmorRegistry.SOUL_ROBES_HELMET;
    }

    @Override
    protected StatusEffectInstance[] getFullSetEffects() {
        return new StatusEffectInstance[] {
                new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0),
                new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 40, 1)
        };
    }

    @Override
    protected Text[] getCustomTooltips() {
        return new Text[0];
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_soul_robes_armor;
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_soul_robes_set;
    }
}