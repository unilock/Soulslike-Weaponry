package net.soulsweaponry.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.LeviathanAxe;
import net.soulsweaponry.particles.ParticleEvents;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.registry.WeaponRegistry;
import net.soulsweaponry.util.WeaponUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class LeviathanAxeEntity extends ReturningProjectile implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public LeviathanAxeEntity(EntityType<? extends LeviathanAxeEntity> entityType, World world) {
        super(entityType, world, WeaponRegistry.LEVIATHAN_AXE.getDefaultStack());
        this.ignoreCameraFrustum = true;
    }

    public LeviathanAxeEntity(EntityType<? extends LeviathanAxeEntity> entityType, World world, ItemStack stack) {
        super(entityType, world, stack);
        this.ignoreCameraFrustum = true;
    }

    public LeviathanAxeEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityRegistry.LEVIATHAN_AXE_ENTITY_TYPE, owner, world, stack);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public float getDamage(Entity target) {
        return ConfigConstructor.leviathan_axe_projectile_damage + WeaponUtil.getEnchantDamageBonus(this.asItemStack());
    }

    @Override
    public boolean collide(Entity owner, Entity target, float damage) {
        if (!this.getWorld().isClient && target instanceof MjolnirProjectile) {
            ParticleEvents.mjolnirLeviathanAxeCollision(this.getWorld(), this.getX(), this.getY(), this.getZ());
            this.getWorld().createExplosion(null, this.getX(), this.getY(), this.getZ(), 6.0F, true, World.ExplosionSourceType.TNT);
        }
        DamageSource damageSource = this.getWorld().getDamageSources().trident(this, owner);
        boolean damaged = target.damage(damageSource, damage);
        if (damaged) {
            if (target instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(EffectRegistry.FREEZING, 200, EnchantmentHelper.getLevel(Enchantments.SHARPNESS, this.asItemStack())));
            }
            LeviathanAxe.iceExplosion(getWorld(), this.getBlockPos(), this.getOwner(), EnchantmentHelper.getLevel(Enchantments.SHARPNESS, this.asItemStack()));
        }
        return damaged;
    }

    @Override
    public double getReturnSpeed(ItemStack stack) {
        return ConfigConstructor.leviathan_axe_return_speed + (double) EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack)/2f;
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof MjolnirProjectile) {
            return true;
        } else {
            return super.canHit(entity);
        }
    }

    private PlayState predicate(AnimationState<?> state) {
        if (this.dealtDamage) return PlayState.STOP;
        state.getController().setAnimation(RawAnimation.begin().then("spin", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}