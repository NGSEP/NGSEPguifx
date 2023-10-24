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
import javafx.scene.control.Label;
import ngsep.simulation.SingleIndividualSimulator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class SingleIndividualSimulatorController extends AnalysisAreaController  {
	
	public static final String TASK_NAME = "SINGLEINDIVIDUAL simulator";
	
	@FXML
	private Label snvRate;
	@FXML
	private Label indelRate;
	@FXML
	private Label mutatedSTRFraction;
	@FXML
	private Label strsFile;
	@FXML
	private Label strUnitIndex;
	@FXML
	private Label sampleId;
	@FXML
	private Label ploidy;
	@FXML
	private ValidatedTextField inputFileTextField;
	@FXML
	private ValidatedTextField outputPrefixTextField;
	@FXML
	private ValidatedTextField snvRateTextField;
	@FXML
	private ValidatedTextField indelRateTextField;
	@FXML
	private ValidatedTextField mutatedSTRFractionTextField;
	@FXML
	private ValidatedTextField strUnitIndexTextField;
	@FXML
	private ValidatedTextField strsFileTextField;
	@FXML
	private ValidatedTextField sampleIdTextField;
	@FXML
	private ValidatedTextField ploidyTextField;
		
	

	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return "/ngsepfx/view/SingleIndividualSimulator.fxml";
	}
	
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
			Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
			textFields.put("genome", inputFileTextField);
			textFields.put("outputPrefix", outputPrefixTextField);
			textFields.put("snvRate", snvRateTextField);
			textFields.put("indelRate", indelRateTextField);
			textFields.put("mutatedSTRFraction", mutatedSTRFractionTextField);
			textFields.put("strsFile", strsFileTextField);
			textFields.put("strUnitIndex", strUnitIndexTextField);
			textFields.put("sampleId", sampleIdTextField);
			textFields.put("ploidy", ploidyTextField);
		
			return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		// TODO Auto-generated method stub
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(SingleIndividualSimulator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "_IndividualSimulation");
		
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		// TODO Auto-generated method stub
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				SingleIndividualSimulator instance = new SingleIndividualSimulator();
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
