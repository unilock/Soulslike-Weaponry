package net.soulsweaponry.client.model.item;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.items.Nightfall;
import software.bernie.geckolib.model.GeoModel;

public class NightfallModel extends GeoModel<Nightfall> {

    @Override
    public Identifier getAnimationResource(Nightfall animatable) {
        return null;
    }

    @Override
    public Identifier getModelResource(Nightfall object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/nightfall.geo.json");
    }

    @Override
    public Identifier getTextureResource(Nightfall object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/item/nightfall.png");
    }
    
}
