package net.soulsweaponry.client.model.entity.projectile;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.entity.projectile.NightsEdge;
import software.bernie.geckolib.model.GeoModel;

public class NightsEdgeModel extends GeoModel<NightsEdge> {

    @Override
    public Identifier getAnimationResource(NightsEdge animatable) {
        return Identifier.of(SoulsWeaponry.ModId, "animations/entity/nights_edge.animation.json");
    }

    @Override
    public Identifier getModelResource(NightsEdge object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/entity/nights_edge.geo.json");
    }

    @Override
    public Identifier getTextureResource(NightsEdge object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/entity/nights_edge.png");
    }
}
