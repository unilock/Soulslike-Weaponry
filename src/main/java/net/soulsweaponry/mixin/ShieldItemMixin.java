package net.soulsweaponry.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.soulsweaponry.util.WeaponUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShieldItem.class)
public class ShieldItemMixin {

    @Inject(method = "appendTooltip", at = @At("TAIL"))
    protected void interceptTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context, CallbackInfo info) {
        if (Screen.hasShiftDown()) {
            WeaponUtil.addAbilityTooltip(WeaponUtil.TooltipAbilities.PARRY, stack, tooltip);
        } else {
            tooltip.add(Text.translatable("tooltip.soulsweapons.shift"));
        }
    }
}
