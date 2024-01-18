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
import javafx.scene.control.ChoiceBox;
import ngsep.assembly.AssemblyReferenceSorter;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class AssemblyReferenceSorterController extends AnalysisAreaController {
	
	//constants.
	public static final String TASK_NAME = "AssemblyReferenceSorter";
	
	//FXML parameters
	
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField referenceFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private CheckBox hardMaskCheckBox;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField windowLengthTextField;

	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private ChoiceBox<String> renameContigsPolicyChoiceBox;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/AssemblyReferenceSorter.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("referenceFile", referenceFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("windowLength", windowLengthTextField);
		textFields.put("numThreads", numThreadsTextField);
		return textFields;
	}
	
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("hardMask", hardMaskCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(AssemblyReferenceSorter.class.getName());
		renameContigsPolicyChoiceBox.getItems().add(AssemblyReferenceSorter.RENAME_CONTIGS_POLICY_REFNAMES,"By reference sequence names");
		renameContigsPolicyChoiceBox.getItems().add(AssemblyReferenceSorter.RENAME_CONTIGS_POLICY_CONSECUTIVE, "Consecutive");
		renameContigsPolicyChoiceBox.getItems().add(AssemblyReferenceSorter.RENAME_CONTIGS_POLICY_KEEPNAMES, "Keep input sequence names");
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_sortRef.fa");
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
    				AssemblyReferenceSorter instance = new AssemblyReferenceSorter();
    				fillAttributes(instance);
    				instance.setRenameContigsPolicy(renameContigsPolicyChoiceBox.getSelectionModel().getSelectedIndex());
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputFile(), "");
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
