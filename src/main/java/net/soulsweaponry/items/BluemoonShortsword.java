package net.soulsweaponry.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.soulsweaponry.config.ConfigConstructor;

public class BluemoonShortsword extends MoonlightShortsword {
    public BluemoonShortsword(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, ConfigConstructor.bluemoon_shortsword_damage, ConfigConstructor.bluemoon_shortsword_attack_speed, settings);
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_bluemoon_shortsword;
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_bluemoon_shortsword;
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ConfigConstructor.bluemoon_shortsword_enchant_reduces_cooldown;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return ConfigConstructor.bluemoon_shortsword_enchant_reduces_cooldown_id;
    }
}