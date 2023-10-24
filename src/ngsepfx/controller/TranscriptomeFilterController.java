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
import java.util.logging.Logger;


import java.util.logging.FileHandler;


import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import ngsep.transcriptome.TranscriptomeFilter;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class TranscriptomeFilterController extends AnalysisAreaController  {
 
		
		public static final String TASK_NAME = "Transcriptome filter";
		private static final String FORMAT_GFF = "GFF";
		private static final String FORMAT_GENE_LIST = "Gene List";
		private static final String FORMAT_GENE_REGIONS = "Gene Regions";
		private static final String FORMAT_TRANSCRIPT_LIST = "Transcript List";
		private static final String FORMAT_TRANSCRIPT_REGIONS = "Transcript Regions";
		
		@FXML
		private Label outputFormat;
		@FXML
		private Label selectCompleteProteins;
		@FXML
		private Label minProteinLength;
		@FXML
		private Label regionsToFilter;
		@FXML
		private Label regionsToSelect;
		@FXML
		private Label intersectOnlyCoding;
		@FXML
		private Label geneIdsToFilter;
		@FXML
		private Label geneIdsToSelect;
		@FXML
		private ValidatedTextField inputFileTextField;
		@FXML
		private ValidatedTextField genomeTextField;
		@FXML
		private ValidatedTextField outputFileTextField;
		@FXML
		private ValidatedTextField minProteinLengthTextField;
		@FXML
		private ValidatedTextField regionsToFilterTextField;
		@FXML
		private ValidatedTextField regionsToSelectTextField;
		@FXML
		private ValidatedTextField geneIdsToFilterTextField;
		@FXML
		private ValidatedTextField geneIdsToSelectTextField;
		@FXML
		private ChoiceBox<String> outputFormatChoiceBox;
		@FXML
		private CheckBox selectCompleteProteinsCheckBox;
		@FXML
		private CheckBox intersectOnlyCodingCheckBox;
		
		
		
		@Override
		public String getFXMLResourcePath() {
			// TODO Auto-generated method stub
			return "/ngsepfx/view/TranscriptomeFilter.fxml";
		}
		public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
			Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
			textFields.put("inputFile", inputFileTextField);
			textFields.put("outputFile", outputFileTextField);
			textFields.put("genome", genomeTextField);
			textFields.put("minProteinLength", minProteinLengthTextField);
			textFields.put("regionsToFilter", regionsToFilterTextField);
			textFields.put("regionsToSelect", regionsToSelectTextField);
			textFields.put("geneIdsToFilter", geneIdsToFilterTextField);
			textFields.put("geneIdsToSelect", geneIdsToSelectTextField);
			return textFields;
		}
		
		
		@Override
		protected Map<String, CheckBox> getCheckBoxComponents() {
			Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
			checkboxes.put("selectCompleteProteins", selectCompleteProteinsCheckBox);
			checkboxes.put("intersectOnlyCoding", intersectOnlyCodingCheckBox);
			return checkboxes;
		}
		@Override
		public void handleActivationEvent(NGSEPEvent event) {
			// TODO Auto-generated method stub
			NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
			File file = analyzeEvent.file;
			setDefaultValues(TranscriptomeFilter.class.getName());
			inputFileTextField.setText(file.getAbsolutePath());
			outputFormatChoiceBox.getItems().add(FORMAT_GFF);
			outputFormatChoiceBox.getItems().add(FORMAT_GENE_LIST);
			outputFormatChoiceBox.getItems().add(FORMAT_GENE_REGIONS);
			outputFormatChoiceBox.getItems().add(FORMAT_TRANSCRIPT_LIST);
			outputFormatChoiceBox.getItems().add(FORMAT_TRANSCRIPT_REGIONS);
			outputFormatChoiceBox.getSelectionModel().select(0);
			suggestOutputFile(file, outputFileTextField, "_filtered.gff");
			
									
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
	    				TranscriptomeFilter instance = new TranscriptomeFilter();
	    				fillAttributes(instance);
	    				String outFormatValue = outputFormatChoiceBox.getValue();
	    				if (outFormatValue.equals("GFF")) {
	    					instance.setOutputFormat(TranscriptomeFilter.FORMAT_GFF);
	    				} else if (outFormatValue.equals("Gene List")) {
	    					instance.setOutputFormat(TranscriptomeFilter.FORMAT_GENE_LIST);
	    				} else if (outFormatValue.equals("Gene Regions")) {
	    					instance.setOutputFormat(TranscriptomeFilter.FORMAT_GENE_REGIONS);
	    				} else if (outFormatValue.equals("Transcript List")) {
	    					instance.setOutputFormat(TranscriptomeFilter.FORMAT_TRANSCRIPT_LIST);
	    				}  else if (outFormatValue.equals("Transcript Regions")) {
	    					instance.setOutputFormat(TranscriptomeFilter.FORMAT_TRANSCRIPT_REGIONS);
	    				} else {
	    					    					
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


