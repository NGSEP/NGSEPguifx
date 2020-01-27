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
package ngsepfx.view.component;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A custom component with a {@link ProgressBar} for the application.
 * @author Fernando Reyes
 *
 */
public class ProgressBarComponent extends VBox {
	
	// Attributes.
	
    @FXML 
    private Text taskNameText;
    
    @FXML 
    private Text fileNameText;
    
    @FXML
    private ProgressBar taskProgressBar;
    
    public BooleanProperty shouldKeepRunning;
    
    // Constructor.

    /**
     * Loads the node graph of this custom component.
     */
    public ProgressBarComponent() {
    	shouldKeepRunning = new SimpleBooleanProperty();
    	shouldKeepRunning.setValue(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
        		.getResource("/ngsepfx/view/ProgressBarComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    // FXML Life cycle methods.
	
 	/**
 	 * Set progress to 0.
 	 */
 	@FXML
 	private void initialize() {
 		setProgress(0); 		
 	}
    
    // Methods.
    
    	// taskNameText

    public String getTaskName() {
        return taskNameTextProperty().get();
    }

    public void setTaskName(String value) {
    	taskNameTextProperty().set(value);
    }

    public StringProperty taskNameTextProperty() {
        return taskNameText.textProperty();
    }
    
    	// fileNameText
    
    public String getFileName() {
        return taskFileNameTextProperty().get();
    }

    public void setFileName(String value) {
    	taskFileNameTextProperty().set(value);
    }

    public StringProperty taskFileNameTextProperty() {
        return fileNameText.textProperty();
    }
    
    	// taskProgressBar
    
    public double getProgress() {
        return taskProgressBarDoubleProperty().get();
    }

    public void setProgress(double value) {
    	taskProgressBarDoubleProperty().set(value);
    }

    public DoubleProperty taskProgressBarDoubleProperty() {
        return taskProgressBar.progressProperty();
    }
    
    	// Button

    /**
     * Set the {@link ProgressBarComponent#shouldKeepRunning} to false in order
     * to indicate the NGSEP analysis to stop.
     */
    @FXML
    protected void cancelProcess() {
    	shouldKeepRunning.setValue(false);
    }

}
