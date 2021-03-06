package org.blockartistry.mod.ThermalRecycling.support.recipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.blockartistry.mod.ThermalRecycling.ModLog;
import org.blockartistry.mod.ThermalRecycling.util.ItemStackHelper;
import org.blockartistry.mod.ThermalRecycling.util.MyUtils;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class RecipeDecomposition implements Iterable<ItemStack> {

	static final String[] classIgnoreList = new String[] {
			"forestry.lepidopterology.MatingRecipe",
			"cofh.thermaldynamics.util.crafting.RecipeCover",
			"mods.railcraft.common.carts.LocomotivePaintingRecipe",
			"mods.railcraft.common.emblems.EmblemPostColorRecipe",
			"codechicken.enderstorage.common.EnderStorageRecipe" };

	public class MyIterator<T> implements Iterator<T> {

		List<ItemStack> list;
		int index;

		public MyIterator(List<ItemStack> list) {
			this.list = list;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return list != null && index < list.size();
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			return (T) list.get(index++);
		}

		@Override
		public void remove() {
			// implement... if supported.
		}
	}

	static Field teRecipeAccessor = null;
	static Field forestryRecipeAccessor = null;

	final IRecipe recipe;
	List<ItemStack> projection;
	ItemStack inputStack;

	public RecipeDecomposition(boolean buildCraftSpecial, ItemStack input,
			Object... output) {
		inputStack = input;
		recipe = null;
		projection = projectBuildcraftRecipeList(output);
		scrubProjection();
	}

	public RecipeDecomposition(ItemStack input, Object... output) {
		inputStack = input;
		recipe = null;
		projection = projectForgeRecipeList(output);
		scrubProjection();
	}

	public RecipeDecomposition(IRecipe recipe) {
		this.recipe = recipe;
	}

	public ItemStack getInput() {
		return inputStack != null ? inputStack.copy() : recipe
				.getRecipeOutput().copy();
	}

	public ItemStack getOutput() {
		return recipe.getRecipeOutput().copy();
	}

	protected static boolean matchClassName(Object obj, String name) {
		return obj.getClass().getName().compareTo(name) == 0;
	}

	protected static boolean isClassIgnored(Object obj) {

		for (String s : classIgnoreList)
			if (matchClassName(obj, s))
				return true;
		return false;
	}

	protected void scrubProjection() {

		if (projection == null)
			return;

		// Scan for the list looking for wildcards as well
		// as those items that match the input. Sometimes
		// an author will make a cyclic recipe.
		for (int i = 0; i < projection.size(); i++) {
			ItemStack stack = projection.get(i);
			if (stack != null)
				if (ItemHelper.itemsEqualWithMetadata(stack, inputStack))
					projection.set(i, null);
				else if (stack != null
						&& stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
					stack.setItemDamage(0);
		}

		// Do final scrub on the list
		projection = MyUtils.compress(ItemStackHelper.coelece(projection));
	}

	public List<ItemStack> project() {

		if (projection == null) {
			if (recipe instanceof ShapedRecipes) {
				projection = project((ShapedRecipes) recipe);
			} else if (recipe instanceof ShapelessRecipes) {
				projection = project((ShapelessRecipes) recipe);
			} else if (recipe instanceof ShapedOreRecipe) {
				projection = project((ShapedOreRecipe) recipe);
			} else if (recipe instanceof ShapelessOreRecipe) {
				projection = project((ShapelessOreRecipe) recipe);
			} else if (matchClassName(recipe,
					"cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper")) {
				projection = projectTERecipe(recipe);
			} else if (matchClassName(recipe,
					"forestry.core.utils.ShapedRecipeCustom")) {
				projection = projectForestryRecipe(recipe);
			} else if (!isClassIgnored(recipe)) {
				ModLog.info("Unknown recipe class: %s", recipe.getClass()
						.getName());
			}

			scrubProjection();
		}

		return projection;
	}

	@Override
	public Iterator<ItemStack> iterator() {
		if (projection == null)
			projection = project();
		return new MyIterator<ItemStack>(projection);
	}

	protected static List<ItemStack> project(ShapedRecipes recipe) {
		return ItemStackHelper.clone(recipe.recipeItems);
	}

	protected static List<ItemStack> project(ShapelessRecipes recipe) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		for (Object stack : recipe.recipeItems)
			result.add(((ItemStack) (stack)).copy());
		return result;
	}

	static List<ItemStack> recurseArray(List<?> list, int level) {

		ArrayList<ItemStack> result = new ArrayList<ItemStack>();

		if (level == 3)
			result.add((ItemStack) list.get(0));
		else
			for (Object o : list) {
				if (o instanceof ItemStack)
					result.add(((ItemStack) o).copy());
				else if (o instanceof ArrayList<?>) {
					result.addAll(recurseArray((ArrayList<?>) o, level + 1));
				}
			}

		return result;
	}

	protected static List<ItemStack> projectBuildcraftRecipeList(Object[] list) {
		return recurseArray(Arrays.asList(list), 1);
	}

	protected static List<ItemStack> projectForgeRecipeList(Object[] list) {

		ArrayList<ItemStack> result = new ArrayList<ItemStack>();

		for (int i = 0; i < list.length; i++) {
			Object o = list[i];

			if (o instanceof ItemStack)
				result.add(((ItemStack) o).copy());
			else if (o instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<ItemStack> t = (ArrayList<ItemStack>) o;
				result.add(ItemStackHelper.getPreferredStack(t.get(0)));
			}
		}

		return result;
	}

	protected static List<ItemStack> project(ShapedOreRecipe recipe) {
		return projectForgeRecipeList(recipe.getInput());
	}

	protected static List<ItemStack> project(ShapelessOreRecipe recipe) {
		return projectForgeRecipeList(recipe.getInput().toArray());
	}

	protected static List<ItemStack> projectTERecipe(IRecipe recipe) {

		List<ItemStack> result = null;

		try {

			if (teRecipeAccessor == null) {
				Field temp = recipe.getClass().getDeclaredField("recipe");
				temp.setAccessible(true);
				teRecipeAccessor = temp;
			}

			try {
				ShapedOreRecipe shaped = (ShapedOreRecipe) teRecipeAccessor
						.get(recipe);
				result = project(shaped);
			} catch (Exception e) {
			}

		} catch (NoSuchFieldException e) {
			;
		} catch (SecurityException e) {
			;
		} catch (IllegalArgumentException e) {
			;
		}

		return result;
	}

	protected static List<ItemStack> projectForestryRecipe(IRecipe recipe) {

		List<ItemStack> result = null;

		try {

			if (forestryRecipeAccessor == null) {
				Field temp = recipe.getClass().getDeclaredField("ingredients");
				temp.setAccessible(true);
				forestryRecipeAccessor = temp;
			}

			try {
				Object[] shaped = (Object[]) forestryRecipeAccessor.get(recipe);
				result = projectForgeRecipeList(shaped);
			} catch (Exception e) {
			}

		} catch (NoSuchFieldException e) {
			;
		} catch (SecurityException e) {
			;
		} catch (IllegalArgumentException e) {
			;
		}

		return result;
	}

	public static IRecipe findRecipe(ItemStack stack) {

		for (Object o : CraftingManager.getInstance().getRecipeList()) {
			IRecipe r = (IRecipe) o;
			// craftingEquivalent
			if (ItemHelper.itemsEqualForCrafting(stack, r.getRecipeOutput()))
				return r;
		}

		return null;
	}
}
