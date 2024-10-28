package net.soulsweaponry.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.client.hud.PostureHudOverlay;
import net.soulsweaponry.client.registry.*;
import net.soulsweaponry.networking.PacketRegistry;
import net.soulsweaponry.registry.BlockRegistry;
import net.soulsweaponry.registry.FluidRegistry;

public class SoulsWeaponryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        //Blocks
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WITHERED_GRASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WITHERED_TALL_GRASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WITHERED_FERN);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WITHERED_LARGE_FERN);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.WITHERED_BERRY_BUSH);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.HYDRANGEA);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockRegistry.OLEANDER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockRegistry.VERGLAS_BLOCK);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidRegistry.STILL_PURIFIED_BLOOD, FluidRegistry.FLOWING_PURIFIED_BLOOD);

        HudRenderCallback.EVENT.register(new PostureHudOverlay());

        EntityModelLayerModRegistry.initClient();
        EntityModelRegistry.initClient();
        PredicateRegistry.initClient();
        PacketRegistry.registerS2CPackets();
        KeyBindRegistry.initClient();
        ParticleClientRegistry.initClient();

        FluidRenderHandlerRegistry.INSTANCE.register(FluidRegistry.STILL_PURIFIED_BLOOD, FluidRegistry.FLOWING_PURIFIED_BLOOD,
                new SimpleFluidRenderHandler(
                        new Identifier(SoulsWeaponry.ModId, "block/purified_blood_still"),
                        new Identifier(SoulsWeaponry.ModId, "block/purified_blood_flow")
                ));
    }
}
