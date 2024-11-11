package net.soulsweaponry.items;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.soulsweaponry.util.WeaponUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ModdedAxe extends AxeItem implements IConfigDisable, ICooldownItem {

    protected final List<WeaponUtil.TooltipAbilities> tooltipAbilities = new ArrayList<>();

    public ModdedAxe(ToolMaterial material, float attackDamage, float ingameAttackSpeed, Settings settings) {
        super(material, attackDamage, - (4f - ingameAttackSpeed), settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        if (this.isDisabled(stack)) {
            tooltip.add(Text.translatableWithFallback("tooltip.soulsweapons.disabled","Disabled"));
        }
        if (Screen.hasShiftDown()) {
            for (WeaponUtil.TooltipAbilities ability : this.getTooltipAbilities()) {
                WeaponUtil.addAbilityTooltip(ability, stack, tooltip);
            }
            tooltip.addAll(Arrays.asList(this.getAdditionalTooltips()));
        } else {
            tooltip.add(Text.translatable("tooltip.soulsweapons.shift"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    public List<WeaponUtil.TooltipAbilities> getTooltipAbilities() {
        return this.tooltipAbilities;
    }

    public void addTooltipAbility(WeaponUtil.TooltipAbilities... abilities) {
        Collections.addAll(this.tooltipAbilities, abilities);
    }

    public Text[] getAdditionalTooltips() {
        return new Text[0];
    }

    @Override
    public abstract boolean isFireproof();
}