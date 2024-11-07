package net.soulsweaponry.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.TransparentBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.blocks.AltarBlock;
import net.soulsweaponry.blocks.BlackstonePedestal;
import net.soulsweaponry.blocks.ChungusMonolith;
import net.soulsweaponry.blocks.DrippingBlock;
import net.soulsweaponry.blocks.SoulLampBlock;
import net.soulsweaponry.blocks.WitheredBlock;
import net.soulsweaponry.blocks.WitheredFlower;
import net.soulsweaponry.blocks.WitheredGrass;
import net.soulsweaponry.blocks.WitheredTallFlower;
import net.soulsweaponry.blocks.WitheredTallGrass;

import java.util.function.ToIntFunction;

public class BlockRegistry {

    public static final Block CRIMSON_OBSIDIAN = new DrippingBlock(FabricBlockSettings
        .copyOf(Blocks.OBSIDIAN)
        .strength(50.0F, 1200.0F) //hardness and resistance, check wiki
        .sounds(BlockSoundGroup.STONE)
        .luminance(10)
        .requiresTool(), ParticleTypes.FALLING_LAVA);
    public static final Block INFUSED_BLACKSTONE = new Block(FabricBlockSettings.copyOf(Blocks.BLACKSTONE).strength(1.8F, 7.0F).sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block CRACKED_INFUSED_BLACKSTONE = new Block(FabricBlockSettings.copyOf(Blocks.BLACKSTONE).strength(1.8F, 7.0F) .sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block MOONSTONE_ORE = new ExperienceDroppingBlock(UniformIntProvider.create(4, 8), FabricBlockSettings.copyOf(Blocks.DIAMOND_ORE).strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE).luminance(9).requiresTool());
    public static final Block MOONSTONE_ORE_DEEPSLATE = new ExperienceDroppingBlock(UniformIntProvider.create(4, 8), FabricBlockSettings.copyOf(Blocks.DEEPSLATE_DIAMOND_ORE).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE).luminance(9).requiresTool());
    public static final Block MOONSTONE_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).strength(5.0F, 6.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).requiresTool());
    public static final AltarBlock ALTAR_BLOCK = new AltarBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).strength(30F, 800.0F).sounds(BlockSoundGroup.STONE).nonOpaque().requiresTool());
    public static final WitheredBlock WITHERED_DIRT = new WitheredBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).strength(0.3F).sounds(BlockSoundGroup.GRAVEL), Blocks.DIRT);
    public static final WitheredBlock WITHERED_GRASS_BLOCK = new WitheredBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).strength(0.5F).sounds(BlockSoundGroup.GRAVEL), Blocks.GRASS_BLOCK);
    public static final WitheredGrass WITHERED_GRASS = new WitheredGrass(FabricBlockSettings.copyOf(Blocks.SHORT_GRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XYZ), Blocks.SHORT_GRASS);
    public static final WitheredGrass WITHERED_FERN = new WitheredGrass(FabricBlockSettings.copyOf(Blocks.SHORT_GRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XYZ), Blocks.FERN);
    public static final WitheredGrass WITHERED_BERRY_BUSH = new WitheredGrass(FabricBlockSettings.copyOf(Blocks.SHORT_GRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS), Blocks.SWEET_BERRY_BUSH);
    public static final WitheredFlower HYDRANGEA = new WitheredFlower(EffectRegistry.DECAY, 200, FabricBlockSettings.copyOf(Blocks.POPPY).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS));
    public static final WitheredTallGrass WITHERED_TALL_GRASS = new WitheredTallGrass(FabricBlockSettings.copyOf(Blocks.TALL_GRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ), Blocks.TALL_GRASS);
    public static final WitheredTallGrass WITHERED_LARGE_FERN = new WitheredTallGrass(FabricBlockSettings.copyOf(Blocks.TALL_GRASS).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ), Blocks.LARGE_FERN);
    public static final WitheredTallFlower OLEANDER = new WitheredTallFlower(FabricBlockSettings.copyOf(Blocks.ROSE_BUSH).noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).offset(AbstractBlock.OffsetType.XZ), Blocks.LARGE_FERN, EffectRegistry.DECAY);
    public static final BlackstonePedestal BLACKSTONE_PEDESTAL = new BlackstonePedestal(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).strength(20.0f, 400.0f).sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block VERGLAS_ORE = new ExperienceDroppingBlock(UniformIntProvider.create(4, 8), FabricBlockSettings.copyOf(Blocks.EMERALD_ORE).strength(3.0F, 3.0F).sounds(BlockSoundGroup.STONE).requiresTool());
    public static final Block VERGLAS_ORE_DEEPSLATE = new ExperienceDroppingBlock(UniformIntProvider.create(4, 8), FabricBlockSettings.copyOf(Blocks.DEEPSLATE_EMERALD_ORE).strength(4.5F, 3.0F).sounds(BlockSoundGroup.DEEPSLATE).requiresTool());
    public static final Block VERGLAS_BLOCK = new TransparentBlock(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.DARK_AQUA).strength(5.0F, 6.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).nonOpaque().requiresTool());
    public static final Block SOULFIRE_STAIN = new MagmaBlock(AbstractBlock.Settings.copy(Blocks.MAGMA_BLOCK).mapColor(MapColor.DARK_AQUA).requiresTool().luminance(state -> 3).ticksRandomly().strength(0.5f).allowsSpawning((state, world, pos, entityType) -> entityType.isFireImmune()).postProcess((state, world, pos) -> true).emissiveLighting((state, world, pos) -> true));
    public static final Block SOUL_LAMP = new SoulLampBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_LAMP).luminance(BlockRegistry.createLightLevelFromLitBlockState(15)).strength(0.3f).sounds(BlockSoundGroup.GLASS).allowsSpawning((state, world, pos, type) -> true));
    public static final Block CHUNGUS_MONOLITH = new ChungusMonolith(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_TILES).strength(3f, 3f).sounds(BlockSoundGroup.STONE).nonOpaque().requiresTool());

    public static void init() {
        registerBlock(CRIMSON_OBSIDIAN, "crimson_obsidian");
        registerBlock(INFUSED_BLACKSTONE, "infused_blackstone");
        registerBlock(CRACKED_INFUSED_BLACKSTONE, "cracked_infused_blackstone");
        registerBlock(MOONSTONE_ORE, "moonstone_ore");
        registerBlock(MOONSTONE_ORE_DEEPSLATE, "moonstone_ore_deepslate");
        registerBlock(MOONSTONE_BLOCK, "moonstone_block");
        registerBlock(ALTAR_BLOCK, "altar_block");
        registerBlock(WITHERED_DIRT, "withered_dirt");
        registerBlock(WITHERED_GRASS_BLOCK, "withered_grass_block");
        registerBlock(WITHERED_GRASS, "withered_grass");
        registerBlock(WITHERED_BERRY_BUSH, "withered_berry_bush");
        registerBlock(WITHERED_FERN, "withered_fern");
        registerBlock(HYDRANGEA, "hydrangea");
        registerBlock(WITHERED_TALL_GRASS, "withered_tall_grass");
        registerBlock(WITHERED_LARGE_FERN, "withered_large_fern");
        registerBlock(OLEANDER, "oleander");
        registerBlock(BLACKSTONE_PEDESTAL, "blackstone_pedestal");
        registerBlock(VERGLAS_ORE, "verglas_ore");
        registerBlock(VERGLAS_ORE_DEEPSLATE, "verglas_ore_deepslate");
        registerBlock(VERGLAS_BLOCK, "verglas_block");
        registerBlock(SOULFIRE_STAIN, "soulfire_stain");
        registerBlock(SOUL_LAMP, "soul_lamp");
        registerBlock(CHUNGUS_MONOLITH, "chungus_monolith");
    }

    private static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }

    public static <I extends Block> I registerBlock(I block, String name) {
        registerBlockItem(block, name);
		return Registry.register(Registries.BLOCK, new Identifier(SoulsWeaponry.ModId, name), block);
	}

    public static Item registerBlockItem(Block block, String name) {
        Item item = new BlockItem(block, new FabricItemSettings());
        SoulsWeaponry.ITEM_GROUP_LIST.add(item);
        return Registry.register(Registries.ITEM, new Identifier(SoulsWeaponry.ModId, name), item);
    }
}
