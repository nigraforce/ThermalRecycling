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

import org.blockartistry.mod.ThermalRecycling.blocks.ScrapBlock;
import org.blockartistry.mod.ThermalRecycling.machines.MachineComposter;
import org.blockartistry.mod.ThermalRecycling.machines.MachineScrapAssessor;
import org.blockartistry.mod.ThermalRecycling.machines.MachineThermalRecycler;

/**
 * Contains references to all Blocks in the mod as well as logic for
 * registration.
 *
 */
public final class BlockManager {

	public static ScrapBlock scrapBlock = new ScrapBlock();
	public static MachineThermalRecycler thermalRecycler = new MachineThermalRecycler();
	public static MachineScrapAssessor scrapAssessor = new MachineScrapAssessor();
	public static MachineComposter composter = new MachineComposter();

	static void registerBlocks() {

		scrapBlock.register();

		thermalRecycler.register();
		scrapAssessor.register();
		composter.register();
	}

	public BlockManager() {
		registerBlocks();
	}
}
