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

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.view.component.ProgressBarComponent;

/**
 * Controller for the container of {@link ProgressBarComponent}. 
 * @author Fernando Reyes
 */
public class ProgressBarAreaController {
	
	// Attributes.
	
	@FXML
	private VBox progressBarComponentContainerVBox;
	
	// Methods.
	
	/**
	 * Instantiates a new {@link ProgressBarController} and adds it's 
	 * {@link ProgressBarComponent} to the progress bar section of the 
	 * application. Then it binds to the {@link NGSEPTask} for progress
	 * updating.
	 * @return A {@link ProgressBarController} for the 
	 * {@link ProgressBarComponent}
	 */
	public void addProgressBarComponentForTask(NGSEPTask<Void> task) {
		ProgressBarController progressBarController = new ProgressBarController();
		progressBarComponentContainerVBox.getChildren()
			.add(progressBarController.progressBarComponent);
		progressBarController.setTask(task);
	}
}
