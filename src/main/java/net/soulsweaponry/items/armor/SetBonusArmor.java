package net.soulsweaponry.items.armor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class SetBonusArmor extends ModdedArmor {

    public SetBonusArmor(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!this.isDisabled(stack) && entity instanceof PlayerEntity player) {
            if (this.hasFullSet(player)) {
                for (StatusEffectInstance instance : this.getFullSetEffects()) {
                    player.addStatusEffect(instance);
                }
                this.tickAdditionalSetEffects(stack, player);
            }
        }
    }

    protected boolean hasFullSet(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack chestplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);
        boolean bootsSlot = !boots.isEmpty() && boots.getItem() == this.getMatchingBoots();
        boolean leggingsSlot = !leggings.isEmpty() && leggings.getItem() == this.getMatchingLegs();
        boolean chestSlot = !chestplate.isEmpty() && chestplate.getItem() == this.getMatchingChest();
        boolean helmetSlot = !helmet.isEmpty() && helmet.getItem() == this.getMatchingHead();
        return bootsSlot && leggingsSlot && chestSlot && helmetSlot;
    }

    protected abstract void tickAdditionalSetEffects(ItemStack stack, PlayerEntity player);
    protected abstract Item getMatchingBoots();
    protected abstract Item getMatchingLegs();
    protected abstract Item getMatchingChest();
    protected abstract Item getMatchingHead();
    protected abstract StatusEffectInstance[] getFullSetEffects();
    protected abstract Text[] getCustomTooltips();

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.soulsweapons.armor.set_bonus").formatted(Formatting.AQUA));
            for (StatusEffectInstance effect : this.getFullSetEffects()) {
                tooltip.add(Text.translatable("tooltip.soulsweapons.armor.set_bonus.gain_effects").append(effect.getEffectType().getName()).formatted(Formatting.GRAY));
            }
            tooltip.addAll(Arrays.asList(this.getCustomTooltips()));
        } else {
            tooltip.add(Text.translatable("tooltip.soulsweapons.shift"));
        }
    }
}