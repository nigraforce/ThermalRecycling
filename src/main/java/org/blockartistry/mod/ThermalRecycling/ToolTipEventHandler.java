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

package org.blockartistry.mod.ThermalRecycling;

import org.blockartistry.mod.ThermalRecycling.data.ItemScrapData;
import org.blockartistry.mod.ThermalRecycling.data.ScrappingTables;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public final class ToolTipEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void onToolTipEvent(ItemTooltipEvent event) {

		if (event == null || event.itemStack == null)
			return;

		ItemStack stack = event.itemStack;

		String lore;
		switch (ItemScrapData.getValue(stack)) {
		case NONE:
			lore = StatCollector.translateToLocal("msg.ItemScrapValue.none");
			break;
		case POOR:
			lore = StatCollector.translateToLocal("msg.ItemScrapValue.poor");
			break;
		case STANDARD:
			lore = StatCollector
					.translateToLocal("msg.ItemScrapValue.standard");
			break;
		case SUPERIOR:
			lore = StatCollector
					.translateToLocal("msg.ItemScrapValue.superior");
			break;
		default:
			lore = "UNKNOWN";
			;
		}

		if (lore != null) {
			if (!ScrappingTables.canBeScrapped(stack))
				lore += EnumChatFormatting.GREEN + "*";
			event.toolTip.add(lore);
		}
	}

	public ToolTipEventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
