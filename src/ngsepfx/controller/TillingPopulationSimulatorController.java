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
import ngsep.simulation.TillingPopulationSimulator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 *
 */
public class TillingPopulationSimulatorController extends AnalysisAreaController {
	
	//Constants.
	
	public static final String TASK_NAME = "TILLING simulator";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField numIndividualsTextField;
	
	@FXML
	private ValidatedTextField numMutationsTextField;
	
	@FXML
	private ValidatedTextField numFragmentsPoolTextField;
	
	@FXML
	private ValidatedTextField readLengthTextField;
	
	@FXML
	private ValidatedTextField minErrorRateTextField;
	
	@FXML
	private ValidatedTextField maxErrorRateTextField;
	
	@FXML
	private ValidatedTextField poolDesignD1TextField;
	
	@FXML
	private ValidatedTextField poolDesignD2TextField;
	
	@FXML
	private ValidatedTextField poolDesignD3TextField;
	
	
	

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/TillingPopulationSimulator.fxml";
	}
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("sequencedRegionsFile", inputFileTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("numIndividuals", numIndividualsTextField);
		textFields.put("numMutations", numMutationsTextField);
		textFields.put("numFragmentsPool", numFragmentsPoolTextField);
		textFields.put("readLength", readLengthTextField);
		textFields.put("minErrorRate", minErrorRateTextField);
		textFields.put("maxErrorRate", maxErrorRateTextField);
		textFields.put("poolDesignD1", poolDesignD1TextField);
		textFields.put("poolDesignD2", poolDesignD2TextField);
		textFields.put("poolDesignD3", poolDesignD3TextField);
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
		setDefaultValues(TillingPopulationSimulator.class.getName());
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
    				TillingPopulationSimulator instance = new TillingPopulationSimulator();
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
