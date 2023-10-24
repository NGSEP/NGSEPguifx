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
import javafx.scene.control.Label;
import ngsep.simulation.SingleReadsSimulator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class SingleReadsSimulatorController extends AnalysisAreaController {
	
	
	public static final String TASK_NAME = "Single reads simulator";


	@FXML
	private Label numberOfReads;
	@FXML
	private Label meanReadLength;
	@FXML
	private Label stdevReadlength;
	@FXML
	private Label minReadLength;
	@FXML
	private Label substitutionErrorRate;
	@FXML
	private Label indelErrorRate;
	@FXML
	private Label outFormat;
	@FXML
	private ValidatedTextField inputFileTextField;
	@FXML
	private ValidatedTextField outputFileTextField;
	@FXML
	private ValidatedTextField numberOfReadsTextField;
	@FXML
	private ValidatedTextField meanReadLenghtTextField;
	@FXML
	private ValidatedTextField stdevReadLengthTextField;
	@FXML
	private ValidatedTextField minReadLenghtTextField;
	@FXML
	private ValidatedTextField substitutionErrorRateTextField;
	@FXML
	private ValidatedTextField indelErrorRateTextField;
	@FXML
	private ChoiceBox<String> outFormatChoiceBox;
	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return  "/ngsepfx/view/SingleReadsSimulator.fxml";
	}
	
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("genome", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("numberOfReads", numberOfReadsTextField);
		textFields.put("meanReadLength", meanReadLenghtTextField);
		textFields.put("stdevReadLength", stdevReadLengthTextField);
		textFields.put("minReadLength", minReadLenghtTextField);
		textFields.put("substitutionErrorRate", substitutionErrorRateTextField);
		textFields.put("indelErrorRate", indelErrorRateTextField);
	
		return textFields;
	}
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		// TODO Auto-generated method stub
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(SingleReadsSimulator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		outFormatChoiceBox.getItems().add(FORMAT_FASTQ);
		outFormatChoiceBox.getItems().add(FORMAT_FASTA);
		outFormatChoiceBox.getSelectionModel().select(0);
		suggestOutputFile(file, outputFileTextField, "_ReadsSimulation.fastq.gz");
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
	    				SingleReadsSimulator instance = new SingleReadsSimulator();
	    				fillAttributes(instance);
	    				String outFormatValue = outFormatChoiceBox.getValue();
	    				if (outFormatValue.equals("fasta")) {
	    					instance.setOutFormat(SingleReadsSimulator.OUT_FORMAT_FASTA);
	    				}
	    				
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

