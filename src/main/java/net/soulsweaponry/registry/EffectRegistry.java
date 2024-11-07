package net.soulsweaponry.registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.entity.effect.Decay;
import net.soulsweaponry.entity.effect.Fear;
import net.soulsweaponry.entity.effect.Freezing;
import net.soulsweaponry.entity.effect.HallowedDragonMist;
import net.soulsweaponry.entity.effect.LifeLeach;
import net.soulsweaponry.entity.effect.PostureBreak;
import net.soulsweaponry.entity.effect.VeilOfFire;
import net.soulsweaponry.util.CustomDamageSource;
import net.soulsweaponry.util.ModTags;

public class EffectRegistry {

    public static final StatusEffect HALLOWED_DRAGON_MIST = registerEffect(new HallowedDragonMist(), "hallowed_dragon_mist");
    public static final StatusEffect BLOODTHIRSTY = registerEffect(new DefaultStatusEffect(StatusEffectCategory.NEUTRAL, 0x630109), "bloodthirsty");
    public static final StatusEffect POSTURE_BREAK = registerEffect(new PostureBreak(), "posture_break");
    public static final StatusEffect LIFE_LEACH = registerEffect(new LifeLeach(), "life_leach");
    public static final StatusEffect RETRIBUTION = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xc76700), "retribution");
    public static final StatusEffect FEAR = registerEffect(new Fear(), "fear");
    public static final StatusEffect DECAY = registerEffect(new Decay(), "decay");
    public static final StatusEffect MAGIC_RESISTANCE = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x80ffff), "magic_resistance");
    public static final StatusEffect MOON_HERALD = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x03e8fc), "moon_herald");
    public static final StatusEffect FREEZING = registerEffect(new Freezing(), "freezing");
    public static final StatusEffect DISABLE_HEAL = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xfc9d9d), "disable_heal");
    public static final StatusEffect BLEED = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xba0c00), "bleed");
    public static final StatusEffect CALCULATED_FALL = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0xffffff), "calculated_fall");
    public static final StatusEffect VEIL_OF_FIRE = registerEffect(new VeilOfFire(), "veil_of_fire");
    public static final StatusEffect BLIGHT = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0x73013c), "blight");
    public static final StatusEffect SHADOW_STEP = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x020e78), "shadow_step").addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "48403ce1-d9b3-4757-b1ef-9fbacff0ed37", 0.30000000298023224, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    public static final StatusEffect COOLDOWN = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0x525252), "cooldown");
    public static final StatusEffect GHOSTLY = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x5e9191), "ghostly");

    public static final Potion WARDING = registerPotion(new Potion(new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 4000)), "warding");
    public static final Potion STRONG_WARDING = registerPotion(new Potion("warding", new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 2000, 1)), "strong_warding");
    public static final Potion LONG_WARDING = registerPotion(new Potion("warding", new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 8000)), "long_warding");
    public static final Potion TAINTED_AMBROSIA = registerPotion(new Potion(new StatusEffectInstance(EffectRegistry.DISABLE_HEAL, 600, 0)), "tainted_ambrosia");

    public static void init() {
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, BlockRegistry.HYDRANGEA.asItem(), WARDING);
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, BlockRegistry.OLEANDER.asItem(), TAINTED_AMBROSIA);
        BrewingRecipeRegistry.registerPotionRecipe(WARDING, Items.GLOWSTONE_DUST, STRONG_WARDING);
        BrewingRecipeRegistry.registerPotionRecipe(WARDING, Items.REDSTONE, LONG_WARDING);
    }

    public static <I extends StatusEffect> I registerEffect(I effect, String name) {
		return Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulsWeaponry.ModId, name), effect);
	}

    private static Potion registerPotion(Potion potion, String name) {
        return Registry.register(Registries.POTION, name, potion);
    }

    static class DefaultStatusEffect extends StatusEffect {

        public DefaultStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
            super(statusEffectCategory, color);
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            if (this == EffectRegistry.BLEED) {
                int i = 15 >> amplifier;
                if (i > 0) {
                    return duration % i == 0;
                }
                return true;
            }
            if (this == EffectRegistry.BLOODTHIRSTY) {
                int k = 40 >> amplifier;
                if (k > 0) {
                    return duration % k == 0;
                } else {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (this == EffectRegistry.BLEED && !entity.getType().isIn(EntityTypeTags.SKELETONS) && !entity.getType().isIn(ModTags.Entities.SKELETONS)) {
                entity.damage(CustomDamageSource.create(entity.getWorld(), CustomDamageSource.BLEED), 1f + amplifier);
            }
            if (this == EffectRegistry.BLOODTHIRSTY) {
                entity.damage(entity.getWorld().getDamageSources().wither(), 1f);
            }
        }
    }
}
