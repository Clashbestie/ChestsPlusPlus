package com.jamesdpeters.minecraft.chests.crafting;

import com.jamesdpeters.minecraft.chests.api.ApiSpecific;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class Crafting {

    private static List<ShapedRecipe> shapedRecipes;
    private static List<ShapelessRecipe> shapelessRecipes;

    public static void load() {
        shapedRecipes = new ArrayList<>();
        shapelessRecipes = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof ShapedRecipe) {
                shapedRecipes.add((ShapedRecipe) recipe);
            }
            if (recipe instanceof ShapelessRecipe) {
                shapelessRecipes.add((ShapelessRecipe) recipe);
            }
        });
    }

    public static Recipe getRecipe(List<ItemStack> craftingTable) {
        return ApiSpecific.getNmsProvider().getCraftingProvider().getRecipe(Bukkit.getWorlds().get(0), craftingTable);
    }

    public static ItemStack craft(List<ItemStack> recipe) {
        return ApiSpecific.getNmsProvider().getCraftingProvider().craft(Bukkit.getWorlds().get(0), recipe);
    }

    public static Recipe getRecipeByKey(NamespacedKey key) {
        Optional<ShapelessRecipe> recipe = shapelessRecipes.stream().filter(s -> s.getKey().equals(key)).findFirst();
        if (recipe.isPresent()) return recipe.get();

        Optional<ShapedRecipe> shapedRecipe = shapedRecipes.stream().filter(s -> s.getKey().equals(key)).findFirst();
        return shapedRecipe.orElse(null);
    }

}
