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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ngsepfx.event.NGSEPExecuteTaskEvent;

/**
 * Singleton {@link ExecutorService} to execute an {@link NGSEPExecuteTaskEvent}'s
 * task.
 * @author Fernando Reyes
 *
 */
public final class ExecutorSingleton {
	
	private static ExecutorService executor;
	
	/**
	 * Get a 10 pool sized {@link Executors#newFixedThreadPool(int)}.
	 * @return ExecutorService singleton.
	 */
	public static final ExecutorService getExecutor() {
		if (executor == null) {
			executor = Executors.newFixedThreadPool(10);
		}
		return executor;
	}
	
	/**
	 * Waits 5 seconds for all tasks to finish and then stops them all.
	 */
	public static final void stopExecutor() {
		if (executor != null) {
			try {
			    System.out.println("attempt to shutdown executor");
			    executor.shutdown();
			    executor.awaitTermination(5, TimeUnit.SECONDS);
			}
			catch (InterruptedException e) {
			    System.err.println("tasks interrupted");
			}
			finally {
			    if (!executor.isTerminated()) {
			        System.err.println("cancel non-finished tasks");
			    }
			    executor.shutdownNow();
			    System.out.println("shutdown finished");
			}
		}
	}

}
