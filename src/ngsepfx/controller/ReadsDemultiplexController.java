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
import javafx.scene.control.CheckBox;
import ngsep.sequencing.ReadsDemultiplex;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 */
public class ReadsDemultiplexController extends AnalysisAreaController{

	public static final String TASK_NAME = "ReadsDemultiplex";
	
	@FXML
	private ValidatedTextField indexFileTextField;
	
	@FXML
	private ValidatedTextField outDirectoryTextField;
	
	@FXML
	private ValidatedTextField laneFilesDescriptorTextField;	
	
	@FXML
	private ValidatedTextField fastqFileTextField;
	
	@FXML
	private ValidatedTextField fastqFile2TextField;
	
	@FXML
	private ValidatedTextField flowcellTextField;
	
	@FXML
	private ValidatedTextField laneTextField;
	
	@FXML
	private ValidatedTextField trimSequencesTextField;
	
	@FXML
	private ValidatedTextField minReadLengthTextField;
	
	@FXML
	private CheckBox dualBarcodeCheckBox;
	
	@FXML
	private CheckBox uncompressedOutputCheckBox;
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("indexFile", indexFileTextField);
		textFields.put("outDirectory", outDirectoryTextField);
		textFields.put("laneFilesDescriptor", laneFilesDescriptorTextField);
		textFields.put("fastqFile", fastqFileTextField);
		textFields.put("fastqFile2", fastqFile2TextField);
		textFields.put("flowcell", flowcellTextField);
		textFields.put("lane", laneTextField);
		textFields.put("trimSequences", trimSequencesTextField);
		textFields.put("minReadLength", minReadLengthTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("dualBarcode", dualBarcodeCheckBox);
		checkboxes.put("uncompressedOutput", uncompressedOutputCheckBox);
		return checkboxes;
	}
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/ReadsDemultiplex.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(ReadsDemultiplex.class.getName());
		indexFileTextField.setText(file.getAbsolutePath());
		outDirectoryTextField.setText(file.getParentFile().getAbsolutePath());
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(indexFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				ReadsDemultiplex instance = new ReadsDemultiplex();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutDirectory()+File.separator+"Demultiplex", null);
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
