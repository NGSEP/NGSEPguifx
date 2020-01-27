/*
    Debigulator - A batch compression utility
Copyright (C) 2003-2018 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * Modified for this specific software.
 */
package ngsepfx.controller;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import ngsepfx.concurrent.ExecutorSingleton;
import ngsepfx.controller.fileexplorer.FileExplorerController;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;
import ngsepfx.event.NGSEPRefreshFileExplorerEvent;

/**
 * Main controller of the application. Used to communicate the 
 * different top level controllers: {@link AnalysisAreaController}, 
 * {@link ProgressBarAreaController}, {@link FileExplorerController}; making
 * use of all {@link NGSEPEvent}.
 * @author Oracle and/or its affiliates and Hugues Johnson.
 * @author Fernando Reyes
 *
 */
public class MainController {
	
	// Attributes.
	
	@FXML 
	private FileExplorerController fileExplorerController;
	
	@FXML 
	private ProgressBarAreaController progressBarAreaController;
	
	@FXML
	private BorderPane rootBorderPane;
	
	private AnalysisAreaController controller;
	
	// FXML Life cycle methods.
	
	/**
	 * Set NSEPEvent global (At root Node level) handlers and filters.
	 */
	@FXML
	private void initialize() {
		rootBorderPane.addEventFilter(NGSEPAnalyzeFileEvent.ANALYZE_FILE, this::handleNGSEPAnalyzeFileEvent);
		rootBorderPane.addEventFilter(NGSEPExecuteTaskEvent.EXECUTE_TASK, this::handleNGSEPExecuteTaskEvent);
		rootBorderPane.addEventFilter(NGSEPRefreshFileExplorerEvent.REFRESH, this::handleNGSEPRefreshFileExplorerEvent);
	}
	
	// Methods.
	
	/**
	 * Get {@link AnalysisAreaController} to process analysis and load 
	 * it's root into the {@link javafx.scene.Scene}.
	 * @param event The {@link NGSEPAnalyzeFileEvent} containing the 
	 * {@link AnalysisAreaController} fully qualified name.
	 */
	private void handleNGSEPAnalyzeFileEvent(NGSEPAnalyzeFileEvent event) {
		try {
			event.consume();
			Class<?> controllerClass = (Class<?>) Class.forName(event.controllerFullyQualifiedName);
			Constructor <?> [] constructors = (Constructor<?>[]) controllerClass.getDeclaredConstructors();
			controller = (AnalysisAreaController)constructors[0].newInstance();
			controller.initializeController();
			Node analysisAreaRoot = controller.getRootNode();
			BorderPane analysisArea = (BorderPane) rootBorderPane.getCenter();
			analysisArea.setCenter(analysisAreaRoot);
			controller.handleActivationEvent(event);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Receive the task to be passed to the Executor and assign it to a
	 * {@link ProgressBarController}.
	 * @param event {@link NGSEPExecuteTaskEvent} containing the {@link Runnable}
	 * task.
	 */
	private void handleNGSEPExecuteTaskEvent(NGSEPExecuteTaskEvent event) {
		event.consume();
		progressBarAreaController.addProgressBarComponentForTask(event.task);
		
		BorderPane analysisArea = (BorderPane) rootBorderPane.getCenter();
		analysisArea.setCenter(null);
		
		ExecutorService executor = ExecutorSingleton.getExecutor();
		executor.submit(event.task);
	}
	
	private void handleNGSEPRefreshFileExplorerEvent(NGSEPRefreshFileExplorerEvent event) {
		event.consume();
		fileExplorerController.refresh(null);
	}
}
