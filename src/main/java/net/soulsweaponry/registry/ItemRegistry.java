package net.soulsweaponry.registry;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.BossCompass;
import net.soulsweaponry.items.ChaosOrb;
import net.soulsweaponry.items.LoreItem;
import net.soulsweaponry.items.MoonstoneRing;
import net.soulsweaponry.items.SkofnungStone;
import net.soulsweaponry.items.armor.ChaosSet;
import net.soulsweaponry.items.armor.WitheredArmor;
import net.soulsweaponry.items.material.ModArmorMaterials;
import net.soulsweaponry.items.material.ModToolMaterials;
import net.soulsweaponry.util.RecipeHandler;

public class ItemRegistry {

    public static final LoreItem LORD_SOUL_RED = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_red", 4);
    public static final LoreItem LORD_SOUL_DARK = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_dark", 3);
    public static final LoreItem LORD_SOUL_VOID = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_void", 3);
    public static final LoreItem LORD_SOUL_ROSE = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_rose", 3);
    public static final LoreItem LORD_SOUL_PURPLE = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_purple", 3);
    public static final LoreItem LORD_SOUL_WHITE = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_white", 3);
    public static final LoreItem LORD_SOUL_DAY_STALKER = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_day_stalker", 2);
    public static final LoreItem LORD_SOUL_NIGHT_PROWLER = new LoreItem(new Item.Settings().rarity(Rarity.EPIC).fireproof(), "lord_soul_night_prowler", 3);
    public static final Item LOST_SOUL = new LoreItem(new Item.Settings().rarity(Rarity.RARE), "lost_soul", 3);
    public static final Item MOONSTONE = new Item(new Item.Settings());
    public static final Item CHUNGUS_EMERALD = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final Item DEMON_HEART = new LoreItem(new Item.Settings().food(new FoodComponent.Builder()
            .nutrition(4).saturationModifier(6f).meat().alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 150, 0), 1)
            .statusEffect(new StatusEffectInstance(EffectRegistry.BLOODTHIRSTY, 150, 0), 10)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 0), 1).build()),
            "demon_heart", 3);
    public static final Item MOLTEN_DEMON_HEART= new Item(new Item.Settings());
    public static final Item DEMON_CHUNK = new Item(new Item.Settings());
    public static final Item CRIMSON_INGOT = new Item(new Item.Settings());
    public static final Item SOUL_INGOT = new Item(new Item.Settings());
    public static final Item SILVER_BULLET = new Item(new Item.Settings().maxCount(20));
    public static final Item BOSS_COMPASS = new BossCompass(new Item.Settings().rarity(Rarity.RARE));
    public static final Item MOONSTONE_RING = new MoonstoneRing(new Item.Settings().rarity(Rarity.EPIC).maxDamage(25));
    public static final Item SHARD_OF_UNCERTAINTY = new LoreItem(new Item.Settings().rarity(Rarity.RARE).fireproof(), "shard_of_uncertainty", 1);
    public static final Item VERGLAS = new Item(new Item.Settings());
    public static final Item SKOFNUNG_STONE = new SkofnungStone(new Item.Settings().maxDamage(20));
    public static final Item IRON_SKULL = new Item(new Item.Settings());

    public static final Item MOONSTONE_SHOVEL = new ShovelItem(ModToolMaterials.MOONSTONE_TOOL, 1.5f, -3.0f, new Item.Settings());
    public static final Item MOONSTONE_PICKAXE = new PickaxeItem(ModToolMaterials.MOONSTONE_TOOL, 1, -2.8f, new Item.Settings());
    public static final Item MOONSTONE_AXE = new AxeItem(ModToolMaterials.MOONSTONE_TOOL, 5.0f, -3.0f, new Item.Settings());
    public static final Item MOONSTONE_HOE = new HoeItem(ModToolMaterials.MOONSTONE_TOOL, -3, 0.0f, new Item.Settings());

    public static final LoreItem WITHERED_DEMON_HEART = new LoreItem(new Item.Settings().rarity(Rarity.RARE).fireproof(), "withered_demon_heart", 4);
    public static final LoreItem ARKENSTONE = new LoreItem(new Item.Settings().rarity(Rarity.RARE).fireproof(), "arkenstone", 4);
    public static final LoreItem ESSENCE_OF_EVENTIDE = new LoreItem(new Item.Settings().rarity(Rarity.RARE).fireproof(), "essence_of_eventide", 2);
    public static final LoreItem ESSENCE_OF_LUMINESCENCE = new LoreItem(new Item.Settings().rarity(Rarity.RARE).fireproof(), "essence_of_luminescence", 3);
    public static final Item CHAOS_CROWN = new ChaosSet(ModArmorMaterials.CHAOS_SET, ArmorItem.Type.HELMET, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item CHAOS_HELMET = new ChaosSet(ModArmorMaterials.CHAOS_ARMOR, ArmorItem.Type.HELMET, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item ARKENPLATE = new ChaosSet(ModArmorMaterials.CHAOS_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item ENHANCED_ARKENPLATE = new ChaosSet(ModArmorMaterials.CHAOS_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item WITHERED_CHEST = new WitheredArmor(ModArmorMaterials.CHAOS_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item ENHANCED_WITHERED_CHEST = new WitheredArmor(ModArmorMaterials.WITHERED_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item CHAOS_ROBES = new ChaosSet(ModArmorMaterials.CHAOS_SET, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.EPIC));
    public static final Item CHAOS_ORB = new ChaosOrb(new Item.Settings().rarity(Rarity.EPIC).fireproof());

    public static final Item CHUNGUS_DISC = new MusicDiscItem(7, SoundRegistry.BIG_CHUNGUS_SONG_EVENT, new Item.Settings().maxCount(1), 112);

    public static void init() {
        registerLoreItem(LORD_SOUL_RED);
        registerLoreItem(LORD_SOUL_DARK);
        registerLoreItem(LORD_SOUL_VOID);
        registerLoreItem(LORD_SOUL_ROSE);
        registerLoreItem(LORD_SOUL_PURPLE);
        registerLoreItem(LORD_SOUL_WHITE);
        registerLoreItem(LORD_SOUL_DAY_STALKER);
        registerLoreItem(LORD_SOUL_NIGHT_PROWLER);
        registerItem(LOST_SOUL, "lost_soul");
        registerItem(MOONSTONE, "moonstone");
        registerItem(CHUNGUS_EMERALD, "chungus_emerald");
        registerItem(DEMON_HEART, "demon_heart");
        registerItem(MOLTEN_DEMON_HEART, "molten_demon_heart");
        registerItem(DEMON_CHUNK, "demon_chunk");
        registerItem(CRIMSON_INGOT, "crimson_ingot");
        registerItem(SOUL_INGOT, "soul_ingot");
        registerGunItem(SILVER_BULLET, "silver_bullet");
        registerItem(BOSS_COMPASS, "boss_compass");
        registerItem(MOONSTONE_RING, "moonstone_ring");
        registerItem(SHARD_OF_UNCERTAINTY, "shard_of_uncertainty");
        registerItem(VERGLAS, "verglas");
        registerItem(SKOFNUNG_STONE, "skofnung_stone");
        registerItem(IRON_SKULL, "iron_skull");

        registerItem(MOONSTONE_SHOVEL, "moonstone_shovel");
        registerItem(MOONSTONE_PICKAXE, "moonstone_pickaxe");
        registerItem(MOONSTONE_AXE, "moonstone_axe");
        registerItem(MOONSTONE_HOE, "moonstone_hoe");

        registerLoreItem(WITHERED_DEMON_HEART);
        registerLoreItem(ARKENSTONE);
        registerLoreItem(ESSENCE_OF_EVENTIDE);
        registerLoreItem(ESSENCE_OF_LUMINESCENCE);
        registerItem(CHAOS_CROWN, "chaos_crown");
        registerArmorItem(CHAOS_HELMET, "chaos_helmet", ConfigConstructor.disable_recipe_chaos_helmet);
        registerArmorItem(ARKENPLATE, "arkenplate", ConfigConstructor.disable_recipe_arkenplate);
        registerArmorItem(ENHANCED_ARKENPLATE, "enhanced_arkenplate", ConfigConstructor.disable_recipe_enhanced_arkenplate);
        registerArmorItem(WITHERED_CHEST, "withered_chest", ConfigConstructor.disable_recipe_withered_chest);
        registerArmorItem(ENHANCED_WITHERED_CHEST, "enhanced_withered_chest", ConfigConstructor.disable_recipe_enhanced_withered_chest);
        registerItem(CHAOS_ROBES, "chaos_robes");
        registerItem(CHAOS_ORB, "chaos_orb");

        registerItem(CHUNGUS_DISC, "chungus_disc");
    }

    public static <I extends Item> I registerItem(I item, String name) {
        SoulsWeaponry.ITEM_GROUP_LIST.add(item);
        return Registry.register(Registries.ITEM, Identifier.of(SoulsWeaponry.ModId, name), item);
    }

    public static <I extends Item> I registerItemRemovableRecipe(I item, String name, boolean removeRecipe) {
        RecipeHandler.RECIPE_IDS.put(Identifier.of(SoulsWeaponry.ModId, name), removeRecipe);
        return registerItem(item, name);
    }

    public static <I extends LoreItem> I registerLoreItem(I item) {
        return registerItem(item, item.getIdName());
	}

    public static <I extends Item> I registerArmorItem(I item, String name, boolean removeRecipe) {
        if (ConfigConstructor.disable_armor_recipes) {
            return registerItemRemovableRecipe(item, name, true);
        } else {
            return registerItemRemovableRecipe(item, name, removeRecipe);
        }
    }

    public static <I extends Item> I registerWeaponItem(I item, String name, boolean removeRecipe) {
        if (ConfigConstructor.disable_weapon_recipes) {
            return registerItemRemovableRecipe(item, name, true);
        } else {
            return registerItemRemovableRecipe(item, name, removeRecipe);
        }
    }

    public static <I extends Item> I registerGunItem(I item, String name) {
        return registerItemRemovableRecipe(item, name, ConfigConstructor.disable_gun_recipes);
    }
}
