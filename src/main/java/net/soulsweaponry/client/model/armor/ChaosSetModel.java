package net.soulsweaponry.client.model.armor;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.items.armor.ChaosSet;
import software.bernie.geckolib.model.GeoModel;

public class ChaosSetModel extends GeoModel<ChaosSet> {

    @Override
    public Identifier getAnimationResource(ChaosSet animatable) {
        return Identifier.of(SoulsWeaponry.ModId, "animations/entity/chaos_monarch.animation.json");
    }

    @Override
    public Identifier getModelResource(ChaosSet object) {
        return Identifier.of(SoulsWeaponry.ModId, "geo/chaos_set.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChaosSet object) {
        return Identifier.of(SoulsWeaponry.ModId, "textures/armor/chaos_set_texture.png");
    }
    
}
