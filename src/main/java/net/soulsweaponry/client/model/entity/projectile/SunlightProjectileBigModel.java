package net.soulsweaponry.client.model.entity.projectile;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.entity.projectile.MoonlightProjectile;
import software.bernie.geckolib.model.GeoModel;

public class SunlightProjectileBigModel extends GeoModel<MoonlightProjectile> {

    @Override
    public Identifier getAnimationResource(MoonlightProjectile animatable) {
        return null;
    }

    @Override
    public Identifier getModelResource(MoonlightProjectile object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/moonlight_projectile_big.geo.json");
    }

    @Override
    public Identifier getTextureResource(MoonlightProjectile object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/entity/sunlight_projectile_big.png");
    }

}
