package net.soulsweaponry.registry;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
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

    public static final RegistryEntry.Reference<StatusEffect> HALLOWED_DRAGON_MIST = registerEffect(new HallowedDragonMist(), "hallowed_dragon_mist");
    public static final RegistryEntry.Reference<StatusEffect> BLOODTHIRSTY = registerEffect(new DefaultStatusEffect(StatusEffectCategory.NEUTRAL, 0x630109), "bloodthirsty");
    public static final RegistryEntry.Reference<StatusEffect> POSTURE_BREAK = registerEffect(new PostureBreak(), "posture_break");
    public static final RegistryEntry.Reference<StatusEffect> LIFE_LEACH = registerEffect(new LifeLeach(), "life_leach");
    public static final RegistryEntry.Reference<StatusEffect> RETRIBUTION = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xc76700), "retribution");
    public static final RegistryEntry.Reference<StatusEffect> FEAR = registerEffect(new Fear(), "fear");
    public static final RegistryEntry.Reference<StatusEffect> DECAY = registerEffect(new Decay(), "decay");
    public static final RegistryEntry.Reference<StatusEffect> MAGIC_RESISTANCE = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x80ffff), "magic_resistance");
    public static final RegistryEntry.Reference<StatusEffect> MOON_HERALD = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x03e8fc), "moon_herald");
    public static final RegistryEntry.Reference<StatusEffect> FREEZING = registerEffect(new Freezing(), "freezing");
    public static final RegistryEntry.Reference<StatusEffect> DISABLE_HEAL = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xfc9d9d), "disable_heal");
    public static final RegistryEntry.Reference<StatusEffect> BLEED = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0xba0c00), "bleed");
    public static final RegistryEntry.Reference<StatusEffect> CALCULATED_FALL = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0xffffff), "calculated_fall");
    public static final RegistryEntry.Reference<StatusEffect> VEIL_OF_FIRE = registerEffect(new VeilOfFire(), "veil_of_fire");
    public static final RegistryEntry.Reference<StatusEffect> BLIGHT = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0x73013c), "blight");
    public static final RegistryEntry.Reference<StatusEffect> SHADOW_STEP = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x020e78).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(SoulsWeaponry.ModId, "shadow_step"), 0.30000000298023224, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), "shadow_step");
    public static final RegistryEntry.Reference<StatusEffect> COOLDOWN = registerEffect(new DefaultStatusEffect(StatusEffectCategory.HARMFUL, 0x525252), "cooldown");
    public static final RegistryEntry.Reference<StatusEffect> GHOSTLY = registerEffect(new DefaultStatusEffect(StatusEffectCategory.BENEFICIAL, 0x5e9191), "ghostly");

    public static final RegistryEntry.Reference<Potion> WARDING = registerPotion(new Potion(new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 4000)), "warding");
    public static final RegistryEntry.Reference<Potion> STRONG_WARDING = registerPotion(new Potion("warding", new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 2000, 1)), "strong_warding");
    public static final RegistryEntry.Reference<Potion> LONG_WARDING = registerPotion(new Potion("warding", new StatusEffectInstance(EffectRegistry.MAGIC_RESISTANCE, 8000)), "long_warding");
    public static final RegistryEntry.Reference<Potion> TAINTED_AMBROSIA = registerPotion(new Potion(new StatusEffectInstance(EffectRegistry.DISABLE_HEAL, 600, 0)), "tainted_ambrosia");

    public static void init() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, BlockRegistry.HYDRANGEA.asItem(), WARDING);
            builder.registerPotionRecipe(Potions.AWKWARD, BlockRegistry.OLEANDER.asItem(), TAINTED_AMBROSIA);
            builder.registerPotionRecipe(WARDING, Items.GLOWSTONE_DUST, STRONG_WARDING);
            builder.registerPotionRecipe(WARDING, Items.REDSTONE, LONG_WARDING);
        });
    }

    public static RegistryEntry.Reference<StatusEffect> registerEffect(StatusEffect effect, String name) {
		return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulsWeaponry.ModId, name), effect);
	}

    private static RegistryEntry.Reference<Potion> registerPotion(Potion potion, String name) {
        return Registry.registerReference(Registries.POTION, Identifier.of(SoulsWeaponry.ModId, name), potion);
    }

    static class DefaultStatusEffect extends StatusEffect {

        public DefaultStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
            super(statusEffectCategory, color);
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            if (this == EffectRegistry.BLEED.value()) {
                int i = 15 >> amplifier;
                if (i > 0) {
                    return duration % i == 0;
                }
                return true;
            }
            if (this == EffectRegistry.BLOODTHIRSTY.value()) {
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
        public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
            if (this == EffectRegistry.BLEED.value() && !entity.getType().isIn(EntityTypeTags.SKELETONS) && !entity.getType().isIn(ModTags.Entities.SKELETONS)) {
                entity.damage(CustomDamageSource.create(entity.getWorld(), CustomDamageSource.BLEED), 1f + amplifier);
            }
            if (this == EffectRegistry.BLOODTHIRSTY.value()) {
                entity.damage(entity.getWorld().getDamageSources().wither(), 1f);
            }
            return true;
        }
    }
}
