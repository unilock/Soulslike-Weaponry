package net.soulsweaponry.registry;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.armor.ForlornArmor;
import net.soulsweaponry.items.armor.SoulIngotArmor;
import net.soulsweaponry.items.armor.SoulRobesArmor;
import net.soulsweaponry.items.material.ModArmorMaterials;

public class ArmorRegistry {

    public static final Item SOUL_INGOT_HELMET = new SoulIngotArmor(ModArmorMaterials.SOUL_INGOT, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item SOUL_INGOT_CHESTPLATE = new SoulIngotArmor(ModArmorMaterials.SOUL_INGOT, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item SOUL_INGOT_LEGGINGS = new SoulIngotArmor(ModArmorMaterials.SOUL_INGOT, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item SOUL_INGOT_BOOTS = new SoulIngotArmor(ModArmorMaterials.SOUL_INGOT, ArmorItem.Type.BOOTS, new Item.Settings());
    public static final Item SOUL_ROBES_HELMET = new SoulRobesArmor(ModArmorMaterials.SOUL_ROBES, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item SOUL_ROBES_CHESTPLATE = new SoulRobesArmor(ModArmorMaterials.SOUL_ROBES, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item SOUL_ROBES_LEGGINGS = new SoulRobesArmor(ModArmorMaterials.SOUL_ROBES, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item SOUL_ROBES_BOOTS = new SoulRobesArmor(ModArmorMaterials.SOUL_ROBES, ArmorItem.Type.BOOTS, new Item.Settings());
    public static final Item FORLORN_HELMET = new ForlornArmor(ModArmorMaterials.FORLORN_ARMOR, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item FORLORN_CHESTPLATE = new ForlornArmor(ModArmorMaterials.FORLORN_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item FORLORN_LEGGINGS = new ForlornArmor(ModArmorMaterials.FORLORN_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item FORLORN_BOOTS = new ForlornArmor(ModArmorMaterials.FORLORN_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings());

    public static void init() {
        ItemRegistry.registerArmorItem(SOUL_INGOT_HELMET, "soul_ingot_helmet", ConfigConstructor.disable_recipe_soul_ingot_helmet);
        ItemRegistry.registerArmorItem(SOUL_INGOT_CHESTPLATE, "soul_ingot_chestplate", ConfigConstructor.disable_recipe_soul_ingot_chestplate);
        ItemRegistry.registerArmorItem(SOUL_INGOT_LEGGINGS, "soul_ingot_leggings", ConfigConstructor.disable_recipe_soul_ingot_leggings);
        ItemRegistry.registerArmorItem(SOUL_INGOT_BOOTS, "soul_ingot_boots", ConfigConstructor.disable_recipe_soul_ingot_boots);
        ItemRegistry.registerArmorItem(SOUL_ROBES_HELMET, "soul_robes_helmet", ConfigConstructor.disable_recipe_soul_robes_helmet);
        ItemRegistry.registerArmorItem(SOUL_ROBES_CHESTPLATE, "soul_robes_chestplate", ConfigConstructor.disable_recipe_soul_robes_chestplate);
        ItemRegistry.registerArmorItem(SOUL_ROBES_LEGGINGS, "soul_robes_leggings", ConfigConstructor.disable_recipe_soul_robes_leggings);
        ItemRegistry.registerArmorItem(SOUL_ROBES_BOOTS, "soul_robes_boots", ConfigConstructor.disable_recipe_soul_robes_boots);
        ItemRegistry.registerArmorItem(FORLORN_HELMET, "forlorn_helmet", ConfigConstructor.disable_recipe_forlorn_helmet);
        ItemRegistry.registerArmorItem(FORLORN_CHESTPLATE, "forlorn_chestplate", ConfigConstructor.disable_recipe_forlorn_chestplate);
        ItemRegistry.registerArmorItem(FORLORN_LEGGINGS, "forlorn_leggings", ConfigConstructor.disable_recipe_forlorn_leggings);
        ItemRegistry.registerArmorItem(FORLORN_BOOTS, "forlorn_boots", ConfigConstructor.disable_recipe_forlorn_boots);
    }
}
