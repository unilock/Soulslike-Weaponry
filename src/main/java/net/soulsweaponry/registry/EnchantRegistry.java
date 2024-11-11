package net.soulsweaponry.registry;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.enchantments.StaggerEnchantmentEffect;
import net.soulsweaponry.util.ModTags;

public class EnchantRegistry {

    public static final RegistryKey<Enchantment> FAST_HANDS = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(SoulsWeaponry.ModId, "fast_hands"));
    public static final RegistryKey<Enchantment> STAGGER = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(SoulsWeaponry.ModId, "stagger"));
    public static final RegistryKey<Enchantment> VISCERAL = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(SoulsWeaponry.ModId, "visceral"));

    public static void init() {
    }

    public static void bootstrap(Registerable<Enchantment> registerable) {
        RegistryEntryLookup<Item> itemLookup = registerable.getRegistryLookup(RegistryKeys.ITEM);

        registerable.register(
                FAST_HANDS,
                Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ModTags.Items.GUN_ENCHANTABLE),
                                2,
                                3,
                                Enchantment.leveledCost(10, 10),
                                Enchantment.leveledCost(15, 10),
                                2,
                                AttributeModifierSlot.MAINHAND
                        )
                ).build(FAST_HANDS.getValue())
        );

        registerable.register(
                STAGGER,
                Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                5,
                                3,
                                Enchantment.leveledCost(10, 10),
                                Enchantment.leveledCost(15, 10),
                                2,
                                AttributeModifierSlot.MAINHAND
                        )
                ).addEffect(
                        EnchantmentEffectComponentTypes.POST_ATTACK,
                        EnchantmentEffectTarget.ATTACKER,
                        EnchantmentEffectTarget.VICTIM,
                        StaggerEnchantmentEffect.INSTANCE
                ).build(STAGGER.getValue())
        );

        registerable.register(
                VISCERAL,
                Enchantment.builder(
                        Enchantment.definition(
                                itemLookup.getOrThrow(ModTags.Items.GUN_ENCHANTABLE),
                                5,
                                3,
                                Enchantment.leveledCost(10, 10),
                                Enchantment.leveledCost(15, 10),
                                2,
                                AttributeModifierSlot.MAINHAND
                        )
                ).build(VISCERAL.getValue())
        );
    }
}
