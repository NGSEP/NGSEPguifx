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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import ngsep.sequences.KmersExtractor;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class KmersExtractorController extends AnalysisAreaController{

	public static final String TASK_NAME = "KmersExtractor";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;	
	
	@FXML
	private ValidatedTextField minKmerCountTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private CheckBox freeTextCheckBox;
	
	@FXML
	private CheckBox onlyForwardStrandCheckBox;
	
	@FXML
	private CheckBox ignoreLowComplexityCheckBox;
	
	@FXML
	private ChoiceBox<String> inputFormatChoiceBox;
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		//textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("kmerLength",kmerLengthTextField);
		textFields.put("minKmerCount", minKmerCountTextField);
		textFields.put("numThreads", numThreadsTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("freeText", freeTextCheckBox);
		checkboxes.put("onlyForwardStrand", onlyForwardStrandCheckBox);
		checkboxes.put("ignoreLowComplexity", ignoreLowComplexityCheckBox);
		return checkboxes;
	}
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/KmersExtractor.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(KmersExtractor.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		inputFormatChoiceBox.getItems().add(FORMAT_FASTQ);
		inputFormatChoiceBox.getItems().add(FORMAT_FASTA);
		String filename = file.getName();
		int k = ReadsAlignerController.getExtensionIndex(filename);
		if(k>0) {
			if(filename.toLowerCase().substring(k).startsWith(".fastq")) inputFormatChoiceBox.getSelectionModel().select(0);
			else if(filename.toLowerCase().substring(k).startsWith(".fq")) inputFormatChoiceBox.getSelectionModel().select(0);
			else inputFormatChoiceBox.getSelectionModel().select(1);
		}
		suggestOutputFile(file, outputPrefixTextField, "_kmers");
		
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			String inputFile = inputFileTextField.getText(); 
    			updateMessage(inputFile);
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				KmersExtractor instance = new KmersExtractor();
    				fillAttributes(instance);
    				if(inputFormatChoiceBox.getSelectionModel().getSelectedIndex()==1) instance.setInputFormat(KmersExtractor.INPUT_FORMAT_FASTA);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), null);
    				log.addHandler(logHandler);
    				instance.setLog(log);
    				instance.setProgressNotifier(this);
    				List<String> files = new ArrayList<>();
    				files.add(inputFile);
    				instance.processFiles(files);
    				instance.saveResults();
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
