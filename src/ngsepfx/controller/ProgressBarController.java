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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import ngsep.main.ProgressNotifier;
import ngsepfx.event.NGSEPRefreshFileExplorerEvent;
import ngsepfx.view.component.ProgressBarComponent;

/**
 * The controller for the {@link ProgressBarComponent} responsible of 
 * communicating the NGSEP Analysis and the {@link ProgressBarComponent}
 * using the {@link ProgressNotifier} interface.
 * @author Fernando Reyes
 * 
 */
public class ProgressBarController {
	
	// Attributes.
	
	public ProgressBarComponent progressBarComponent;
	
	private Task<Void> task;
	
	// Constructor.
	
	/**
	 * Creates a new {@link ProgressBarComponent}
	 */
	public ProgressBarController() {
		this.progressBarComponent = new ProgressBarComponent();
		this.progressBarComponent.shouldKeepRunning.addListener(
			new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable, 
						Boolean oldValue,
						Boolean newValue) {
					if (!newValue.booleanValue()) {
						if (task!=null) {
							task.cancel();
						}
						VBox parent = (VBox) progressBarComponent.getParent();
						parent.fireEvent(new NGSEPRefreshFileExplorerEvent());
						parent.getChildren().remove(progressBarComponent);
					}	
				}
			}
		);
	}	
	
	// Methods.
	
	public void setTask(Task<Void> task) {
		this.task = task;
		progressBarComponent.taskProgressBarDoubleProperty()
			.bind(task.progressProperty());
		progressBarComponent.taskNameTextProperty().bind(task.titleProperty());
		progressBarComponent.taskFileNameTextProperty()
		.bind(task.messageProperty());
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				VBox parent = (VBox) progressBarComponent.getParent();
				parent.fireEvent(new NGSEPRefreshFileExplorerEvent());
				parent.getChildren().remove(progressBarComponent);
			}
		});
		
	}

}
