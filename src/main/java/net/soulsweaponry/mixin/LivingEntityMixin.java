package net.soulsweaponry.mixin;

import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.soulsweaponry.entity.mobs.BossEntity;
import net.soulsweaponry.networking.PacketRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.*;
import net.soulsweaponry.util.ParticleNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import net.soulsweaponry.registry.SoundRegistry;

import static net.soulsweaponry.items.UmbralTrespassItem.SHOULD_DAMAGE_RIDING;

@Mixin(LivingEntity.class)
public class LivingEntityMixin<T> {

    @Inject(method = "modifyAppliedDamage", at = @At("TAIL"), cancellable = true)
    protected void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> info) {
        LivingEntity entity = ((LivingEntity)(Object)this);
        float newAmount = info.getReturnValue();
        if (entity.hasStatusEffect(EffectRegistry.DECAY) && !entity.getEquippedStack(EquipmentSlot.HEAD).isOf(ItemRegistry.CHAOS_CROWN) && !entity.getEquippedStack(EquipmentSlot.HEAD).isOf(ItemRegistry.CHAOS_HELMET)) {
            int amplifier = entity.getStatusEffect(EffectRegistry.DECAY).getAmplifier();
            float amountAdded = newAmount * ((amplifier + 1)*.2f);
            newAmount += amountAdded;
        }
        if ((source.isOf(DamageTypes.INDIRECT_MAGIC) || source.isOf(DamageTypes.INDIRECT_MAGIC)) && entity.hasStatusEffect(EffectRegistry.MAGIC_RESISTANCE)) {
            int amplifier = entity.getStatusEffect(EffectRegistry.MAGIC_RESISTANCE).getAmplifier();
            float amountReduced = newAmount * ((amplifier + 1)*.2f);
            newAmount -= amountReduced;
        }
        if (entity.hasStatusEffect(EffectRegistry.POSTURE_BREAK) && !source.isIndirect() && source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            int amplifier = entity.getStatusEffect(EffectRegistry.POSTURE_BREAK).getAmplifier();
            float baseAdded = entity instanceof PlayerEntity ? 3f : 8f;
            float totalAdded = baseAdded * (amplifier + 1);
            newAmount += totalAdded;
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundRegistry.CRIT_HIT_EVENT, SoundCategory.HOSTILE, .5f, 1f);
            entity.removeStatusEffect(EffectRegistry.POSTURE_BREAK);
            if (entity.hasStatusEffect(StatusEffects.SLOWNESS) && entity.getStatusEffect(StatusEffects.SLOWNESS).getDuration() < 100) entity.removeStatusEffect(StatusEffects.SLOWNESS);
            if (entity.hasStatusEffect(StatusEffects.WEAKNESS) && entity.getStatusEffect(StatusEffects.WEAKNESS).getDuration() < 100) entity.removeStatusEffect(StatusEffects.WEAKNESS);
            if (entity.hasStatusEffect(StatusEffects.MINING_FATIGUE) && entity.getStatusEffect(StatusEffects.MINING_FATIGUE).getDuration() < 100) entity.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        }
        info.setReturnValue(newAmount);
    }

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    public void interceptHeal(float amount, CallbackInfo info) {
        if (((LivingEntity)(Object)this).hasStatusEffect(EffectRegistry.DISABLE_HEAL)) {
            info.cancel();
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void interceptDamage(DamageSource source, float amount, CallbackInfoReturnable<T> info) {
        if (source.isOf(DamageTypes.FALL) && ((LivingEntity)(Object)this).hasStatusEffect(EffectRegistry.CALCULATED_FALL)) {
            ((LivingEntity)(Object)this).removeStatusEffect(EffectRegistry.CALCULATED_FALL);
            //Removes, then re-adds for half a second so that "dream_on" advancement may trigger
            ((LivingEntity)(Object)this).addStatusEffect(new StatusEffectInstance(EffectRegistry.CALCULATED_FALL, 10, 0));
            info.cancel();
        }
    }

    @Inject(method = "onDismounted", at = @At("HEAD"))
    public void interceptDismount(Entity entity, CallbackInfo info) {
        if (entity instanceof LivingEntity && ((LivingEntity) (Object) this) instanceof PlayerEntity) {
            PlayerEntity player = ((PlayerEntity) (Object) this);
            try {
                if (player.getDataTracker().get(SHOULD_DAMAGE_RIDING)) {
                    LivingEntity target = ((LivingEntity) entity);
                    float damage = 20f;
                    ItemCooldownManager cooldownManager = player.getItemCooldownManager();
                    for (Hand hand : Hand.values()) {
                        ItemStack stack = player.getStackInHand(hand);
                        if (player.getStackInHand(hand).isOf(WeaponRegistry.SHADOW_ASSASSIN_SCYTHE)) {
                            damage = ConfigConstructor.shadow_assassin_scythe_ability_damage + EnchantmentHelper.getAttackDamage(stack, target.getGroup());
                            cooldownManager.set(stack.getItem(), ConfigConstructor.shadow_assassin_scythe_ability_cooldown);
                        } else if (player.getStackInHand(hand).isOf(WeaponRegistry.DARKIN_SCYTHE_PRIME)) {
                            damage = ConfigConstructor.darkin_scythe_prime_ability_damage + EnchantmentHelper.getAttackDamage(stack, target.getGroup()) + (target.getMaxHealth() * (ConfigConstructor.darkin_scythe_prime_ability_percent_health_damage / 100f));
                            float healing = damage * ConfigConstructor.darkin_scythe_prime_heal_modifier;
                            player.heal(healing);
                            cooldownManager.set(stack.getItem(), ConfigConstructor.darkin_scythe_prime_ability_cooldown);
                        }
                    }
                    target.damage(player.getWorld().getDamageSources().mobAttack(player), damage);
                    player.getDataTracker().set(SHOULD_DAMAGE_RIDING, false);
                    if (!player.getWorld().isClient && player.getBlockPos() != null) {
                        player.getWorld().playSound(null, player.getBlockPos(), SoundRegistry.SLICE_TARGET_EVENT, SoundCategory.PLAYERS, 0.8f, 1f);
                        ParticleNetworking.specificServerParticlePacket((ServerWorld) player.getWorld(), PacketRegistry.UMBRAL_TRESPASS_ID, player.getBlockPos(), player.getEyeY());
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    protected void interceptGetMaxHealth(CallbackInfoReturnable<Float> infoReturnable) {
        if (((LivingEntity)(Object)this) instanceof BossEntity boss) {
            infoReturnable.setReturnValue((float) boss.getBossMaxHealth());
        }
    }
}
