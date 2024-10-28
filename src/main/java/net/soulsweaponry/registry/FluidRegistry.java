package net.soulsweaponry.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.fluid.PurifiedBloodCauldronBlock;
import net.soulsweaponry.fluid.PurifiedBlood;
import net.soulsweaponry.fluid.PurifiedBloodBlock;

import java.util.function.Predicate;

public class FluidRegistry {

    public static FlowableFluid STILL_PURIFIED_BLOOD;
    public static FlowableFluid FLOWING_PURIFIED_BLOOD;
    public static Block PURIFIED_BLOOD_BLOCK;
    public static Item PURIFIED_BLOOD_BUCKET;

    public static final Predicate<Biome.Precipitation> NONE_PREDICATE = precipitation -> precipitation == Biome.Precipitation.NONE;
    public static final Block BLOOD_CAULDRON = BlockRegistry.registerBlockAlone(
            new PurifiedBloodCauldronBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON), NONE_PREDICATE, CauldronBehavior.WATER_CAULDRON_BEHAVIOR),
            "purified_blood_cauldron"
    );

    public static void init() {
        STILL_PURIFIED_BLOOD = registerFluid("purified_blood", new PurifiedBlood.Still());
        FLOWING_PURIFIED_BLOOD = registerFluid("flowing_purified_blood", new PurifiedBlood.Flowing());
        PURIFIED_BLOOD_BLOCK = BlockRegistry.registerBlockAlone(new PurifiedBloodBlock(STILL_PURIFIED_BLOOD, FabricBlockSettings.copyOf(Blocks.WATER)), "purified_blood_block");
        PURIFIED_BLOOD_BUCKET = ItemRegistry.registerItem(new BucketItem(STILL_PURIFIED_BLOOD, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)), "purified_blood_bucket");
    }

    public static FlowableFluid registerFluid(String id, FlowableFluid fluid) {
        return Registry.register(Registries.FLUID, new Identifier(SoulsWeaponry.ModId, id), fluid);
    }

    public static void registerCauldronBehavior() {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(PURIFIED_BLOOD_BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(
                world, pos, player, hand, stack, BLOOD_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY
        ));//TODO right clicking the fluid (in general, both in and out of cauldron) gives regular water bottle, remove this somehow
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(
                Items.BUCKET,
                (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(
                        state,
                        world,
                        pos,
                        player,
                        hand,
                        stack,
                        new ItemStack(PURIFIED_BLOOD_BUCKET),
                        state1 -> state1.get(LeveledCauldronBlock.LEVEL) == 3,
                        SoundEvents.ITEM_BUCKET_FILL
                )
        );
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(ItemRegistry.GLASS_VIAL, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, ItemRegistry.BLOOD_VIAL.getDefaultStack()));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        });
        CauldronFluidContent.registerCauldron(BLOOD_CAULDRON, STILL_PURIFIED_BLOOD, FluidConstants.BOTTLE, LeveledCauldronBlock.LEVEL);
    }
}
