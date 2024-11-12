package net.soulsweaponry.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entity.projectile.DragonslayerSwordspearEntity;
import net.soulsweaponry.particles.ParticleEvents;
import net.soulsweaponry.particles.ParticleHandler;
import net.soulsweaponry.util.WeaponUtil;

import java.util.List;

public class DragonslayerSwordspear extends ChargeToUseItem {

    private static final String RAINING = "raining_id";

    public DragonslayerSwordspear(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, ConfigConstructor.dragonslayer_swordspear_damage, ConfigConstructor.dragonslayer_swordspear_attack_speed, settings);
        this.addTooltipAbility(WeaponUtil.TooltipAbilities.LIGHTNING_CALL, WeaponUtil.TooltipAbilities.INFINITY, WeaponUtil.TooltipAbilities.THROW_LIGHTNING, WeaponUtil.TooltipAbilities.STORM_STOMP, WeaponUtil.TooltipAbilities.WEATHERBORN);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i >= 10) {
                if (stack != user.getOffHandStack()) {
                    stack.damage(1, (LivingEntity)playerEntity, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(user.getActiveHand()));
                    DragonslayerSwordspearEntity entity = new DragonslayerSwordspearEntity(world, playerEntity, stack);
                    entity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 5.0F, 1.0F);
                    entity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    world.spawnEntity(entity);
                    world.playSoundFromEntity(null, entity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    this.applyItemCooldown(playerEntity, this.getScaledCooldownThrow(world, stack));
                } else {
                    stack.damage(3, (LivingEntity)playerEntity, (p_220045_0_) -> p_220045_0_.sendToolBreakStatus(user.getActiveHand()));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 5));
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0));
                    Box chunkBox = new Box(user.getX() - 10, user.getY() - 5, user.getZ() - 10, user.getX() + 10, user.getY() + 5, user.getZ() + 10);
                    List<Entity> nearbyEntities = world.getOtherEntities(user, chunkBox);
                    //Entity["EntityKey"/number?, l = "ClientLevel", x, y, z] and so on... Includes items too!
                    for (Entity nearbyEntity : nearbyEntities) {
                        if (nearbyEntity instanceof LivingEntity target) {
                            if (nearbyEntity instanceof TameableEntity tamed && tamed.isTamed()) {
                                continue;
                            }
                            if (world.isSkyVisible(target.getBlockPos())) {
                                for (i = 0; i < ConfigConstructor.dragonslayer_swordspear_lightning_amount; i++) {
                                    LightningEntity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                                    entity.setPos(target.getX(), target.getY(), target.getZ());
                                    world.spawnEntity(entity);
                                }
                            } else {
                                double x = target.getX() - user.getX();
                                double z = target.getX() - user.getX();
                                target.takeKnockback(5F, -x, -z);
                                target.damage(world.getDamageSources().mobAttack(user), ConfigConstructor.dragonslayer_swordspear_ability_damage);
                                if (!world.isClient) {
                                    ParticleHandler.particleSphereList(world, 20, target.getX(), target.getY(), target.getZ(), ParticleEvents.DARK_EXPLOSION_LIST, 0.3f);
                                }
                            }
                            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
                        }
                    }
                    this.applyItemCooldown(playerEntity, this.getScaledCooldownAbility(world, stack));
                }
            }
        }
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ConfigConstructor.dragonslayer_swordspear_enchant_reduces_cooldown;
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        return ConfigConstructor.dragonslayer_swordspear_enchant_reduces_cooldown_id;
    }

    protected int getScaledCooldownAbility(World world, ItemStack stack) {
        int base = ConfigConstructor.dragonslayer_swordspear_ability_cooldown;
        return Math.max(ConfigConstructor.dragonslayer_swordspear_ability_min_cooldown, base - this.getReduceCooldownEnchantLevel(stack) * 20 / (world.isRaining() ? 2 : 1));
    }

    protected int getScaledCooldownThrow(World world, ItemStack stack) {
        int base = ConfigConstructor.dragonslayer_swordspear_throw_cooldown;
        return Math.max(ConfigConstructor.dragonslayer_swordspear_throw_min_cooldown, base - this.getReduceCooldownEnchantLevel(stack) * 10 / (world.isRaining() ? 2 : 1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!this.isDisabled(stack)) {
            this.updateRaining(world, stack);
        }
    }

    private void updateRaining(World world, ItemStack stack) {
        if (stack.hasNbt()) {
            stack.getNbt().putBoolean(RAINING, world.isRaining());
        }
    }

    private boolean getRaining(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(RAINING) && !this.isDisabled(stack)) {
            return stack.getNbt().getBoolean(RAINING);
        } else {
            return false;
        }
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
        if (slot == EquipmentSlot.MAINHAND && this.getRaining(stack)) {
            Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", ConfigConstructor.dragonslayer_swordspear_damage + ConfigConstructor.dragonslayer_swordspear_rain_bonus_damage - 1, EntityAttributeModifier.Operation.ADD_VALUE));
            builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", - (4f - ConfigConstructor.dragonslayer_swordspear_rain_total_attack_speed), EntityAttributeModifier.Operation.ADD_VALUE));
            attributeModifiers = builder.build();
            return attributeModifiers;
        } else {
            return super.getAttributeModifiers(slot);
        }
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_dragonslayer_swordspear;
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        return ConfigConstructor.disable_use_dragonslayer_swordspear;
    }
}