package net.soulsweaponry.client.model.entity.projectile;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.entity.projectile.GrowingFireball;
import software.bernie.geckolib.model.GeoModel;

public class GrowingFireballModel extends GeoModel<GrowingFireball> {

    @Override
    public Identifier getAnimationResource(GrowingFireball animatable) {
        return Identifier.of(SoulsWeaponry.ModId, "animations/entity/growing_fireball.animation.json");
    }

    @Override
    public Identifier getModelResource(GrowingFireball object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/entity/growing_fireball.geo.json");
    }

    @Override
    public Identifier getTextureResource(GrowingFireball object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/entity/growing_fireball.png");
    }
}
