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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import ngsep.assembly.Assembler;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * Controller for the genomes assembler
 * @author Jorge Duitama
 *
 */
public class AssemblerController extends AnalysisAreaController {
	
	//Constants.
	
	public static final String TASK_NAME = "Assembler";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField graphFileTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField windowLengthTextField;
	
	@FXML
	private ValidatedTextField ploidyTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private ChoiceBox<String> inputFormatChoiceBox;
	
	//AnalysisAreaController.

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/Assembler.fxml";
	}
	
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	protected Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("graphFile", graphFileTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("windowLength", windowLengthTextField);
		textFields.put("ploidy", ploidyTextField);
		textFields.put("numThreads", numThreadsTextField);
		return textFields;
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#handleActivationEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(Assembler.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		inputFormatChoiceBox.getItems().add(FORMAT_FASTQ);
		inputFormatChoiceBox.getItems().add(FORMAT_FASTA);
		String filename = file.getName();
		int k = ReadsAlignerController.getExtensionIndex(filename);
		if(k>0) {
			if(filename.toLowerCase().substring(k).startsWith(".fastq")) inputFormatChoiceBox.getSelectionModel().select(0);
			else inputFormatChoiceBox.getSelectionModel().select(1);
		}
		suggestOutputFile(file, outputPrefixTextField, "_assembly");
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {
    		@Override 
    		public Void call() {
    			updateMessage(inputFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				Assembler instance = new Assembler();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), "Assembler");
    				log.addHandler(logHandler);
    				
    				instance.setLog(log);
    				instance.setProgressNotifier(this);
    				
    				instance.run();
    			} catch (Exception e) {
    				e.printStackTrace();
    				showExecutionErrorDialog(Thread.currentThread().getName(), e);
    			} finally {
    				if(logHandler!=null) {
    					logHandler.flush();
    					logHandler.close();
    				}
    			}
    			return null;
    		}
		};
	}
	
	

}
