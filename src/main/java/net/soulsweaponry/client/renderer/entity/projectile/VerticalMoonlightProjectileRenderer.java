package net.soulsweaponry.client.renderer.entity.projectile;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.soulsweaponry.client.model.entity.projectile.VerticalMoonlightProjectileModel;
import net.soulsweaponry.entity.projectile.MoonlightProjectile;

public class VerticalMoonlightProjectileRenderer extends GeoProjectileRenderer<MoonlightProjectile> {

    public VerticalMoonlightProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new VerticalMoonlightProjectileModel());
    }
}
