/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ngsepfx.concurrent;

import javafx.concurrent.Task;
import ngsep.main.ProgressNotifier;

/**
 * Class to create a {@link Task} that updates the progress of the {@link Task}
 * using {@link ProgressNotifier#keepRunning(int)} and determines if the 
 * {@link Task} is cancelled using {@link Task#isCancelled()}. 
 * The developer must not call {@link Task}'s updateProgress(int,int).
 * @author Fernando Reyes
 *
 */
public abstract class NGSEPTask<V> extends Task<V> implements ProgressNotifier{
	
	// Attributes.
	
	private int maxProgress = 100;
		
	// ProgressNotifier.
	
	@Override
	public boolean keepRunning(int progress) {
		super.updateProgress(progress, maxProgress);
		return !super.isCancelled();
	}
	
	// Getters and setters.

	/**
	 * @return the maxProgress
	 */
	public int getMaxProgress() {
		return maxProgress;
	}

	/**
	 * @param maxProgress the maxProgress to set
	 */
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

}
