package net.soulsweaponry.enchantments;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.soulsweaponry.config.ConfigConstructor;
import net.soulsweaponry.entitydata.IEntityDataSaver;
import net.soulsweaponry.entitydata.PostureData;
import net.soulsweaponry.items.IUltraHeavy;

public final class StaggerEnchantmentEffect implements EnchantmentEntityEffect {
	public static final StaggerEnchantmentEffect INSTANCE = new StaggerEnchantmentEffect();
	public static final MapCodec<StaggerEnchantmentEffect> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity target, Vec3d pos) {
		if (target instanceof LivingEntity living && !living.isDead()) {
			int postureLoss = MathHelper.floor(ConfigConstructor.stagger_enchant_posture_loss_on_player_modifier * ConfigConstructor.stagger_enchant_posture_loss_applied_per_level);
			if (context.stack().getItem() instanceof IUltraHeavy heavy && heavy.isHeavy()) {
				postureLoss = MathHelper.floor(postureLoss * ConfigConstructor.ultra_heavy_posture_loss_modifier_when_stagger_enchant);
			}
			postureLoss *= level;
			PostureData.addPosture((IEntityDataSaver) living, postureLoss);
		}
	}

	@Override
	public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
		return CODEC;
	}
}
