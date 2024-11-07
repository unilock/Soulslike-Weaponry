package net.soulsweaponry.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.ai.goal.FreyrSwordGoal;
import net.soulsweaponry.entitydata.FreyrSwordSummonData;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.registry.WeaponRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;

public class FreyrSwordEntity extends TameableEntity implements GeoEntity {

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private final ItemStack stack;
    public static final BlockPos NULLISH_POS = new BlockPos(0, 0, 0);
    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(FreyrSwordEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> STATIONARY = DataTracker.registerData(FreyrSwordEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> IS_STATIONARY = DataTracker.registerData(FreyrSwordEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String STACK_NBT = "enchants_list";

    public FreyrSwordEntity(EntityType<? extends FreyrSwordEntity> entityType, World world) {
        super(entityType, world);
        this.stack = new ItemStack(WeaponRegistry.FREYR_SWORD);
    }

    public FreyrSwordEntity(World world, PlayerEntity owner, ItemStack stack) {
        super(EntityRegistry.FREYR_SWORD_ENTITY_TYPE, world);
        this.stack = stack.copy();
        this.setTamed(true);
        this.setOwner(owner);
    }

    public PlayState attack(AnimationState<?> state) {
        if (this.getAnimationAttacking()) {
            state.getController().setAnimation(RawAnimation.begin().then("attack_east", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        state.getController().stop();
        return PlayState.STOP;
    }

    private PlayState idle(AnimationState<?> state) {
        if (!this.getAnimationAttacking()) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<FreyrSwordEntity> attackController = new AnimationController<>(this, "attackController", 0, this::attack);
        AnimationController<FreyrSwordEntity> idleController = new AnimationController<>(this, "idleController", 0, this::idle);
        controllers.add(attackController);
        controllers.add(idleController);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FreyrSwordGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, HostileEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, SlimeEntity.class, true));
        super.initGoals();
    }

    @Override
    public boolean isTeammate(Entity other) {
        // Don't attack players if friendly fire is off
        if (!ConfigConstructor.sword_of_freyr_friendly_fire && other instanceof PlayerEntity) {
            return true;
        }
        if (other instanceof Tameable tameableOther) {
            if (tameableOther.getOwner() != null && this.getOwner() != null) {
                if (tameableOther.getOwner().equals(this.getOwner())) {
                    return true;
                }
            }
        }
        return super.isTeammate(other);
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.getOwner() != null) {
            if (!this.isBlockPosNullish(this.getStationaryPos()) /* != null */) {
                if (this.getTarget() == null || this.squaredDistanceTo(this.stationaryAsVec3d()) > this.getFollowRange()) {
                    this.updatePosition(this.getStationaryPos().getX(), this.getStationaryPos().getY(), this.getStationaryPos().getZ());
                    this.setAnimationAttacking(false);
                }
            } else {
                if (this.getTarget() == null || this.squaredDistanceTo(this.getOwner()) > this.getFollowRange()) {
                    Vec3d vecOwner = this.getOwner().getRotationVector();
                    double xAdd = 0;                    
                    if (this.getOwner().getPitch() < -50 || this.getOwner().getPitch() > 50) {
                        xAdd = vecOwner.getX() > 0 ? -1 : 1;
                    }
                    Vec3d vecEdited = vecOwner.multiply(-1.5).add(this.getOwner().getPos()).add(xAdd, 0, 0);
                    this.updatePositionAndAngles(vecEdited.getX(), this.getOwner().getY(), vecEdited.getZ(), this.getOwner().getYaw() / 2, this.getOwner().getPitch());
                    this.setAnimationAttacking(false);
                }
            }
        }
    }

    @Override
    public EntityView method_48926() {
        return super.getWorld();
    }

    @Override
    public int getAir() {
        return 300;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            LivingEntity owner = uUID == null ? null : this.getWorld().getPlayerByUuid(uUID);
            if (owner instanceof PlayerEntity player) {
                UUID swordUuid = FreyrSwordSummonData.getSummonUuid(player);
                if (swordUuid != null && swordUuid.equals(this.getUuid())) {
                    return owner;
                } else {
                    return null;
                }
            }
        } catch (IllegalArgumentException var2) {
            return null;
        }
        return null;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getOwner() == null || (this.getOwner() != null && this.getOwner().equals(player))) {
            if (!this.insertStack(player)) {
                this.setPos(player.getX(), player.getEyeY(), player.getZ());
                this.dropStack();
            }
            this.discard();
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public boolean insertStack(PlayerEntity player) {
        int slot = player.getInventory().selectedSlot;
        if (!player.getInventory().getMainHandStack().isEmpty()) {
            slot = -1;
        }
        return player.getInventory().insertStack(slot, this.asItemStack());
    }

    public void dropStack() {
        ItemEntity entity = this.dropStack(this.stack);
        if (entity != null) entity.setCovetedItem();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!getWorld().isClient && this.getBlockPos() != null) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f);
            this.stack.damage(10, this.getRandom(), null);
            if (!((this.stack.getMaxDamage() - this.stack.getDamage()) <= 0)) {
                if (this.getOwner() != null && this.getOwner() instanceof PlayerEntity player) {
                    if (!this.insertStack(player)) {
                        this.setPos(player.getX(), player.getEyeY(), player.getZ());
                        this.dropStack();
                    }
                } else {
                    this.dropStack();
                }
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    /**
     * Since servers crash when the input is null, this function checks if the blockPos is
     * "null-ish", since it is highly unlikely the player will be on the coords xyz 0,
     * it will consider that position to be equal to null, from there on out
     * deciding whether it should stay behind the owner or not.
     */
    public boolean isBlockPosNullish(BlockPos pos) {
        return pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.age % 4 == 0) {
            double random = this.getRandom().nextDouble();
            this.getWorld().addParticle(ParticleTypes.GLOW, false,
                this.getX() + random/4 - random/8, this.getEyeY() - random*6 + random*6/2, this.getZ() + random/4 - random/8, 
                random/16 - random/32, random - random/2, random/16 - random/32);
        }
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    /**
     * When the owner disconnects, the entity will lose its bond. It will then
     * despawn after 3 seconds.
     */
    public int getNoOwnerAge() {
        return 60; //1200
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return PathAwareEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, (double) ConfigConstructor.sword_of_freyr_damage);
    }

    public double getFollowRange() {
        return this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    public ItemStack asItemStack() {
        return this.stack;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public void setAnimationAttacking(boolean bl) {
        this.dataTracker.set(ATTACKING, bl);
    }

    private boolean getAnimationAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public BlockPos getStationaryPos() {
        return this.dataTracker.get(STATIONARY);
    }

    public void setStationaryPos(BlockPos pos) {
        if (this.dataTracker.get(IS_STATIONARY)) {
            this.dataTracker.set(STATIONARY, NULLISH_POS);
            this.dataTracker.set(IS_STATIONARY, false);
        } else {
            this.dataTracker.set(STATIONARY, pos);
            this.dataTracker.set(IS_STATIONARY, true);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() != null && this.getOwner() != null && source.getAttacker().equals(this.getOwner())) {
            return false;
        }
        return super.damage(source, amount);
    }

    public Vec3d stationaryAsVec3d() {
        return new Vec3d(this.getStationaryPos().getX(), this.getStationaryPos().getY(), this.getStationaryPos().getZ());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, Boolean.FALSE);
        this.dataTracker.startTracking(STATIONARY, NULLISH_POS);
        this.dataTracker.startTracking(IS_STATIONARY, Boolean.FALSE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    public PassiveEntity createChild(ServerWorld var1, PassiveEntity var2) {
        return null;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        ItemStack itemStack = this.asItemStack();
        if (itemStack.getNbt() != null) {
            nbt.put(STACK_NBT, itemStack.getNbt());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(STACK_NBT)) {
            this.stack.setNbt((NbtCompound) nbt.get(STACK_NBT));
        }
    }
}
