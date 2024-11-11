package net.soulsweaponry.entity.projectile;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class MoonlightProjectile extends NonArrowProjectile implements GeoEntity {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TrackedData<Integer> POINTS = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> TICK_PARTICLES = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_AGE = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HUGE_EXPLOSION = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ROTATE_STATE = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<ParticleEffect> EXPLOSION_PARTICLE = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.PARTICLE);
    private static final TrackedData<ParticleEffect> TRAIL_PARTICLE = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.PARTICLE);
    private static final TrackedData<Integer> APPLY_FIRE_TICKS = DataTracker.registerData(MoonlightProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public MoonlightProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world, Items.ARROW.getDefaultStack());
    }

    public MoonlightProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world, ItemStack stack) {
        super(entityType, world, stack);
    }
    
    public MoonlightProjectile(EntityType<? extends PersistentProjectileEntity> type, World world, LivingEntity owner, ItemStack stack) {
        super(type, owner, world, stack);
    }

    public MoonlightProjectile(EntityType<? extends PersistentProjectileEntity> type, World world, LivingEntity owner) {
        super(type, owner, world, Items.ARROW.getDefaultStack());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(POINTS, 75);
        builder.add(TICK_PARTICLES, 4);
        builder.add(MAX_AGE, 30);
        builder.add(HUGE_EXPLOSION, false);
        builder.add(ROTATE_STATE, 0);
        builder.add(EXPLOSION_PARTICLE, ParticleTypes.SOUL_FIRE_FLAME);
        builder.add(TRAIL_PARTICLE, ParticleTypes.GLOW);
        builder.add(APPLY_FIRE_TICKS, 0);
    }

    public void setAgeAndPoints(int maxAge, int explosionPoints, int tickParticleAmount) {
        this.dataTracker.set(MAX_AGE, maxAge);
        this.dataTracker.set(POINTS, explosionPoints);
        this.dataTracker.set(TICK_PARTICLES, tickParticleAmount);
    }

    public void setHugeExplosion(boolean bl) {
        this.dataTracker.set(HUGE_EXPLOSION, bl);
    }

    public void setRotateState(RotationState state) {
        for (int i = 0; i < RotationState.values().length; i++) {
            if (state.equals(RotationState.values()[i])) {
                this.dataTracker.set(ROTATE_STATE, i);
            }
        }
    }

    public RotationState getRotateState() {
        return RotationState.values()[this.dataTracker.get(ROTATE_STATE)];
    }

    public int getMaxParticlePoints() {
        return this.dataTracker.get(POINTS);
    }

    public int getTickParticleAmount() {
        return this.dataTracker.get(TICK_PARTICLES);
    }

    public double getMaxAge() {
        return this.dataTracker.get(MAX_AGE);
    }

    public void tick() {
        super.tick();
        Vec3d vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;
        for (int i = 0; i < this.getTickParticleAmount(); ++i) {
            this.getWorld().addParticle(this.getTrailParticleType(), this.getX() + e * (double)i / 4.0D, this.getY() + f * (double)i / 4.0D, this.getZ() + g * (double)i / 4.0D, -e, -f + 0.2D, -g);
        }

        if (this.age > this.getMaxAge()) {
            this.discard(); 
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }

    @Override
    public int getPunch() {
        if (this.asItemStack() != null) {
            return EnchantmentHelper.getLevel(Enchantments.KNOCKBACK, this.asItemStack());
        }
        return super.getPunch();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() != null && entityHitResult.getEntity() instanceof LivingEntity && this.asItemStack() != null) {
            float bonus = EnchantmentHelper.getAttackDamage(this.asItemStack(), ((LivingEntity) entityHitResult.getEntity()).getGroup());
            this.setDamage(this.getDamage() + (bonus >= 5 ? bonus * 0.7f : bonus));
        }
        super.onEntityHit(entityHitResult);
        if (this.getFireTicksOnHit() > 0 && entityHitResult.getEntity() != null) {
            entityHitResult.getEntity().setFireTicks(this.getFireTicksOnHit());
        }
        this.discard();
    }

    public void detonateEntity(World world, double x, double y, double z, double points, float sizeModifier) {
        double phi = Math.PI * (3. - Math.sqrt(5.));
        for (int i = 0; i < points; i++) {
            double velocityY = 1 - (i/(points - 1)) * 2;
            double radius = Math.sqrt(1 - velocityY*velocityY);
            double theta = phi * i;
            double velocityX = Math.cos(theta) * radius;
            double velocityZ = Math.sin(theta) * radius;
            world.addParticle(this.getExplosionParticleType(), true, x, y, z, velocityX*sizeModifier, velocityY*sizeModifier, velocityZ*sizeModifier);
        } 
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.dataTracker.get(HUGE_EXPLOSION)) {
            this.detonateEntity(getWorld(), this.getX(), this.getY(), this.getZ(), 750, 0.5f);
        } else {
            this.detonateEntity(getWorld(), this.getX(), this.getY(), this.getZ(), this.getMaxParticlePoints(), 0.125f);
        }
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_GENERIC_EXPLODE;
    }

    protected float getDragInWater() {
        return 1.01F;
    }

    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public void setExplosionParticleType(ParticleEffect particle) {
        this.getDataTracker().set(EXPLOSION_PARTICLE, particle);
    }

    public ParticleEffect getExplosionParticleType() {
        return this.getDataTracker().get(EXPLOSION_PARTICLE);
    }

    public void setTrailParticleType(ParticleEffect particle) {
        this.getDataTracker().set(TRAIL_PARTICLE, particle);
    }

    public ParticleEffect getTrailParticleType() {
        return this.getDataTracker().get(TRAIL_PARTICLE);
    }

    public void applyFireTicks(int ticks) {
        this.dataTracker.set(APPLY_FIRE_TICKS, ticks);
    }

    public int getFireTicksOnHit() {
        return this.dataTracker.get(APPLY_FIRE_TICKS);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Particle", this.getExplosionParticleType().asString());
        nbt.putString("TrailParticle", this.getExplosionParticleType().asString());
        nbt.putInt("FireTicksOnHit", this.getFireTicksOnHit());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Particle", 8)) {
            try {
                this.setExplosionParticleType(ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("Particle")), Registries.PARTICLE_TYPE.getReadOnlyWrapper()));
            } catch (CommandSyntaxException var5) {
                LOGGER.warn("Couldn't load custom particle {}", nbt.getString("Particle"), var5);
            }
        }
        if (nbt.contains("TrailParticle", 8)) {
            try {
                this.setTrailParticleType(ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("TrailParticle")), Registries.PARTICLE_TYPE.getReadOnlyWrapper()));
            } catch (CommandSyntaxException var5) {
                LOGGER.warn("Couldn't load custom particle {}", nbt.getString("TrailParticle"), var5);
            }
        }
        if (nbt.contains("FireTicksOnHit")) {
            this.applyFireTicks(nbt.getInt("FireTicksOnHit"));
        }
    }

    public enum RotationState {
        NORMAL,
        SWIPE_FROM_RIGHT,
        SWIPE_FROM_LEFT
    }
}
