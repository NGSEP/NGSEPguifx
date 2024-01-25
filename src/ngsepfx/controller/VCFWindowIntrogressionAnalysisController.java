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
import ngsep.vcf.VCFWindowIntrogressionAnalysis;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 *
 */
public class VCFWindowIntrogressionAnalysisController extends AnalysisAreaController {
	
	//Constants.
	
	public static final String TASK_NAME = "VCF Window Introgression Analysis";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField populationsFileTextField;

	@FXML
	private ValidatedTextField minPCTGenotypedTextField;
	
	@FXML
	private ValidatedTextField minDiffAFTextField;
	
	@FXML
	private ValidatedTextField maxMAFWithinTextField;
	
	@FXML
	private ValidatedTextField windowSizeTextField;
	
	@FXML
	private ValidatedTextField overlapTextField;
	
	@FXML
	private ValidatedTextField matchScoreTextField;
	
	@FXML
	private ValidatedTextField mismatchScoreTextField;
	
	@FXML
	private ValidatedTextField minScoreTextField;
	
	@FXML
	private CheckBox printVCFCheckBox;
	
	@FXML
	private CheckBox printUnassignedCheckBox;
	

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFWindowIntrogressionAnalysis.fxml";
	}
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("populationsFile", populationsFileTextField);
		textFields.put("minPCTGenotyped", minPCTGenotypedTextField);
		textFields.put("minDiffAF", minDiffAFTextField);
		textFields.put("maxMAFWithin", maxMAFWithinTextField);
		textFields.put("windowSize", windowSizeTextField);
		textFields.put("overlap", overlapTextField);
		textFields.put("matchScore", matchScoreTextField);
		textFields.put("mismatchScore", mismatchScoreTextField);
		textFields.put("minScore", minScoreTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("printVCF", printVCFCheckBox);
		checkboxes.put("printUnassigned", printUnassignedCheckBox);
		return checkboxes;
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#handleActivationEvent(ngsepfx.event.NGSEPEvent)
	 * #handleNGSEPEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(VCFWindowIntrogressionAnalysis.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "");
	}
	
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getTask()
	 */
	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				VCFWindowIntrogressionAnalysis instance = new VCFWindowIntrogressionAnalysis();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), "Introgression");
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
