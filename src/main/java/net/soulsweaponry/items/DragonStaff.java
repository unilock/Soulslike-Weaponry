package net.soulsweaponry.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.projectile.DragonStaffProjectile;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.ParticleRegistry;
import net.soulsweaponry.util.CustomDamageSource;
import net.soulsweaponry.util.WeaponUtil;

public class DragonStaff extends ModdedSword {

    public DragonStaff(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, ConfigConstructor.dragon_staff_damage, ConfigConstructor.dragon_staff_attack_speed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.DRAGON_STAFF, WeaponUtil.TooltipAbilities.VENGEFUL_FOG);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user.isSneaking() && remainingUseTicks > 0) {
            Vec3d pov = user.getRotationVector();
            Vec3d particleSpawn = pov.multiply(1);
            Vec3d area = pov.multiply(10).add(user.getPos());
            Vec3i on = new Vec3i((int) area.getX(), (int) area.getY(), (int) area.getZ());
            for (Entity entity : world.getOtherEntities(user, new Box(user.getPos().add(0, 2, 0), new BlockPos(on).toCenterPos()))) {
                if (entity instanceof LivingEntity) {
                    entity.damage(CustomDamageSource.create(world, CustomDamageSource.DRAGON_MIST, user), 2);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(EffectRegistry.HALLOWED_DRAGON_MIST, 100, ConfigConstructor.dragon_staff_aura_strength));
                }
            }
            if (world.isClient) {
                for (int k = 0; k < 10; k++) {
                    world.addParticle(ParticleRegistry.PURPLE_FLAME, true, particleSpawn.add(user.getPos()).getX(), particleSpawn.add(user.getPos()).getY() + 1.5F, particleSpawn.add(user.getPos()).getZ(), pov.x + user.getRandom().nextDouble() - .25, pov.y + user.getRandom().nextDouble() - .5, pov.z + user.getRandom().nextDouble() - .25);
                    world.addParticle(ParticleTypes.DRAGON_BREATH, true, particleSpawn.add(user.getPos()).getX(), particleSpawn.add(user.getPos()).getY() + 1.5F, particleSpawn.add(user.getPos()).getZ(), pov.x + user.getRandom().nextDouble() - .25, pov.y + user.getRandom().nextDouble() - .5, pov.z + user.getRandom().nextDouble() - .25);
                }
            }
        } else {
            user.stopUsingItem();
            super.usageTick(world, user, stack, remainingUseTicks);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.stop(user, stack);
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        this.stop(user, stack);
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    private void stop(LivingEntity user, ItemStack stack) {
        if (user instanceof PlayerEntity player) {
            this.applyItemCooldown(player, this.getCooldown(stack));
        }
        stack.damage(3, user, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(user.getActiveHand()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (this.isDisabled(itemStack)) {
            this.notifyDisabled(user);
            return TypedActionResult.fail(itemStack);
        }
        if (!user.isSneaking()) {
            if (!user.isCreative()) user.getItemCooldownManager().set(this, this.getCooldown(itemStack)*2);
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.NEUTRAL, 0.5f, 2/(world.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!world.isClient) {
                DragonStaffProjectile fireball = new DragonStaffProjectile(world, user, itemStack);
                fireball.setPos(user.getX(), user.getY() + 1.0f, user.getZ());
                fireball.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 0f);
                world.spawnEntity(fireball);
                itemStack.damage(1, user, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(hand));
            }
            return TypedActionResult.success(itemStack, world.isClient());
        } else {
            if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
                return TypedActionResult.fail(itemStack);
            } else {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return ConfigConstructor.dragon_staff_use_time + WeaponUtil.getEnchantDamageBonus(stack) * 20;
    }

    private int getCooldown(ItemStack stack) {
        return Math.max(ConfigConstructor.dragon_staff_min_cooldown, ConfigConstructor.dragon_staff_cooldown - this.getReduceCooldownEnchantLevel(stack) * 10);
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_dragon_staff;
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_dragon_staff;
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ConfigConstructor.dragon_staff_enchant_reduces_cooldown;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return ConfigConstructor.dragon_staff_enchant_reduces_cooldown_id;
    }
}