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
import ngsep.alignments.ReadsAligner;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Maria del Rosario Leon
 *
 */
public class ReadsAlignerController extends AnalysisAreaController {

	public static final String TASK_NAME = "ReadsAligner";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField inputFile2TextField;
	
	@FXML
	private ValidatedTextField fmIndexFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField knownSTRsFileTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField minProportionKmersTextField;
	
	@FXML
	private ValidatedTextField minInsertLengthTextField;
	
	@FXML
	private ValidatedTextField maxInsertLengthTextField;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/ReadsAligner.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("inputFile2", inputFile2TextField);
		textFields.put("fmIndexFile", fmIndexFileTextField);
		textFields.put("knownSTRsFile", knownSTRsFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("minProportionKmers", minProportionKmersTextField);
		textFields.put("minInsertLength", minInsertLengthTextField);
		textFields.put("maxInsertLength", maxInsertLengthTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(ReadsAligner.class.getName());
		fmIndexFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_ReadsAligner.bam");
		
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(fmIndexFileTextField.getText());
    			updateMessage(inputFileTextField.getText());
    			updateMessage(inputFile2TextField.getText());
    			updateMessage(knownSTRsFileTextField.getText());
    			updateMessage(kmerLengthTextField.getText());
    			updateMessage(minProportionKmersTextField.getText());
    			updateMessage(minInsertLengthTextField.getText());
    			updateMessage(maxInsertLengthTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				ReadsAligner instance = new ReadsAligner();
    				fillAttributes(instance);
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
