package net.soulsweaponry.client.model.entity.projectile;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.entity.projectile.NightSkull;
import software.bernie.geckolib.model.GeoModel;

public class NightSkullModel extends GeoModel<NightSkull> {

    @Override
    public Identifier getAnimationResource(NightSkull animatable) {
        return Identifier.of(SoulsWeaponry.ModId, "animations/entity/night_skull.animation.json");
    }

    @Override
    public Identifier getModelResource(NightSkull object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/entity/night_skull.geo.json");

    }

    @Override
    public Identifier getTextureResource(NightSkull object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/entity/night_skull.png");
    }
}
