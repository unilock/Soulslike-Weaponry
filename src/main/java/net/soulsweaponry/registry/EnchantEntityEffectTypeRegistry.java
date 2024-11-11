package net.soulsweaponry.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.enchantments.StaggerEnchantmentEffect;

public class EnchantEntityEffectTypeRegistry {
	public static void init() {
		Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(SoulsWeaponry.ModId, "stagger"), StaggerEnchantmentEffect.CODEC);
	}
}
