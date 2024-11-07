package net.soulsweaponry.entity.mobs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.soulsweaponry.particles.ParticleEvents;
import net.soulsweaponry.particles.ParticleHandler;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.registry.ParticleRegistry;
import net.soulsweaponry.registry.SoundRegistry;
import net.soulsweaponry.util.CustomDeathHandler;
import net.soulsweaponry.util.IAnimatedDeath;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Soulmass extends Remnant implements GeoEntity, IAnimatedDeath {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    public int deathTicks;
    private List<Integer> summonIds = new ArrayList<>();

    private static final TrackedData<Boolean> CLAP = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SMASH = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> START_BEAM = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> STOP_BEAM = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BEAMING = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SACRIFICE = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> BEAM_CORDS = DataTracker.registerData(Soulmass.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public Soulmass(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 30;
    }

    private PlayState predicate(AnimationState<?> state) {
        if (this.getSacrifice()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("sacrifice"));
        } else if (this.getStartBeam()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("start_beam"));
        } else if (this.isSneaking()) {
            state.getController().setAnimation(RawAnimation.begin().then("start_beam", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else if (this.getStopBeam()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("stop_beam"));
        } else if (this.getBeaming()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("beaming"));
        } else if (this.getClap()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("clap"));
        } else if (this.getSmash()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("smash_ground"));
        } else {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        }
        
        return PlayState.CONTINUE;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new SoulmassGoal(this));
        super.initGoals();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public int getSoulAmount() {
        return 30;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CLAP, Boolean.FALSE);
        this.dataTracker.startTracking(SMASH, Boolean.FALSE);
        this.dataTracker.startTracking(START_BEAM, Boolean.FALSE);
        this.dataTracker.startTracking(STOP_BEAM, Boolean.FALSE);
        this.dataTracker.startTracking(BEAMING, Boolean.FALSE);
        this.dataTracker.startTracking(SACRIFICE, Boolean.FALSE);
        this.dataTracker.startTracking(BEAM_CORDS, new BlockPos(0, 0, 0));
    }

    public static DefaultAttributeContainer.Builder createSoulmassAttributes() {
        return MobEntity.createMobAttributes()
        .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20D)
        .add(EntityAttributes.GENERIC_MAX_HEALTH, 50D)
        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
        .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
    }

    public void setClap(boolean bl) {
        this.dataTracker.set(CLAP, bl);
    }

    public boolean getClap() {
        return this.dataTracker.get(CLAP);
    }

    public void setSmash(boolean bl) {
        this.dataTracker.set(SMASH, bl);
    }

    public boolean getSmash() {
        return this.dataTracker.get(SMASH);
    }

    public void setStartBeam(boolean bl) {
        this.dataTracker.set(START_BEAM, bl);
    }

    public boolean getStartBeam() {
        return this.dataTracker.get(START_BEAM);
    }

    public void setStopBeam(boolean bl) {
        this.dataTracker.set(STOP_BEAM, bl);
    }

    public boolean getStopBeam() {
        return this.dataTracker.get(STOP_BEAM);
    }

    public void setBeaming(boolean bl) {
        this.dataTracker.set(BEAMING, bl);
    }

    public boolean getBeaming() {
        return this.dataTracker.get(BEAMING);
    }

    public void setSacrifice(boolean bl) {
        this.dataTracker.set(SACRIFICE, bl);
    }

    public boolean getSacrifice() {
        return this.dataTracker.get(SACRIFICE);
    }

    public void setBeamCords(double x, double y, double z) {
        this.dataTracker.set(BEAM_CORDS, BlockPos.ofFloored(x, y, z));
    }

    public BlockPos getBeamCords() {
        return this.dataTracker.get(BEAM_CORDS);
    }

    public void resetAnimations() {
        this.setClap(false);
        this.setSmash(false);
        this.setBeaming(false);
        this.setStartBeam(false);
        this.setStopBeam(false);
    }

    @Override
    public int getTicksUntilDeath() {
        return 60;
    }

    @Override
    public void setDeath() {
        this.setSacrifice(true);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.setDeath();
    }

    @Override
    public int getDeathTicks() {
        return this.deathTicks;
    }

    @Override
    public void updatePostDeath() {
        this.deathTicks++;
        if (this.deathTicks >= this.getTicksUntilDeath() && !this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
            CustomDeathHandler.deathExplosionEvent(this.getWorld(), this.getPos(), SoundRegistry.DAWNBREAKER_EVENT, ParticleRegistry.PURPLE_FLAME, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.LARGE_SMOKE);
            this.sacrificeEvent();
            this.remove(RemovalReason.KILLED);
        }
    }

    public void sacrificeEvent() {
        Box chunkBox = new Box(this.getBlockPos()).expand(16);
        List<Entity> nearbyEntities = this.getWorld().getOtherEntities(this, chunkBox);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof HostileEntity) {
                LivingEntity closestTarget = (LivingEntity) nearbyEntity;
                closestTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 1));
                closestTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 1));
                closestTarget.damage(this.getWorld().getDamageSources().magic(), 16F);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putIntArray("summon_ids", this.summonIds);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("summon_ids")) {
            this.summonIds = WeaponUtil.arrayToList(nbt.getIntArray("summon_ids"));
        }
    }

    public void addSummonIds(int id) {
        this.summonIds.add(id);
    }

    public boolean hasSummonsAlive() {
        if (!this.getWorld().isClient) {
            for (int id : summonIds) {
                if (getWorld().getEntityById(id) != null) {
                    return true;
                }
            }
        }
        this.summonIds.clear();
        return false;
    }

    static class SoulmassGoal extends Goal {
        private final Soulmass entity;
        private int attackCooldown;
        private int attackStatus;
        private int uniqueCooldown;
        private int nextAttack = 0;

        public SoulmassGoal(Soulmass entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity target = this.entity.getTarget();
            return target != null && target.isAlive() && this.entity.canTarget(target);
        }

        @Override
        public void stop() {
            super.stop();
            this.entity.resetAnimations();
            this.entity.setAttacking(false);
            this.resetCooldowns(10, 10);
        }

        public void newAttack() {
            this.nextAttack = this.entity.random.nextInt(5);
        }

        private void resetCooldowns(int uniqueCooldown, int attackCooldown) {
            this.attackCooldown = attackCooldown;
            this.uniqueCooldown = uniqueCooldown;
            this.attackStatus = 0;
            this.newAttack();
        }

        public void tick() {
            if (attackCooldown > -1) {attackCooldown--;}
            if (uniqueCooldown > -1) {uniqueCooldown--;}
            LivingEntity target = this.entity.getTarget();
            
            if (target != null) {
                double distanceToEntity = this.entity.squaredDistanceTo(target);
                this.entity.setAttacking(true);
                this.entity.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());

                if (this.attackCooldown < 0) {
                    if (this.nextAttack > 1) {
                        this.entity.setClap(true);
                    } else if (this.uniqueCooldown > 10) {
                        this.newAttack();
                    }
                    if (this.uniqueCooldown < 0) {
                        if (this.nextAttack == 0) {
                            this.entity.setStartBeam(true);
                            this.resetCooldowns(80, 50);
                        } else if (this.nextAttack == 1) {
                            if (!this.entity.hasSummonsAlive()) {
                                this.entity.setSmash(true);
                            } else {
                                this.entity.setSmash(false);
                                this.newAttack();
                            }
                        }
                    }
                }

                if (this.entity.getClap()) {
                    attackStatus++;
                    if (this.attackStatus == 5) {
                        this.castSpell(target);
                    } else if (this.attackStatus >= 20) {
                        this.entity.setClap(false);
                        this.resetCooldowns(0, 10);
                    }
                }
                if (this.entity.getSmash()) {
                    attackStatus++;
                    if (this.attackStatus == 10) {
                        int[][] cords = {{4,4}, {-4,4}, {4,-4}, {-4,-4}};
                        for (int[] cord : cords) {
                            Vec3d pos = new Vec3d(this.entity.getX() + cord[0], this.entity.getY(), this.entity.getZ() + cord[1]);
                            if (!this.entity.getWorld().isClient) {
                                ParticleHandler.particleOutburstMap(this.entity.getWorld(), 50, pos.getX(), pos.getY(), pos.getZ(), ParticleEvents.CONJURE_ENTITY_MAP, 1f);
                            }

                            this.entity.getWorld().playSound(null, target.getBlockPos(), SoundRegistry.NIGHTFALL_SPAWN_EVENT, SoundCategory.PLAYERS, 0.6f, 1f);
                            SoulReaperGhost mob = new SoulReaperGhost(EntityRegistry.SOUL_REAPER_GHOST, this.entity.getWorld());
                            mob.setSoulAmount(0);
                            this.entity.addSummonIds(mob.getId());
                            mob.setPos(this.entity.getX() + cord[0], this.entity.getY() + .1f, this.entity.getZ() + cord[1]);
                            if (this.entity.getOwner() instanceof PlayerEntity) {
                                mob.setOwner((PlayerEntity) this.entity.getOwner());
                            }
                            this.entity.getWorld().spawnEntity(mob);
                        }
                    } else if (this.attackStatus >= 20) {
                        this.entity.setSmash(false);
                        this.resetCooldowns(100, 10);
                    }
                }
                if (this.entity.getStartBeam()) {
                    attackStatus++;
                    if (this.attackStatus == 12) {
                        this.entity.setBeaming(true);
                        this.attackStatus = 0;
                        this.entity.setStartBeam(false);
                    }
                }
                if (this.entity.getBeaming()) {
                    attackStatus++;
                    if (attackStatus > 30 || target.isDead() || distanceToEntity > 140f) {
                        this.entity.setBeaming(false);
                        this.entity.setStopBeam(true);
                        this.attackStatus = 0;
                    }
                    this.entity.setBeamCords(target.getBlockX(), target.getEyeY(), target.getBlockZ());
                    if (attackStatus % 5 == 0 && distanceToEntity < 130f) {
                        target.damage(this.entity.getWorld().getDamageSources().mobAttack(this.entity), 6f);
                        this.entity.heal(2f);
                        this.entity.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
                if (this.entity.getStopBeam()) {
                    attackStatus++;
                    if (attackStatus == 12) {
                        this.entity.setStopBeam(false);
                        this.resetCooldowns(150, 10);
                    }
                }
                
                super.tick();
            }
        }

        protected void castSpell(LivingEntity livingEntity) {
            double d = Math.min(livingEntity.getY(), this.entity.getY());
            double e = Math.max(livingEntity.getY(), this.entity.getY()) + 1.0;
            float f = (float)MathHelper.atan2(livingEntity.getZ() - this.entity.getZ(), livingEntity.getX() - this.entity.getX());
            if (this.entity.squaredDistanceTo(livingEntity) < 9.0) {
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float)i * (float)Math.PI * 0.4f;
                    this.conjureFangs(this.entity.getX() + (double)MathHelper.cos(g) * 1.5, this.entity.getZ() + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float)i * (float)Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(this.entity.getX() + (double)MathHelper.cos(g) * 2.5, this.entity.getZ() + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double)(i + 1);
                    this.conjureFangs(this.entity.getX() + (double)MathHelper.cos(f) * h, this.entity.getZ() + (double)MathHelper.sin(f) * h, d, e, f, i);
                }
            }
        }

        @SuppressWarnings("all")
        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = this.entity.getWorld().getBlockState(blockPos2 = blockPos.down())).isSideSolidFullSquare(this.entity.getWorld(), blockPos2, Direction.UP)) continue;
                if (!this.entity.getWorld().isAir(blockPos) && !(voxelShape = (blockState2 = this.entity.getWorld().getBlockState(blockPos)).getCollisionShape(this.entity.getWorld(), blockPos)).isEmpty()) {
                    d = voxelShape.getMax(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
            if (bl) {
                this.entity.getWorld().spawnEntity(new EvokerFangsEntity(this.entity.getWorld(), x, (double)blockPos.getY() + d, z, yaw, warmup, this.entity));
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getParticleX(0.5D), this.getRandomBodyY(), this.getParticleZ(0.5D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isSneaking() && !this.isDead()) {
            this.soulCircle(this.getWorld(), this.getX(), this.getEyeY() + 1.0F, this.getZ());
            if (this.getHealth() < this.getMaxHealth()) this.heal(1f);
        } else if (this.getBeaming()) {
            double e = this.getBeamCords().getX() - this.getX();
            double f = this.getBeamCords().getY() - this.getEyeY();
            double g = this.getBeamCords().getZ() - this.getZ();
            double h = Math.sqrt(e * e + f * f + g * g);
            e /= h;
            f /= h;
            g /= h;
            double j = this.random.nextDouble();
            for (int i = 0; i < 10; i++) {
                j += .5f + this.random.nextDouble();
                this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + e * j, this.getEyeY() + 1 + f * j, this.getZ() + g * j, 0.0D, 0.0D, 0.0D);
                this.getWorld().addParticle(ParticleTypes.SOUL, this.getX() + e * j, this.getEyeY() + 1 + f * j, this.getZ() + g * j, 0.0D, 0.0D, 0.0D);
            }
            this.soulCircle(this.getWorld(), this.getX(), this.getEyeY() + 1.0F, this.getZ());
        }
    }

    public void soulCircle(World world, double x, double y, double z) {
        double points = 30;
        double phi = Math.PI * (3. - Math.sqrt(5.));
        for (int i = 0; i < points; i++) {
            double velocityY = 1 - (i/(points - 1)) * 2;
            double radius = Math.sqrt(1 - velocityY*velocityY);
            double theta = phi * i;
            double velocityX = Math.cos(theta) * radius;
            double velocityZ = Math.sin(theta) * radius;
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, true, x + velocityX/2, y + velocityY/2, z + velocityZ/2, 0, 0, 0);
        } 
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SOULMASS_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ITEM_TOTEM_USE;
    }

    @Override
    public void initEquip() {}

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    
}
