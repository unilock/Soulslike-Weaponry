package net.soulsweaponry.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.soulsweaponry.SoulsWeaponry;

@SuppressWarnings("unused")
public class ModTags {

    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SoulsWeaponry.ModId, name));
        }

        private static TagKey<Block> createCommonTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", name));
        }
    }

    public static class Items {
        public static final TagKey<Item> LORD_SOUL = createTag("lord_soul");
        public static final TagKey<Item> DEMON_HEARTS = createCommonTag("demon_hearts");
        public static final TagKey<Item> MOONLIGHT_SWORD = createTag("moonlight_sword");

        public static final TagKey<Item> STICKS = createCommonTag("wood_sticks");
        public static final TagKey<Item> SILVER_INGOTS = createCommonTag("silver_ingots");
        public static final TagKey<Item> IRON_INGOTS = createCommonTag("iron_ingots");
        public static final TagKey<Item> SHIELDS = createCommonTag("shields");

        public static final TagKey<Item> GUN_ENCHANTABLE = createTag("enchantable/gun");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(SoulsWeaponry.ModId, name));
        }

        private static TagKey<Item> createCommonTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of("c", name));
        }
    }

    public static class Structures {
        public static final TagKey<Structure> DECAYING_KINGDOM = createTag("decaying_kingdom");
        public static final TagKey<Structure> CHAMPIONS_GRAVES = createTag("champions_graves");

        private static TagKey<Structure> createTag(String id) {
            return TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(SoulsWeaponry.ModId, id));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> SKELETONS = createCommonTag("skeletons");
        public static final TagKey<EntityType<?>> RANGED_MOBS = createCommonTag("ranged_mobs");
        public static final TagKey<EntityType<?>> BOSSES = createCommonTag("bosses");

        private static TagKey<EntityType<?>> createCommonTag(String id) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", id));
        }
    }

    public static class Effects {
        public static final TagKey<StatusEffect> DAMAGE_OVER_TIME = createCommonTag("damage_over_time");

        private static TagKey<StatusEffect> createCommonTag(String id) {
            return TagKey.of(RegistryKeys.STATUS_EFFECT, Identifier.of("c", id));
        }
    }
}
