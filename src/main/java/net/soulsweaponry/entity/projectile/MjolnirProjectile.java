package net.soulsweaponry.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.registry.WeaponRegistry;
import net.soulsweaponry.util.WeaponUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class MjolnirProjectile extends ReturningProjectile implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public MjolnirProjectile(EntityType<? extends MjolnirProjectile> entityType, World world) {
        super(entityType, world, WeaponRegistry.MJOLNIR.getDefaultStack());
    }

    public MjolnirProjectile(EntityType<? extends MjolnirProjectile> entityType, World world, ItemStack stack) {
        super(entityType, world, stack);
    }

    public MjolnirProjectile(World world, LivingEntity owner, ItemStack stack) {
        super(EntityRegistry.MJOLNIR_ENTITY_TYPE, owner, world, stack);
    }

    @Override
    public double getReturnSpeed(ItemStack stack) {
        return ConfigConstructor.mjolnir_return_speed + (double) WeaponUtil.getEnchantDamageBonus(this.asItemStack())/2;
    }

    @Override
    public float getDamage(Entity target) {
        float f = ConfigConstructor.mjolnir_projectile_damage;
        if (target instanceof LivingEntity) f += EnchantmentHelper.getAttackDamage(this.asItemStack(), ((LivingEntity) target).getGroup());
        return f;
    }

    @Override
    public boolean collide(Entity owner, Entity target, float damage) {
        DamageSource damageSource = this.getWorld().getDamageSources().trident(this, owner);
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        BlockPos blockPos;
        float g = 1f;
        boolean bl = target.damage(damageSource, damage);
        int strikes = 1;
        if (this.getWorld().isThundering() || target instanceof LeviathanAxeEntity) strikes = 3;
        if (bl && this.getWorld() instanceof ServerWorld && this.getWorld().isSkyVisible(blockPos = target.getBlockPos())) {
            for (int i = 0; i < strikes; i++) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld());
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(owner instanceof ServerPlayerEntity ? (ServerPlayerEntity)owner : null);
                this.getWorld().spawnEntity(lightningEntity);
            }
            soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
            g = 5.0f;
        }
        this.playSound(soundEvent, g, 1.0f);
        return bl;
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LeviathanAxeEntity) {
            return true;
        } else {
            return super.canHit(entity);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}