package net.soulsweaponry.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.soulsweaponry.registry.EntityRegistry;

public class ChargedArrow extends PersistentProjectileEntity {

    private boolean scaleDamageHp;

    public ChargedArrow(EntityType<? extends ChargedArrow> entityType, World world) {
        super(entityType, world, Items.ARROW.getDefaultStack());
        this.scaleDamageHp = false;
    }

    public ChargedArrow(EntityType<? extends ChargedArrow> entityType, World world, ItemStack stack) {
        super(entityType, world, stack);
        this.scaleDamageHp = false;
    }

    public ChargedArrow(World world, double x, double y, double z, boolean scaleDamageHp, ItemStack stack) {
        super(EntityRegistry.CHARGED_ARROW_ENTITY_TYPE, x, y, z, world, stack);
        this.scaleDamageHp = scaleDamageHp;
    }

    public ChargedArrow(World world, LivingEntity owner, ItemStack stack, boolean scaleDamageHp) {
        super(EntityRegistry.CHARGED_ARROW_ENTITY_TYPE, owner, world, stack);
        this.scaleDamageHp = scaleDamageHp;
    }
  
    public void tick() {
        if (!this.inGround) {
            Vec3d vec3d = this.getVelocity();
            double e = vec3d.x;
            double f = vec3d.y;
            double g = vec3d.z;
            for (int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(this.getParticleType(), this.getX() + e * (double)i / 4.0D, this.getY() + f * (double)i / 4.0D, this.getZ() + g * (double)i / 4.0D, -e, -f + 0.2D, -g);
            }
        }
        super.tick();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("scaleDmg")) {
            this.scaleDamageHp = nbt.getBoolean("scaleDmg");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("scaleDmg", this.scaleDamageHp);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.scaleDamageHp && entityHitResult.getEntity() instanceof LivingEntity target) {
            float percentMissingHp = 100f - (target.getHealth() / target.getMaxHealth()) * 100f;
            float increase = 1f + (percentMissingHp / 100f);
            this.setDamage(this.getDamage() * increase);
        }
        super.onEntityHit(entityHitResult);
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.GLOW;
    }
}
