package net.soulsweaponry.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.particles.ParticleEvents;
import net.soulsweaponry.particles.ParticleHandler;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.SoundRegistry;
import net.soulsweaponry.util.WeaponUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.List;

public abstract class AbstractDawnbreaker extends ChargeToUseItem implements GeoItem {

    public AbstractDawnbreaker(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.DAWNBREAKER, WeaponUtil.TooltipAbilities.BLAZING_BLADE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isDisabled(stack)) {
            return super.postHit(stack, target, attacker);
        }
        target.setOnFireFor(4 + 3 * EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack));
        if (target.hasInvertedHealingAndHarm() || ConfigConstructor.dawnbreaker_affect_all_entities) {
            if (target.isDead()) {
                if (target.hasStatusEffect(EffectRegistry.RETRIBUTION)) {
                    double chance = ConfigConstructor.dawnbreaker_ability_percent_chance_addition + 1 - (Math.pow(.75, target.getStatusEffect(EffectRegistry.RETRIBUTION).getAmplifier()));
                    double random = target.getRandom().nextDouble();
                    if (random < chance) {
                        AbstractDawnbreaker.dawnbreakerEvent(target, attacker, stack);
                    }
                }
            }
            if (target.hasStatusEffect(EffectRegistry.RETRIBUTION)) {
                int amplifier = target.getStatusEffect(EffectRegistry.RETRIBUTION).getAmplifier();
                target.addStatusEffect(new StatusEffectInstance(EffectRegistry.RETRIBUTION, 80, amplifier + 1));
            } else {
                target.addStatusEffect(new StatusEffectInstance(EffectRegistry.RETRIBUTION, 80, EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack)));
            }
        }
        return super.postHit(stack, target, attacker);
    }

    /**
     * Excecutes the dawnbreaker explosion.
     * @param target the position of where the explosion and sound will come from the target's position
     * @param attacker damage and effects will not apply to attacker
     * @param stack used to gather damage buffs
     */
    public static void dawnbreakerEvent(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        if (!attacker.getWorld().isClient && attacker instanceof ServerPlayerEntity) {
            //Replaces old DAWNBREAKER_PACKET call
            ParticleHandler.particleSphere(attacker.getWorld(), 1000, target.getX(), target.getEyeY() - .25f, target.getZ(), ParticleTypes.FLAME, 1f);
            ParticleHandler.particleOutburstMap(attacker.getWorld(), 200, target.getX(), target.getY(), target.getZ(), ParticleEvents.DAWNBREAKER_MAP, 1f);
        }
        target.getWorld().playSound(null, target.getBlockPos(), SoundRegistry.DAWNBREAKER_EVENT, SoundCategory.HOSTILE, 2f, 1f);
        Box aoe = target.getBoundingBox().expand(10);
        List<Entity> entities = attacker.getWorld().getOtherEntities(target, aoe);
        boolean bl = ConfigConstructor.dawnbreaker_affect_all_entities;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity targetHit) {
                if (targetHit.hasInvertedHealingAndHarm() || bl) {
                    if (!targetHit.equals(attacker)) {
                        targetHit.setOnFireFor(4 + EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack));
                        targetHit.damage(attacker.getWorld().getDamageSources().explosion(null, attacker), ConfigConstructor.dawnbreaker_ability_damage + 5 * EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack));
                        targetHit.addStatusEffect(new StatusEffectInstance(EffectRegistry.FEAR, 80, 0));
                    }
                }
            }
        }
    }
}