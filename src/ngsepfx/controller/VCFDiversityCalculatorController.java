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
import ngsep.vcf.VCFDiversityCalculator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 *
 */
public class VCFDiversityCalculatorController extends AnalysisAreaController {
	
	//Constants.
	
	public static final String TASK_NAME = "VCF Diversity Statistics";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField samplesMapFileTextField;
	

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFDiversityCalculator.fxml";
	}
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("samplesMap", samplesMapFileTextField);
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
		setDefaultValues(VCFDiversityCalculator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_DiversityStats.txt");
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
    				VCFDiversityCalculator instance = new VCFDiversityCalculator();
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
