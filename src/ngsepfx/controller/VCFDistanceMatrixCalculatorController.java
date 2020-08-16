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
import ngsep.vcf.VCFDistanceMatrixCalculator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 *
 */
public class VCFDistanceMatrixCalculatorController extends AnalysisAreaController {
	
	//Constants.
	
	public static final String TASK_NAME = "VCF Distance Matrix Calculator";
	
	private static final String DISTANCE_SOURCE_GENOTYPES_SIMPLE = "Genotype calls (GT field)";
	private static final String DISTANCE_SOURCE_GENOTYPES_COPY_NUMBER = "Alleles copy number (ACN field)";
	private static final String DISTANCE_SOURCE_COPY_NUMBER = "Total copy number for CNV calls";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ChoiceBox<String> distanceSourceChoiceBox;
	

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFDistanceMatrixCalculator.fxml";
	}
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		return textFields;
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#handleActivationEvent(ngsepfx.event.NGSEPEvent)
	 * #handleNGSEPEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(VCFDistanceMatrixCalculator.class.getName());
		distanceSourceChoiceBox.getItems().add(DISTANCE_SOURCE_GENOTYPES_SIMPLE);
		distanceSourceChoiceBox.getItems().add(DISTANCE_SOURCE_GENOTYPES_COPY_NUMBER);
		distanceSourceChoiceBox.getItems().add(DISTANCE_SOURCE_COPY_NUMBER);
		
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_DistanceMatrix.txt");
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
    				VCFDistanceMatrixCalculator instance = new VCFDistanceMatrixCalculator();
    				fillAttributes(instance);
    				fillDistanceSource(instance);
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
	private void fillDistanceSource(VCFDistanceMatrixCalculator instance) {
		String value = distanceSourceChoiceBox.getValue();
		if(DISTANCE_SOURCE_GENOTYPES_SIMPLE.equals(value)) instance.setDistanceSource(VCFDistanceMatrixCalculator.DISTANCE_SOURCE_GENOTYPES_SIMPLE);
		if(DISTANCE_SOURCE_GENOTYPES_COPY_NUMBER.equals(value)) instance.setDistanceSource(VCFDistanceMatrixCalculator.DISTANCE_SOURCE_GENOTYPES_COPY_NUMBER);
		if(DISTANCE_SOURCE_COPY_NUMBER.equals(value)) instance.setDistanceSource(VCFDistanceMatrixCalculator.DISTANCE_SOURCE_COPY_NUMBER);
	}
}
