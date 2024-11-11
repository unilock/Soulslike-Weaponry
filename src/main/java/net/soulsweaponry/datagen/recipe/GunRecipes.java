package net.soulsweaponry.datagen.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;
import net.soulsweaponry.registry.GunRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import net.soulsweaponry.util.ModTags;

public class GunRecipes {

    public static void generateRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GunRegistry.HUNTER_CANNON)
                .input('#', Items.IRON_INGOT)
                .input('G', ItemRegistry.LOST_SOUL)
                .input('M', Blocks.IRON_BLOCK)
                .input('S', ModTags.Items.STICKS)
                .pattern("S M")
                .pattern("SG#")
                .pattern(" MM")
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GunRegistry.HUNTER_PISTOL)
                .input('#', Items.IRON_INGOT)
                .input('G', ItemRegistry.LOST_SOUL)
                .input('S', ModTags.Items.STICKS)
                .pattern(" ##")
                .pattern("SG#")
                .pattern("S  ")
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GunRegistry.GATLING_GUN)
                .input('#', Items.IRON_INGOT)
                .input('G', ItemRegistry.LOST_SOUL)
                .input('M', Blocks.IRON_BLOCK)
                .input('S', ModTags.Items.STICKS)
                .pattern("S #")
                .pattern("SG#")
                .pattern(" #M")
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GunRegistry.BLUNDERBUSS)
                .input('#', Blocks.IRON_BLOCK)
                .input('G', ItemRegistry.LOST_SOUL)
                .input('S', ModTags.Items.STICKS)
                .input('i', Items.IRON_INGOT)
                .pattern(" i#")
                .pattern("SGi")
                .pattern("S i")
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, ItemRegistry.SILVER_BULLET, 10)
                .input(ModTags.Items.SILVER_INGOTS)
                .input(ItemRegistry.LOST_SOUL)
                .input(Items.GUNPOWDER)
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, ItemRegistry.SILVER_BULLET, 3)
                .input(Items.IRON_INGOT)
                .input(ItemRegistry.LOST_SOUL)
                .input(Items.GUNPOWDER)
                .criterion("has_lost_soul", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create()
                        .items(ItemRegistry.LOST_SOUL).build()))
                .offerTo(exporter, Identifier.of(SoulsWeaponry.ModId, "silver_bullet_iron_ingot"));
    }
}