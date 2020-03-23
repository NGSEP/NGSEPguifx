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
import ngsep.alignments.ReadsAligner;
import ngsep.discovery.VariantsDetector;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class VariantsDetectorController extends AnalysisAreaController {

	public static final String TASK_NAME = "VariantsDetector";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField knownSVsTextField;
	
	@FXML
	private ValidatedTextField knownSTRsFileTextField;
	
	@FXML
	private ValidatedTextField knownVariantsFileTextField;
	
	@FXML
	private ValidatedTextField minMQTextField;
	
	@FXML
	private ValidatedTextField basesToIgnore5PTextField;
	
	@FXML
	private ValidatedTextField basesToIgnore3PTextField;
	
	@FXML
	private ValidatedTextField sampleIdTextField;
	
	@FXML
	private ValidatedTextField normalPloidyTextField;
	
	@FXML
	private ValidatedTextField heterozygosityRateTextField;
	
	@FXML
	private ValidatedTextField minQualityTextField;
	
	@FXML
	private ValidatedTextField maxBaseQSTextField;
	
	@FXML
	private ValidatedTextField minSVQualityTextField;
	
	@FXML
	private ValidatedTextField inputGenomeSizeTextField;
	
	@FXML
	private ValidatedTextField binSizeTextField;
	
	@FXML
	private ValidatedTextField maxPCTOverlapCNVsTextField;
	
	@FXML
	private ValidatedTextField maxLengthDeletionTextField;
	
	@FXML
	private ValidatedTextField splitReadTextField;
	
	@FXML
	private CheckBox findRepeatsCheckBox;
	
	@FXML
	private CheckBox runRDAnalysisCheckBox;
	
	@FXML
	private CheckBox runRPAnalysisCheckBox;
	
	@FXML
	private CheckBox runOnlySVsAnalysesCheckBox;
	
	@FXML
	private CheckBox printSamplePloidyCheckBox;
	
	@FXML
	private CheckBox ignoreLowerCaseRefCheckBox;
	
	@FXML
	private CheckBox processNonUniquePrimaryAlignmentsCheckBox;
	
	@FXML
	private CheckBox processSecondaryAlignmentsCheckBox;

	@FXML
	private CheckBox callEmbeddedSNVsCheckBox;

	@FXML
	private CheckBox ignoreProperPairFlagCheckBox;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VariantsDetector.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		
		textFields.put("knownSTRsFile", knownSTRsFileTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(ReadsAligner.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, inputFileTextField, "_variants");
		
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
    				VariantsDetector instance = new VariantsDetector();
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
