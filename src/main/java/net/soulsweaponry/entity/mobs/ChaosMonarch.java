package net.soulsweaponry.entity.mobs;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.ai.goal.ChaosMonarchGoal;
import net.soulsweaponry.items.armor.ChaosSet;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import net.soulsweaponry.registry.ParticleRegistry;
import net.soulsweaponry.registry.SoundRegistry;
import net.soulsweaponry.util.CustomDeathHandler;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class ChaosMonarch extends BossEntity implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public int deathTicks;
    private int spawnTicks;
    private static final TrackedData<Integer> ATTACK = DataTracker.registerData(ChaosMonarch.class, TrackedDataHandlerRegistry.INTEGER);

    public ChaosMonarch(EntityType<? extends BossEntity> entityType, World world) {
        super(entityType, world, Color.PURPLE);
    }

    private PlayState predicate(AnimationState<?> state) {
        switch (this.getAttack()) {
            case SPAWN -> state.getController().setAnimation(RawAnimation.begin().thenPlay("spawn"));
            case TELEPORT -> state.getController().setAnimation(RawAnimation.begin().thenPlay("teleport"));
            case MELEE -> state.getController().setAnimation(RawAnimation.begin().thenPlay("swing_staff"));
            case LIGHTNING -> state.getController().setAnimation(RawAnimation.begin().thenPlay("lightning_call"));
            case SHOOT -> state.getController().setAnimation(RawAnimation.begin().thenPlay("shoot"));
            case BARRAGE -> state.getController().setAnimation(RawAnimation.begin().thenPlay("barrage"));
            case DEATH -> state.getController().setAnimation(RawAnimation.begin().thenPlay("death"));
            default -> state.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
	protected void initGoals() {
        this.goalSelector.add(1, new ChaosMonarchGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, AccursedLordBoss.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(5, (new RevengeGoal(this)).setGroupRevenge());
		super.initGoals();
	}

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return HostileEntity.createHostileAttributes()
        .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 60D)
        .add(EntityAttributes.GENERIC_MAX_HEALTH, ConfigConstructor.chaos_monarch_health)
        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0D)
        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
        .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
        .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 2.0D);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ATTACK, 0);
    }

    @Override
    public int getTicksUntilDeath() {
        return 80;
    }

    @Override
    public int getDeathTicks() {
        return this.deathTicks;
    }

    @Override
    public void setDeath() {
        this.setAttack(7);
    }

    @Override
    public void updatePostDeath() {
        this.deathTicks++;
        if (this.deathTicks >= this.getTicksUntilDeath() && !this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
            CustomDeathHandler.deathExplosionEvent(this.getWorld(), this.getPos(), SoundRegistry.DAWNBREAKER_EVENT, ParticleTypes.LARGE_SMOKE, ParticleTypes.DRAGON_BREATH, ParticleRegistry.PURPLE_FLAME);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getAttack() == Attack.SPAWN) {
            this.spawnTicks++;
            SimpleParticleType[] dragonParticles = {ParticleTypes.DRAGON_BREATH, ParticleTypes.DRAGON_BREATH};
            SimpleParticleType[] portalParticles = {ParticleTypes.PORTAL};
            if (this.spawnTicks % 2 == 0 && this.spawnTicks < 20) {
                this.particleExplosion(portalParticles, 4f);
            }
            if (this.spawnTicks == 40) {
                this.particleExplosion(dragonParticles, .5f);
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.HOSTILE, 1f, 1f);
            }
            if (this.spawnTicks >= 60) {
                this.setAttack(0);;
            }
        }
    }

    /* static enum Test {
        ATTACK,
        SHOOT,
        IDLE,
        DEATH
    }
    public static final Test TESTING[] = Test.values();
    System.out.println(TESTING[MathHelper.floor(this.getHeadYaw() / 90.0 + 0.5) & 3]); */
    /* & <-- verifies both operands
    && <-- stops evaluating if the first operand evaluates to false since the result will be false */

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == (this.getWorld().getDamageSources().lightningBolt())) {
            return false;
        }
        if (source.isOf(DamageTypes.WITHER)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public int getXp() {
        return ConfigConstructor.chaos_monarch_xp;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (this.hasStatusEffect(EffectRegistry.DECAY) && this.age % 10 == 0) {
            this.heal(this.getStatusEffect(EffectRegistry.DECAY).getAmplifier() + 1 + this.getAttackingPlayers().size());
            for (LivingEntity target : this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(3D))) {
                if (!(target instanceof PlayerEntity) && target != this) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 3));
                }
            }
        }
        if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
            this.removeStatusEffect(StatusEffects.LEVITATION);
        }
        if (this.hasStatusEffect(StatusEffects.WITHER)) {
            this.removeStatusEffect(StatusEffects.WITHER);
        }
        this.turnBlocks(this.getWorld(), this.getBlockPos());
    }

    private void turnBlocks(World world, BlockPos blockPos) {
        ChaosSet cape = (ChaosSet) ItemRegistry.CHAOS_ROBES;
        cape.turnBlocks(this, world, blockPos, 3);
    }

    private void particleExplosion(SimpleParticleType[] particles, float sizeModifier) {
        this.roundParticleOutburst(this.getWorld(), 1000, particles, this.getX(), this.getY() + 3, this.getZ(), sizeModifier);
    }

    public void roundParticleOutburst(World world, double points, SimpleParticleType[] particles, double x, double y, double z, float sizeModifier) {
        double phi = Math.PI * (3. - Math.sqrt(5.));
        for (int i = 0; i < points; i++) {
            double velocityY = 1 - (i/(points - 1)) * 2;
            double radius = Math.sqrt(1 - velocityY*velocityY);
            double theta = phi * i;
            double velocityX = Math.cos(theta) * radius;
            double velocityZ = Math.sin(theta) * radius;
            for (SimpleParticleType particle : particles) {
                world.addParticle(particle, true, x, y, z, velocityX * sizeModifier, velocityY * sizeModifier, velocityZ * sizeModifier);
            }
        } 
    }

    /**
     * Set the attack based on integer as Attack Id
     * <p>Idle: > 1 || 7 <
     * <p>Spawn: 1
     * <p>Teleport: 2
     * <p>Melee: 3
     * <p>Lightning: 4
     * <p>Shoot: 5
     * <p>Barrage 6
     * <p>Death: 7
     */
    public void setAttack(int attackId) {
        this.dataTracker.set(ATTACK, attackId);
    }

    /**
     * Turns the given integer previously to the datatracker into an enum for switch
     * statements.
     * 
     * <p> Was previously:
     * public int getAttack() {
     *      return this.dataTracker.get(ATTACK);
     * }
     * <p> Was changed to current function for clarity reasons.
     */
    
    public Attack getAttack() {
        return Attack.values()[this.dataTracker.get(ATTACK)];
    }

    public enum Attack {
        IDLE, SPAWN, TELEPORT, MELEE, LIGHTNING, SHOOT, BARRAGE, DEATH
    }

    public Vec3d getRotationVec(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }
    
    @Override
    public boolean disablesShield() {
        return ConfigConstructor.chaos_monarch_disables_shields;
    }

    @Override
    public boolean isFireImmune() {
        return ConfigConstructor.chaos_monarch_is_fire_immune;
    }

    @Override
    public boolean hasInvertedHealingAndHarm() {
        return ConfigConstructor.chaos_monarch_is_undead;
    }

    @Override
    public String getGroupId() {
        return ConfigConstructor.chaos_monarch_group_type;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }
}
