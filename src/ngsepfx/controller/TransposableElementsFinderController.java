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
import ngsep.transposons.TransposableElementsFinder;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class TransposableElementsFinderController extends AnalysisAreaController {
	
	//constants.
	public static final String TASK_NAME = "TransposableElementsFinder";
	
	//FXML parameters
	
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField transposonsDatabaseFileTextField;
	
	@FXML
	private ValidatedTextField minTELengthTextField;
	
	@FXML
	private ValidatedTextField roundsTextField;
	
	@FXML
	private ValidatedTextField limitGenomeLengthTextField;
	
	@FXML
	private ValidatedTextField kmerLengthDenovoTextField;
	
	@FXML
	private ValidatedTextField kmerLengthSimilarityTextField;
	
	@FXML
	private ValidatedTextField windowLengthSimilarityTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private CheckBox runDeNovoCheckBox;

	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/TransposableElementsFinder.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("transposonsDatabaseFile", transposonsDatabaseFileTextField);
		textFields.put("minTELength", minTELengthTextField);
		textFields.put("rounds", roundsTextField);
		textFields.put("limitGenomeLength", limitGenomeLengthTextField);
		textFields.put("kmerLengthDenovo", kmerLengthDenovoTextField);
		textFields.put("kmerLengthSimilarity", kmerLengthSimilarityTextField);
		textFields.put("windowLengthSimilarity", windowLengthSimilarityTextField);
		textFields.put("numThreads", numThreadsTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("runDeNovo", runDeNovoCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(TransposableElementsFinder.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "_TEFinder");
		// TODO Auto-generated method stub
		
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
    				TransposableElementsFinder instance = new TransposableElementsFinder();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), "");
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
