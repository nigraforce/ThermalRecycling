/*
 * This file is part of ThermalRecycling, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.ThermalRecycling.support.recipe;

import org.blockartistry.mod.ThermalRecycling.data.RecipeData;
import org.blockartistry.mod.ThermalRecycling.util.ItemStackHelper;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import cofh.api.modhelpers.ThermalExpansionHelper;

public class SmelterRecipeBuilder extends
		SecondaryInputRecipeBuilder<SmelterRecipeBuilder> {

	@Override
	protected int saveImpl(ItemStack stack) {

		Preconditions.checkNotNull(stack, "Input ItemStack cannot be null");
		Preconditions.checkNotNull(secondaryInput,
				"Secondary input ItemStack cannot be null");
		Preconditions.checkNotNull(output, "Output ItemStack cannot be null");

		ThermalExpansionHelper.addSmelterRecipe(energy, stack, secondaryInput,
				output);

		return RecipeData.SUCCESS;
	}

	@Override
	protected String toString(ItemStack stack) {

		Preconditions.checkNotNull(stack, "Input ItemStack cannot be null");
		Preconditions.checkNotNull(secondaryInput,
				"Secondary input ItemStack cannot be null");
		Preconditions.checkNotNull(output, "Output ItemStack cannot be null");

		return String.format("Induction Smelter [%dx %s, %dx %s] => [%dx %s]",
				stack.stackSize, ItemStackHelper.resolveName(stack),
				secondaryInput.stackSize,
				ItemStackHelper.resolveName(secondaryInput), output.stackSize,
				ItemStackHelper.resolveName(output));
	}
}
