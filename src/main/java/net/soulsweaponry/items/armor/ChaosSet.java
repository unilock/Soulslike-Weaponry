package net.soulsweaponry.items.armor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.soulsweaponry.blocks.WitheredBlock;
import net.soulsweaponry.blocks.WitheredFlower;
import net.soulsweaponry.blocks.WitheredGrass;
import net.soulsweaponry.blocks.WitheredTallFlower;
import net.soulsweaponry.blocks.WitheredTallGrass;
import net.soulsweaponry.client.renderer.armor.ChaosArmorRenderer;
import net.soulsweaponry.client.renderer.armor.ChaosSetRenderer;
import net.soulsweaponry.client.renderer.armor.EChaosArmorRenderer;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.items.ICooldownItem;
import net.soulsweaponry.particles.ParticleHandler;
import net.soulsweaponry.registry.BlockRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ChaosSet extends ModdedArmor implements GeoItem, ICooldownItem {

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final HashMap<Block, WitheredBlock> turnableBlocks = new HashMap<>();
    private final HashMap<Block, WitheredGrass> turnableGrass = new HashMap<>();
    private final HashMap<Block, WitheredTallGrass> turnableTallPlant = new HashMap<>();
    /**
     * Will contain harmful effects as the key, and the opposite beneficial effect as value
     */
    private static final HashMap<StatusEffect, StatusEffect> FLIPPABLE_EFFECTS = new HashMap<>();
    
    public ChaosSet(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
        this.turnableBlocks.put(Blocks.GRASS_BLOCK, BlockRegistry.WITHERED_GRASS_BLOCK);
        this.turnableBlocks.put(Blocks.DIRT, BlockRegistry.WITHERED_DIRT);

        this.turnableGrass.put(Blocks.SHORT_GRASS, BlockRegistry.WITHERED_GRASS);
        this.turnableGrass.put(Blocks.FERN, BlockRegistry.WITHERED_FERN);
        this.turnableGrass.put(Blocks.SWEET_BERRY_BUSH, BlockRegistry.WITHERED_BERRY_BUSH);

        this.turnableTallPlant.put(Blocks.TALL_GRASS, BlockRegistry.WITHERED_TALL_GRASS);
        this.turnableTallPlant.put(Blocks.LARGE_FERN, BlockRegistry.WITHERED_LARGE_FERN);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof PlayerEntity player) {
            if (this.isHelmetEquipped(player)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 40, 0));
                if (!player.getItemCooldownManager().isCoolingDown(ItemRegistry.CHAOS_CROWN) && !player.getItemCooldownManager().isCoolingDown(ItemRegistry.CHAOS_HELMET)) {
                    this.flipEffects(player);
                }
            }
            if (this.isRobesEquipped(player)) {
                this.turnBlocks(player, world, player.getBlockPos(), 0);
                if (player.age % 40 == 0) {
                    for (LivingEntity target : world.getNonSpectatingEntities(LivingEntity.class, player.getBoundingBox().expand(3D))) {
                        if (!(target instanceof PlayerEntity) && target != player) {
                            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1));
                        }
                    }
                }
            }
            if (this.isChestActive(player)) {
                ItemStack chest = player.getInventory().getArmorStack(2);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 1));
                if (!world.isClient && !player.getItemCooldownManager().isCoolingDown(chest.getItem()) && player.getAttacker() != null) {
                    this.shockwave(world, player);
                }
            }
        }
    }

    public void turnBlocks(LivingEntity entity, World world, BlockPos blockPos, int bonusRadius) {
        if (!entity.isOnGround()) {
            return;
        }
        int f = Math.min(16, 3 + bonusRadius);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-f, -1, -f), blockPos.add(f, -1, f))) {
            if (!world.getBlockState(blockPos2).isAir()) {
                for (Block turnBlock : this.turnableBlocks.keySet()) {
                    if (world.getBlockState(blockPos2).getBlock() == turnBlock) {
                        BlockState blockState = this.turnableBlocks.get(turnBlock).getDefaultState();
                        if (!blockPos2.isWithinDistance(entity.getPos(), f)) continue;
                        mutable.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
                        BlockState blockState2 = world.getBlockState(mutable);
                        if (blockState2.isIn(BlockTags.SMALL_FLOWERS)) world.setBlockState(mutable, BlockRegistry.HYDRANGEA.getDefaultState().with(WitheredFlower.CANNOT_TURN, false));
                        for (Block turnGrass : this.turnableGrass.keySet()) if (blockState2.isOf(turnGrass)) world.setBlockState(mutable, this.turnableGrass.get(turnGrass).getDefaultState());
                        for (Block turnTallPlant : this.turnableTallPlant.keySet()) if (blockState2.isOf(turnTallPlant)) {
                            world.removeBlock(mutable, false);
                            TallPlantBlock.placeAt(world, this.turnableTallPlant.get(turnTallPlant).getDefaultState(), mutable, 2);
                        }
                        if (blockState2.isIn(BlockTags.TALL_FLOWERS)) {
                            world.removeBlock(mutable, false);
                            TallPlantBlock.placeAt(world, BlockRegistry.OLEANDER.getDefaultState().with(WitheredTallFlower.CANNOT_TURN, false), mutable, 2);
                        }
                        world.setBlockState(blockPos2, blockState);
                        world.scheduleBlockTick(blockPos2, this.turnableBlocks.get(turnBlock), MathHelper.nextInt(entity.getRandom(), 50, 90));
                    } else if (world.getBlockState(blockPos2).getBlock() == this.turnableBlocks.get(turnBlock)) {
                        WitheredBlock block = (WitheredBlock) world.getBlockState(blockPos2).getBlock();
                        block.resetAge(world.getBlockState(blockPos2), world, blockPos2);
                    }
                }
            }
        }
    }

    static {
        FLIPPABLE_EFFECTS.put(StatusEffects.SLOWNESS, StatusEffects.SPEED);
        FLIPPABLE_EFFECTS.put(StatusEffects.MINING_FATIGUE, StatusEffects.HASTE);
        FLIPPABLE_EFFECTS.put(StatusEffects.WEAKNESS, StatusEffects.STRENGTH);
        FLIPPABLE_EFFECTS.put(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION);
        FLIPPABLE_EFFECTS.put(StatusEffects.HUNGER, StatusEffects.SATURATION);
        FLIPPABLE_EFFECTS.put(StatusEffects.LEVITATION, StatusEffects.SLOW_FALLING);
    }

    private void flipEffects(PlayerEntity player) {
        List<StatusEffectInstance> statusEffectsCopy = new ArrayList<>(player.getStatusEffects());
        List<StatusEffect> effectsToRemove = new ArrayList<>();
        boolean triggered = false;
        for (StatusEffectInstance instance : statusEffectsCopy) {
            StatusEffect effect = instance.getEffectType();
            if (effect.getCategory() == StatusEffectCategory.HARMFUL) {
                int duration = (int) (instance.getDuration() / 3f);
                int amplifier = (int) (instance.getAmplifier() / 2f);
                StatusEffect newEffect = StatusEffects.REGENERATION;
                for (StatusEffect harmful : FLIPPABLE_EFFECTS.keySet()) {
                    if (effect.equals(harmful)) {
                        newEffect = FLIPPABLE_EFFECTS.get(harmful);
                        break;
                    }
                }
                effectsToRemove.add(effect);
                triggered = true;
                player.addStatusEffect(new StatusEffectInstance(newEffect, duration, amplifier));
            }
        }
        for (StatusEffect effectToRemove : effectsToRemove) {
            player.removeStatusEffect(effectToRemove);
        }
        if (triggered && !player.isCreative()) {
            player.getItemCooldownManager().set(ItemRegistry.CHAOS_CROWN, Math.max(ConfigConstructor.chaos_crown_flip_effect_min_cooldown, ConfigConstructor.chaos_crown_flip_effect_cooldown
                    - this.getReduceCooldownEnchantLevel(player.getEquippedStack(EquipmentSlot.HEAD)) * 40));
            player.getItemCooldownManager().set(ItemRegistry.CHAOS_HELMET, Math.max(ConfigConstructor.chaos_crown_flip_effect_min_cooldown, ConfigConstructor.chaos_crown_flip_effect_cooldown
                    - this.getReduceCooldownEnchantLevel(player.getEquippedStack(EquipmentSlot.HEAD)) * 40));
        }
    }

    private void shockwave(World world, PlayerEntity player) {
        float i = ConfigConstructor.arkenplate_shockwave_knockback;
        ItemStack stack = player.getInventory().getArmorStack(2);
        if (stack == null) return;
        i += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
        ParticleHandler.singleParticle(world, ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getBodyY(0.5D), player.getZ(), 0, 0, 0);
        for (Entity entity : world.getOtherEntities(player, player.getBoundingBox().expand(5D))) {
            if (entity instanceof LivingEntity target && !target.isTeammate(player)) {
                if (this.equals(ItemRegistry.ENHANCED_ARKENPLATE)) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 160, 2));
                }
                target.damage(player.getDamageSources().mobAttack(player), ConfigConstructor.arkenplate_shockwave_damage);
                double x = player.getX() - target.getX();
                double z = player.getZ() - target.getZ();
                target.takeKnockback(i * 0.5f, x, z);
            }
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
        if (!player.isCreative()) {
            player.getItemCooldownManager().set(stack.getItem(), Math.max(ConfigConstructor.arkenplate_shockwave_min_cooldown, ConfigConstructor.arkenplate_shockwave_cooldown
                    - this.getReduceCooldownEnchantLevel(stack) * 20));
        }
    }

    private boolean isHelmetEquipped(PlayerEntity player) {
        ItemStack helmet = player.getInventory().getArmorStack(3);
        return !helmet.isEmpty() && !this.isDisabled(helmet) && (helmet.isOf(ItemRegistry.CHAOS_CROWN) || helmet.isOf(ItemRegistry.CHAOS_HELMET));
    }

    private boolean isRobesEquipped(PlayerEntity player) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        return !chest.isEmpty() && !this.isDisabled(chest) && chest.isOf(ItemRegistry.CHAOS_ROBES);
    }

    private boolean isChestActive(PlayerEntity player) {
        ItemStack chest = player.getInventory().getArmorStack(2);
        return !chest.isEmpty() && !this.isDisabled(chest) && (chest.isOf(ItemRegistry.ARKENPLATE) || chest.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) && player.getHealth() < player.getMaxHealth()/2;
    }

	private PlayState predicate(AnimationState<?> event) {
        if (this == ItemRegistry.CHAOS_ROBES) {
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        if (this.equals(ItemRegistry.ENHANCED_ARKENPLATE)) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("soul_spin"));
        } else if (this.equals(ItemRegistry.ARKENPLATE)) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("no_souls"));
        }
		return PlayState.CONTINUE;
	}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (Screen.hasShiftDown()) {
            if (stack.isOf(ItemRegistry.CHAOS_CROWN) || stack.isOf(ItemRegistry.CHAOS_HELMET)) {
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_crown").formatted(Formatting.DARK_RED));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_crown_description_1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_crown_description_2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_crown_description_3").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.reversal").formatted(Formatting.DARK_AQUA));
                tooltip.add(Text.translatable("tooltip.soulsweapons.reversal_1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.reversal_2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.reversal_3").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.reversal_4").formatted(Formatting.DARK_GRAY));
                if (stack.isOf(ItemRegistry.CHAOS_CROWN)) {
                    if (Screen.hasControlDown()) {
                        for (int i = 1; i <= 4; i++) {
                            tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_crown_lore_" + i).formatted(Formatting.DARK_GRAY));
                        }
                    } else {
                        tooltip.add(Text.translatable("tooltip.soulsweapons.control"));
                    }
                } else {
                    if (Screen.hasControlDown()) {
                        for (int i = 1; i <= 4; i++) {
                            tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_helm_lore_" + i).formatted(Formatting.DARK_GRAY));
                        }
                    } else {
                        tooltip.add(Text.translatable("tooltip.soulsweapons.control"));
                    }
                }
            } else if (stack.isOf(ItemRegistry.CHAOS_ROBES)) {
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes").formatted(Formatting.WHITE));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes_description_1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes_description_2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes_description_3").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes_description_4").formatted(Formatting.GRAY));
                if (Screen.hasControlDown()) {
                    for (int i = 1; i <= 4; i++) {
                        tooltip.add(Text.translatable("tooltip.soulsweapons.chaos_robes_lore_" + i).formatted(Formatting.DARK_GRAY));
                    }
                } else {
                    tooltip.add(Text.translatable("tooltip.soulsweapons.control"));
                }
            } else if (stack.isOf(ItemRegistry.ARKENPLATE) || stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate").formatted(Formatting.AQUA));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate_description_1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate_description_2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.aftershock").formatted(Formatting.DARK_GREEN));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.aftershock.1").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.aftershock.2").formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.aftershock.3").formatted(Formatting.GRAY));
                if (stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
                    tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.aftershock.4").formatted(Formatting.GRAY));
                    tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.mirror").formatted(Formatting.DARK_PURPLE));
                    tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.mirror.1").formatted(Formatting.GRAY).append(Text.literal((int)(ConfigConstructor.arkenplate_mirror_trigger_percent * 100f) + "%").formatted(Formatting.RED)));
                    tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate.mirror.2").formatted(Formatting.GRAY));
                }
                if (!stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
                    if (Screen.hasControlDown()) {
                        for (int i = 1; i <= 4; i++) {
                            tooltip.add(Text.translatable("tooltip.soulsweapons.arkenplate_lore_" + i).formatted(Formatting.DARK_GRAY));
                        }
                    } else {
                        tooltip.add(Text.translatable("tooltip.soulsweapons.control"));
                    }
                }
            }
        } else {
            tooltip.add(Text.translatable("tooltip.soulsweapons.shift"));
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> BipedEntityModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable BipedEntityModel<T> original) {
                if (this.renderer == null) {
                    if (itemStack.isOf(ItemRegistry.CHAOS_HELMET) || itemStack.isOf(ItemRegistry.ARKENPLATE)) {
                        this.renderer = new ChaosArmorRenderer();
                    } else if (itemStack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
                        this.renderer = new EChaosArmorRenderer();
                    } else {
                        this.renderer = new ChaosSetRenderer();
                    }
                }

                return this.renderer;
            }
        });
    }

    @Override
    public boolean isDisabled(ItemStack stack) {
        if (stack.isOf(ItemRegistry.CHAOS_CROWN) || stack.isOf(ItemRegistry.CHAOS_HELMET)) {
            return ConfigConstructor.disable_use_chaos_crown;
        } else if (stack.isOf(ItemRegistry.ARKENPLATE) || stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
            return ConfigConstructor.disable_use_arkenplate;
        } else if (stack.isOf(ItemRegistry.CHAOS_ROBES)) {
            return ConfigConstructor.disable_use_chaos_robes;
        }
        return false;
    }

    @Override
    public boolean isFireproof() {
        return ConfigConstructor.is_fireproof_chaos_set;
    }

    @Override
    public boolean canEnchantReduceCooldown(ItemStack stack) {
        return ((stack.isOf(ItemRegistry.CHAOS_CROWN) || stack.isOf(ItemRegistry.CHAOS_HELMET)) && ConfigConstructor.chaos_crown_flip_effect_enchant_reduces_cooldown)
                || ((stack.isOf(ItemRegistry.ARKENPLATE) || stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) && ConfigConstructor.arkenplate_shockwave_enchant_reduces_cooldown);
    }

    @Override
    public String getReduceCooldownEnchantId(ItemStack stack) {
        if (stack.isOf(ItemRegistry.CHAOS_CROWN) || stack.isOf(ItemRegistry.CHAOS_HELMET)) {
            return ConfigConstructor.chaos_crown_flip_effect_enchant_reduces_cooldown_id;
        } else if (stack.isOf(ItemRegistry.ARKENPLATE) || stack.isOf(ItemRegistry.ENHANCED_ARKENPLATE)) {
            return ConfigConstructor.arkenplate_shockwave_enchant_reduces_cooldown_id;
        }
        return null;
    }
}
