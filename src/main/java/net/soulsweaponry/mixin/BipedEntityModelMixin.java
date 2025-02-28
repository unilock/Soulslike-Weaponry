package net.soulsweaponry.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.soulsweaponry.client.model.entity.mobs.ScythePosing;
import net.soulsweaponry.entity.mobs.Remnant;
import net.soulsweaponry.entitydata.ParryData;
import net.soulsweaponry.registry.WeaponRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Unique
    private final Set<Item> customHoldItems = Set.of(
            WeaponRegistry.GUTS_SWORD,
            WeaponRegistry.KRAKEN_SLAYER_CROSSBOW
    );
    @Unique
    private float parryProgress;
    
    @Inject(at = @At("TAIL"), method = "positionRightArm")
    private void positionRightArm(T entity, CallbackInfo info) {
        var model = ((BipedEntityModel<?>)(Object)this);
        for (ItemStack stack : entity.getHandItems()) {
            if (customHoldItems.contains(stack.getItem())) {
                if (!FabricLoader.getInstance().isModLoaded("bettercombat")) {
                    if (stack.isOf(WeaponRegistry.GUTS_SWORD)) {
                        CrossbowPosing.hold(model.rightArm, model.leftArm, model.head, true);
                    } else if (!stack.isOf(WeaponRegistry.KRAKEN_SLAYER_CROSSBOW) && !stack.isOf(WeaponRegistry.FROSTMOURNE)) {
                        ScythePosing.hold(model.rightArm, model.leftArm, model.head, true);
                    }
                } else if (entity instanceof Remnant) {
                    CrossbowPosing.hold(model.rightArm, model.leftArm, model.head, true);
                }
                if (stack.isOf(WeaponRegistry.KRAKEN_SLAYER_CROSSBOW)) {
                    CrossbowPosing.hold(model.rightArm, model.leftArm, model.head, true);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "animateArms", cancellable = true)
    protected void animateArms(T entity, float animationProgress, CallbackInfo info) {
        var model = ((BipedEntityModel<?>)(Object)this);
        // Parry animation
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            int frames = ParryData.getParryFrames(abstractClientPlayerEntity);
            if (frames >= 1) {
                this.parryProgress = frames == 1 ? 0.1f : parryProgress;
                float added = (1f / (float) ParryData.MAX_PARRY_FRAMES) / 6f;
                this.parryProgress = Math.min(this.parryProgress + added, 1f);
                ModelPart modelPart = model.leftArm;
                float f = parryProgress;
                model.body.yaw = MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2)) * 0.2f;
                model.leftArm.pivotZ = -MathHelper.sin(model.body.yaw) * 5.0f;
                model.leftArm.pivotX = MathHelper.cos(model.body.yaw) * 5.0f;
                model.leftArm.yaw += model.body.yaw;
                model.leftArm.pitch += model.body.yaw;
                f = 1.0f - parryProgress;
                f *= f;
                f *= f;
                f = 1.0f - f;
                float g = MathHelper.sin(f * (float)Math.PI);
                float h = MathHelper.sin(parryProgress * (float)Math.PI) * -(model.head.pitch - 0.7f) * 0.75f;
                modelPart.pitch -= g * 1.2f + h;
                modelPart.yaw += model.body.yaw * 2.0f;
                modelPart.roll += MathHelper.sin(parryProgress * (float)Math.PI) * -0.8f; //0.4
            }
        }
        if (!FabricLoader.getInstance().isModLoaded("bettercombat")) {
            ItemStack stack = entity.getMainHandStack();
            if (model.handSwingProgress > 0.0f && customHoldItems.contains(stack.getItem())) {
                ScythePosing.meleeAttack(model.leftArm, model.rightArm, entity, entity.handSwingProgress, animationProgress);
                info.cancel();
            }
        }
    }
}
