package net.soulsweaponry.items;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.projectile.NightsEdge;
import net.soulsweaponry.registry.EffectRegistry;
import net.soulsweaponry.registry.EntityRegistry;
import net.soulsweaponry.util.IKeybindAbility;
import net.soulsweaponry.util.WeaponUtil;

public class NightsEdgeItem extends ChargeToUseItem implements IKeybindAbility {

    public NightsEdgeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, ConfigConstructor.nights_edge_weapon_damage, ConfigConstructor.nights_edge_weapon_attack_speed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.NIGHTS_EDGE, WeaponUtil.TooltipAbilities.BLIGHT);
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_nights_edge;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.isDisabled(stack)) {
            return super.postHit(stack, target, attacker);
        }
        if (target.hasStatusEffect(EffectRegistry.BLIGHT)) {
            int amp = target.getStatusEffect(EffectRegistry.BLIGHT).getAmplifier();
            target.addStatusEffect(new StatusEffectInstance(EffectRegistry.BLIGHT, 60, amp + 1));
            if (amp >= 10) {
                target.addStatusEffect(new StatusEffectInstance(EffectRegistry.DECAY, 80, 0));
            }
        } else {
            target.addStatusEffect(new StatusEffectInstance(EffectRegistry.BLIGHT, 60, MathHelper.floor(WeaponUtil.getEnchantDamageBonus(stack)/2f)));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player && !player.getItemCooldownManager().isCoolingDown(this)) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i >= 10) {
                stack.damage(1, player, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(user.getActiveHand()));
                this.castSpell(player, world, stack, false);
                this.applyItemCooldown(player, this.getScaledCooldown(stack));
            }
        }
    }

    @Override
    public void useKeybindAbilityServer(ServerWorld world, ItemStack stack, PlayerEntity player) {
        if (!player.getItemCooldownManager().isCoolingDown(this)) {
            this.castSpell(player, world, stack, true);
            this.applyItemCooldown(player, this.getScaledCooldown(stack));
            stack.damage(1, player, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(player.getActiveHand()));
        }
    }

    protected int getScaledCooldown(ItemStack stack) {
        int base = ConfigConstructor.nights_edge_ability_cooldown;
        return Math.max(ConfigConstructor.nights_edge_ability_min_cooldown, base - this.getReduceCooldownEnchantLevel(stack) * 8);
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ConfigConstructor.nights_edge_enchant_reduces_cooldown;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return ConfigConstructor.nights_edge_enchant_reduces_cooldown_id;
    }

    protected void castSpell(PlayerEntity user, World world, ItemStack stack, boolean ripple) {
        Vec3d start = user.getPos();
        float r;
        double maxY = user.getY();
        double y = user.getY() + 1.0;
        float f = (float) Math.toRadians(user.getYaw() + 90);
        if (ripple) {
            float yaw;
            int i;
            for (int waves = 0; waves < 3 + MathHelper.floor(WeaponUtil.getEnchantDamageBonus(stack)/2f); waves++) {
                for (i = 0; i < 360; i += MathHelper.floor((80f) / (waves + 1f))) {
                    r = 1.5f + waves * 1.75f;
                    yaw = (float) (f + i * Math.PI / 180f);
                    double x0 = start.getX();
                    double z0 = start.getZ();
                    double x = x0 + r * Math.cos(i * Math.PI / 180);
                    double z = z0 + r * Math.sin(i * Math.PI / 180);
                    this.conjureFangs(user, world, stack, x, z, maxY, y, yaw, 3 * (waves + 1));
                }
            }
        } else {
            for (int i = 0; i < 10 + 2 * WeaponUtil.getEnchantDamageBonus(stack); ++i) {
                double h = 1.25 * (double)(i + 1);
                this.conjureFangs(user, world, stack, user.getX() + (double)MathHelper.cos(f) * h, user.getZ() + (double)MathHelper.sin(f) * h, maxY, y, f, i);
            }
        }
    }

    private void conjureFangs(PlayerEntity user, World world, ItemStack stack, double x, double z, double maxY, double y, float yaw, int warmup) {
        BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
        boolean bl = false;
        double d = 0.0;
        do {
            VoxelShape voxelShape;
            BlockPos blockPos2;
            if (!world.getBlockState(blockPos2 = blockPos.down()).isSideSolidFullSquare(world, blockPos2, Direction.UP)) continue;
            if (!world.isAir(blockPos) && !(voxelShape = world.getBlockState(blockPos).getCollisionShape(world, blockPos)).isEmpty()) {
                d = voxelShape.getMax(Direction.Axis.Y);
            }
            bl = true;
            break;
        } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
        if (bl) {
            NightsEdge edge = new NightsEdge(EntityRegistry.NIGHTS_EDGE, world);
            edge.setOwner(user);
            edge.setDamage(ConfigConstructor.nights_edge_ability_damage + 2 * WeaponUtil.getEnchantDamageBonus(stack));
            edge.setWarmup(warmup);
            edge.setYaw(yaw * 57.295776F);
            edge.setPos(x, (double)blockPos.getY() + d, z);
            world.spawnEntity(edge);
        }
    }

    @Override
    public void useKeybindAbilityClient(ClientWorld world, ItemStack stack, ClientPlayerEntity player) {
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_nights_edge;
    }
}