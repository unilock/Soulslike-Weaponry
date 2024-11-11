package net.soulsweaponry.entity.projectile.invisible;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.soulsweaponry.entity.mobs.NightProwler;
import net.soulsweaponry.particles.ParticleEvents;
import net.soulsweaponry.particles.ParticleHandler;
import net.soulsweaponry.registry.EntityRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class BlackflameSnakeEntity extends InvisibleEntity {
    private boolean hasHitPlayer;
    private static final TrackedData<Optional<UUID>> TARGET_UUID = DataTracker.registerData(BlackflameSnakeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public BlackflameSnakeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world, ItemStack stack) {
        super(entityType, world, stack);
    }

    public BlackflameSnakeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world, Items.AIR.getDefaultStack());
    }

    public BlackflameSnakeEntity(World world, LivingEntity owner) {
        super(EntityRegistry.BLACKFLAME_SNAKE_ENTITY, world, owner);
    }

    @Override
    public void tick() {
        super.tick();
        Entity target;
        if (!this.getWorld().isClient) {
            if ((target = this.getSavedTarget((ServerWorld) this.getWorld())) != null) {
                double f = target.getX() - this.getX();
                double g = (target.getBodyY(0.5) - (this.getBodyY(0.5D))) / 4f;
                double h = target.getZ() - this.getZ();
                this.setVelocity(f, g, h, 0.3f, 1f);
            }
            if (this.age % 4 == 0) {
                for (Entity entity : this.getWorld().getOtherEntities(this, this.getBoundingBox())) {
                    if (entity instanceof LivingEntity living && !this.isOwner(living) && !(entity instanceof NightProwler)) {
                        if (living.damage(this.getWorld().getDamageSources().mobProjectile(this, (LivingEntity) this.getOwner()), (float) this.getDamage())) {
                            living.addVelocity(0, 1.5f, 0);
                            if (living instanceof PlayerEntity) {
                                this.hasHitPlayer = true;
                            }
                        }
                    }
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                ParticleHandler.particleOutburstMap(this.getWorld(), 250, this.getX(), this.getY(), this.getZ(), ParticleEvents.BLACKFLAME_SNAKE_PARTICLE_MAP, 1f);
            }
        }
        if (this.age > 100 || this.hasHitPlayer) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2f, true, World.ExplosionSourceType.TNT);
            this.discard();
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TARGET_UUID, Optional.empty());
    }

    public void setTargetUuid(@Nullable UUID uuid) {
        this.dataTracker.set(TARGET_UUID, Optional.ofNullable(uuid));
    }

    public UUID getTargetUuid() {
        return this.dataTracker.get(TARGET_UUID).orElse(null);
    }

    @Nullable
    public Entity getSavedTarget(ServerWorld world) {
        return world.getEntity(this.getTargetUuid());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID uUID = null;
        if (nbt.containsUuid("TargetUuid")) {
            uUID = nbt.getUuid("TargetUuid");
        }
        if (uUID != null) {
            try {
                this.setTargetUuid(uUID);
            } catch (Throwable ignored) {}
        }
        if (nbt.contains("HasHitPlayer")) {
            this.hasHitPlayer = nbt.getBoolean("HasHitPlayer");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getTargetUuid() != null) {
            nbt.putUuid("TargetUuid", this.getTargetUuid());
        }
        nbt.putBoolean("HasHitPlayer", this.hasHitPlayer);
    }
}
