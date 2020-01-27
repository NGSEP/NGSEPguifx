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
package ngsepfx.controller;

import java.util.concurrent.TimeUnit;

import ngsep.main.ProgressNotifier;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;

/**
 * Test analysis to demonstrate the usage of {@link NGSEPExecuteTaskEvent}.
 * @author Fernando Reyes
 *
 */
public class TestAnalysisController extends AnalysisAreaController {
	
	// Constantes.
	
	private static final String TASK_NAME = "Test Analysis";
	
	// Attributes.
	
	private String filename;
	
	// AnalysisAreaController.

	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/TestAnalysis.fxml";
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.IAnalysisAreaController#handleActivationEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent ngsepAnalyzeFileEvent = (NGSEPAnalyzeFileEvent) event;
		this.filename = ngsepAnalyzeFileEvent.file.getName();
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getTask()
	 */
	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {
    		@Override 
    		public Void call() {
    			try {
    				updateMessage(TestAnalysisController.this.filename);
    				updateTitle(TestAnalysisController.TASK_NAME);
    				executeCall(this);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				System.out.println(Thread.currentThread().getName() + " task cancelled");
    			}
    			return null;
    		}
		};
	}
	/**
	 * Simulate a call to an NGSEPCore process that receives a 
	 * {@link ProgressNotifier}.
	 * @param progressNotifier The {@link ProgressNotifier}
	 * @throws InterruptedException if the process is interrupted.
	 */
	private void executeCall(ProgressNotifier progressNotifier) 
			throws InterruptedException {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " starting task");
		progressNotifier.keepRunning(50);
		System.out.println(threadName + " progress updated");
		TimeUnit.SECONDS.sleep(10);
		System.out.println(threadName + " going to finish");
		progressNotifier.keepRunning(100);
		System.out.println(threadName + " ending task");
	}

	

}
