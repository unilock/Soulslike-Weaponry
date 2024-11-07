package net.soulsweaponry.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.ai.goal.DraugrBossGoal;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.registry.ParticleRegistry;
import net.soulsweaponry.registry.SoundRegistry;
import net.soulsweaponry.util.CustomDeathHandler;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;

public class DraugrBoss extends BossEntity implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public int deathTicks;
    private int spawnTicks;
    private boolean shouldDisableShield = false;
    private String weaponDamagedById = "none";
    private static final String LAST_WEAPON_DAMAGED_BY = "last_weapon_damaged_by";

    public DraugrBoss(EntityType<? extends DraugrBoss> entityType, World world) {
        super(entityType, world, BossBar.Color.WHITE);
    }

    private static final TrackedData<Boolean> IS_SHIELDING = DataTracker.registerData(DraugrBoss.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> POSTURE_BROKEN = DataTracker.registerData(DraugrBoss.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STATES = DataTracker.registerData(DraugrBoss.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> POS = DataTracker.registerData(DraugrBoss.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> SAME_WEAPON_COUNT = DataTracker.registerData(DraugrBoss.class, TrackedDataHandlerRegistry.INTEGER);

    private PlayState attackAnimations(AnimationState<?> event) {
        if (this.isPostureBroken()) {
            event.getController().setAnimation(RawAnimation.begin().then("posture_break", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            switch (this.getState()) {
                case IDLE -> {
                    if (this.isShielding()) {
                        if (event.isMoving()) {
                            event.getController().setAnimation(RawAnimation.begin().then("block_stance", Animation.LoopType.LOOP));
                        } else {
                            event.getController().setAnimation(RawAnimation.begin().then("idle_block", Animation.LoopType.LOOP));
                        }
                    } else {
                        if (event.isMoving()) {
                            event.getController().setAnimation(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
                        } else {
                            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
                        }
                    }
                }
                case SPAWN -> event.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case DEATH -> event.getController().setAnimation(RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case COUNTER -> event.getController().setAnimation(RawAnimation.begin().then("counter", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case SHIELD_BASH -> event.getController().setAnimation(RawAnimation.begin().then("shield_bash", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case SHIELD_VAULT -> event.getController().setAnimation(RawAnimation.begin().then("shield_vault", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case SWIPES -> event.getController().setAnimation(RawAnimation.begin().then("swipes", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case BACKSTEP -> event.getController().setAnimation(RawAnimation.begin().then("backstep_block", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case HEAVY -> event.getController().setAnimation(RawAnimation.begin().then("charged_attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case GROUND_SLAM -> event.getController().setAnimation(RawAnimation.begin().then("ground_slam", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case PARRY -> event.getController().setAnimation(RawAnimation.begin().then("parry", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case BATTLE_CRY -> event.getController().setAnimation(RawAnimation.begin().then("battle_cry", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case LEAP -> event.getController().setAnimation(RawAnimation.begin().then("sword_leap", Animation.LoopType.HOLD_ON_LAST_FRAME));
                case RUN_THRUST -> event.getController().setAnimation(RawAnimation.begin().then("thrust_run", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
        }
        return PlayState.CONTINUE;
    }

    public static DefaultAttributeContainer.Builder createBossAttributes() {
        return HostileEntity.createHostileAttributes()
        .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 60D)
        .add(EntityAttributes.GENERIC_MAX_HEALTH, ConfigConstructor.old_champions_remains_health)
        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10D)
        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
        .add(EntityAttributes.GENERIC_ARMOR, 5.0D);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_SHIELDING, Boolean.FALSE);
        this.dataTracker.startTracking(POSTURE_BROKEN, Boolean.FALSE);
        this.dataTracker.startTracking(STATES, 0);
        this.dataTracker.startTracking(SAME_WEAPON_COUNT, 0);
        this.dataTracker.startTracking(POS, new BlockPos(0,0,0));
    }


    @Override
	protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new DraugrBossGoal(this));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(5, (new RevengeGoal(this)).setGroupRevenge());
		super.initGoals();
	}

    public void setShielding(boolean bl) {
        this.dataTracker.set(IS_SHIELDING, bl);
    }

    public boolean isShielding() {
        return this.dataTracker.get(IS_SHIELDING);
    }

    public void setPostureBroken(boolean bl) {
        this.dataTracker.set(POSTURE_BROKEN, bl);
    }

    public boolean isPostureBroken() {
        return this.dataTracker.get(POSTURE_BROKEN);
    }

    private void setSameWeaponCount(int amount) {
        this.dataTracker.set(SAME_WEAPON_COUNT, amount);
    }

    private void addSameWeaponCount() {
        this.setSameWeaponCount(this.getSameWeaponCount() + 1);
    }

    private int getSameWeaponCount() {
        return this.dataTracker.get(SAME_WEAPON_COUNT);
    }

    public void setTargetPos(BlockPos pos) {
        this.dataTracker.set(POS, pos);
    }

    public BlockPos getTargetPos() {
        return this.dataTracker.get(POS);
    }

    public void setState(States attack) {
        for (int i = 0; i < States.values().length; i++) {
            if (States.values()[i].equals(attack)) {
                this.dataTracker.set(STATES, i);
            }
        }
    }

    public States getState() {
        return States.values()[this.dataTracker.get(STATES)];
    }

    public enum States {
        IDLE, SPAWN, DEATH, COUNTER, SHIELD_BASH, SHIELD_VAULT, SWIPES, BACKSTEP, HEAVY, GROUND_SLAM,
        PARRY, BATTLE_CRY, LEAP, RUN_THRUST
    }

    public void setSpawning() {
        this.setState(States.SPAWN);
    }

    public boolean isSpawning() {
        return this.getState().equals(States.SPAWN);
    }

    public void tickMovement() {
        super.tickMovement();
        
        if (this.isSpawning()) {
            this.spawnTicks++;
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 50));
            for(int i = 0; i < 30; ++i) {
                Random random = this.getRandom();
                BlockPos pos = this.getBlockPos();
                double d = random.nextGaussian() * 0.05D;
                double e = random.nextGaussian() * 0.05D;
                double newX = random.nextDouble() - 0.5D + random.nextGaussian() * 0.15D + d;
                double newZ = random.nextDouble() - 0.5D + random.nextGaussian() * 0.15D + e;
                double newY = random.nextDouble() - 0.5D + random.nextDouble() * 0.5D;
                getWorld().addParticle(ParticleTypes.SOUL, pos.getX(), pos.getY(), pos.getZ(), newX/2, newY/8, newZ/2);
                getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), newX/2, newY/8, newZ/2);
            }
            
            if (this.spawnTicks % 10 == 0 && this.spawnTicks < 40) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 1f, 1f);
            }
            if (this.spawnTicks == 48 || this.spawnTicks == 55) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundRegistry.SWORD_HIT_SHIELD_EVENT, SoundCategory.HOSTILE, 1f, 1f);
                for (Entity entity : this.getWorld().getOtherEntities(this, getBoundingBox().expand(10f))) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0));
                        ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
                    }
                }
            }
            if (this.spawnTicks >= 70) {
                this.setState(States.IDLE);
            }
        }

        if (this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 0));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 10, 1));
            for(int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getParticleX(0.5D), this.getRandomBodyY(), this.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        } else if (this.getWorld().isSkyVisible(this.getBlockPos())) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 1));
            for(int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.SNOWFLAKE, this.getParticleX(0.5D), this.getRandomBodyY(), this.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
        if (this.isShielding()) this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 1));
    }

    /**
     * For each hit the boss takes with the same weapon, the more resistant it grows against that weapon.
     * The attacker has to switch between weapons to fully utilize their damage. When starting to become
     * resistant, the damage sound changes from ENTITY_WITHER_SKELETON_HURT to ENTITY_ZOMBIE_ATTACK_IRON_DOOR.
     * {@link net.soulsweaponry.items.TrickWeapon} come especially in handy here.
    */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            String item = attacker.getMainHandStack().getTranslationKey();
            if (Objects.equals(this.weaponDamagedById, item)) {
                this.addSameWeaponCount();
            } else {
                this.setSameWeaponCount(0);
            }
            if (this.getSameWeaponCount() >= ConfigConstructor.old_champions_remains_hits_before_growing_resistant) {
                double x = this.getSameWeaponCount() - (ConfigConstructor.old_champions_remains_hits_before_growing_resistant -
                        this.getSameWeaponCount() > 0 ? 1 : 0);
                amount = (float) (amount * Math.pow((1f / 1.07f), x));
            }
            this.weaponDamagedById = item;
        }
        return super.damage(source, amount);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString(LAST_WEAPON_DAMAGED_BY, this.weaponDamagedById);
    }

    @Override
    public int getXp() {
        return ConfigConstructor.old_champions_remains_xp;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(LAST_WEAPON_DAMAGED_BY)) {
            this.weaponDamagedById = nbt.getString(LAST_WEAPON_DAMAGED_BY);
        }
    }

    @Override
    public boolean isUndead() {
        return ConfigConstructor.old_champions_remains_is_undead;
    }

    @Override
    public String getGroupId() {
        return ConfigConstructor.old_champions_remains_group_type;
    }

    @Override
    public boolean isFireImmune() {
        return ConfigConstructor.old_champions_remains_is_fire_immune;
    }

    @Override
    public int getTicksUntilDeath() {
        return 20;
    }

    @Override
    public int getDeathTicks() {
        return this.deathTicks;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.setState(States.DEATH);
        CustomDeathHandler.deathExplosionEvent(this.getWorld(), this.getPos(), SoundRegistry.NIGHTFALL_SPAWN_EVENT, ParticleTypes.LARGE_SMOKE, ParticleTypes.SOUL_FIRE_FLAME, ParticleRegistry.BLACK_FLAME);
        NightShade entity = new NightShade(EntityRegistry.NIGHT_SHADE, getWorld());
        entity.setPos(this.getX(), this.getY() + .1F, this.getZ());
        entity.setVelocity(0, .1f, 0);
        entity.setSpawn();
        getWorld().spawnEntity(entity);
    }

    @Override
    public void setDeath() {
    }

    @Override
    public void updatePostDeath() {
        this.deathTicks++;
        if (this.deathTicks >= this.getTicksUntilDeath() && !this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean disablesShield() {
        return ConfigConstructor.old_champions_remains_disables_shields && this.shouldDisableShield || ConfigConstructor.old_champions_remains_disables_shields_all_attacks;
    }

    public void updateDisableShield(boolean bl) {
        this.shouldDisableShield = bl;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::attackAnimations));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }
  
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.getSameWeaponCount() >= ConfigConstructor.old_champions_remains_hits_before_growing_resistant) {
            return SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR;
        } else {
            return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
        }
    }
  
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }
}
