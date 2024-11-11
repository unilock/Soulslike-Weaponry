package net.soulsweaponry.client.model.armor;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.items.armor.WitheredArmor;
import software.bernie.geckolib.model.GeoModel;

public class WitheredArmorModel extends GeoModel<WitheredArmor> {

    @Override
    public Identifier getAnimationResource(WitheredArmor animatable) {
        return Identifier.of(SoulsWeaponry.ModId, "animations/withered_armor.animation.json");
    }

    @Override
    public Identifier getModelResource(WitheredArmor object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/withered_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WitheredArmor object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/armor/withered_armor.png");
    }
}