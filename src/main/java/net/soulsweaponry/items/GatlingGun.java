package net.soulsweaponry.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.registry.EnchantRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import net.soulsweaponry.registry.SoundRegistry;

public class GatlingGun extends GunItem {

    public GatlingGun(Settings settings) {
        super(settings);
    }

    @Override
    public int getPostureLoss(ItemStack stack) {
        int lvl = EnchantmentHelper.getLevel(EnchantRegistry.VISCERAL, stack);
        return ConfigConstructor.gatling_gun_posture_loss + lvl;
    }

    @Override
    public float getBulletDamage(ItemStack stack) {
        return ConfigConstructor.gatling_gun_damage;
    }

    @Override
    public float getBulletVelocity(ItemStack stack) {
        return ConfigConstructor.gatling_gun_velocity;
    }

    @Override
    public float getBulletDivergence(ItemStack stack) {
        return ConfigConstructor.gatling_gun_divergence;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return ConfigConstructor.gatling_gun_cooldown - 3 * this.getReducedCooldown(stack) + EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) * 30;
    }

    @Override
    public int bulletsNeeded() {
        return ConfigConstructor.gatling_gun_bullets_needed;
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_gatling_gun;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (remainingUseTicks < this.getMaxUseTime(stack, user) - 15 && remainingUseTicks % 4 == 0) {
            if (user instanceof PlayerEntity playerEntity) {
                boolean bl = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
                ItemStack itemStack = playerEntity.getProjectileType(stack);
                if (!itemStack.isEmpty() || bl) {
                    if (itemStack.isEmpty()) {
                        itemStack = new ItemStack(ItemRegistry.SILVER_BULLET);
                    }
                    boolean bl2 = bl && itemStack.isOf(ItemRegistry.SILVER_BULLET);
                    Vec3d pov = playerEntity.getRotationVector();
                    Vec3d particleBox = pov.multiply(1).add(playerEntity.getPos());
                    if (world.isClient) {
                        for (int k = 0; k < 8 + EnchantmentHelper.getLevel(EnchantRegistry.FAST_HANDS, stack); k++) {
                            world.addParticle(ParticleTypes.FLAME, true, particleBox.x, particleBox.y + 1.5F, particleBox.z, pov.x + user.getRandom().nextDouble() - .25, pov.y + user.getRandom().nextDouble() - .5, pov.z + user.getRandom().nextDouble() - .25);
                            world.addParticle(ParticleTypes.SMOKE, true, particleBox.x, particleBox.y + 1.5F, particleBox.z, pov.x + user.getRandom().nextDouble() - .25, pov.y + user.getRandom().nextDouble() - .5, pov.z + user.getRandom().nextDouble() - .25);
                        }
                    }

                    PersistentProjectileEntity entity = this.createSilverBulletEntity(world, user, stack);
                    world.spawnEntity(entity);
                    world.playSound(playerEntity, user.getBlockPos(), SoundRegistry.GATLING_GUN_BARRAGE_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    if (!bl2 && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(this.bulletsNeeded());
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        } else if (-remainingUseTicks > this.getMaxUseTime(stack, user)) {
            user.stopUsingItem();
            super.usageTick(world, user, stack, remainingUseTicks);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.stop(user, stack, world);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        int lvl = EnchantmentHelper.getLevel(EnchantRegistry.FAST_HANDS, stack);
        return ConfigConstructor.gatling_gun_max_time * (lvl == 0 ? 1 : lvl);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        this.stop(user, stack, world);
    }

    private void stop(LivingEntity user, ItemStack stack, World world) {
        world.playSound(null, user.getBlockPos(), SoundRegistry.GATLING_GUN_STOP_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, this.getCooldown(stack));
            stack.damage(5, player, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(user.getActiveHand()));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (this.isDisabled(user.getStackInHand(hand))) {
            this.notifyDisabled(user);
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(user, user.getBlockPos(), SoundRegistry.GATLING_GUN_STARTUP_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        }
        else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_gatling_gun;
    }
}