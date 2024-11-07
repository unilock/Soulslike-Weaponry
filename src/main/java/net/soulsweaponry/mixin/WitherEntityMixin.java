package net.soulsweaponry.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.soulsweaponry.registry.ItemRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {
    
    @Inject(at = @At("TAIL"), method = "dropEquipment")
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo info) {
        WitherEntity wither = ((WitherEntity)(Object)this);
        ItemEntity[] drops = {wither.dropItem(ItemRegistry.LORD_SOUL_VOID), wither.dropItem(ItemRegistry.SHARD_OF_UNCERTAINTY)};
        for (ItemEntity entity : drops) {
            if (entity != null) {
                entity.setCovetedItem();
            }
        }
    }
}
