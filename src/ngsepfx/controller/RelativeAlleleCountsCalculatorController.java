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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ngsep.discovery.RelativeAlleleCountsCalculator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class RelativeAlleleCountsCalculatorController extends AnalysisAreaController {

		public static final String TASK_NAME = "Allele Counts Calculator";
		
		@FXML
		private Label inputFileLabel;
		@FXML
		private Label genomeReferenceLabel;
		@FXML
		private Label regionsIncludeLabel;
		@FXML
		private Label regionsExcludeLabel;
		@FXML
		private Label outputFileLabel;
		@FXML
		private Label minimumReadDepthLabel;
		@FXML
		private Label maximumReadDepthLabel;
		@FXML
		private Label minimumScoreLabel;
		@FXML
		private ValidatedTextField inputFileTextField;
		@FXML
		private ValidatedTextField genomeReferenceTextField;
		@FXML
		private ValidatedTextField regionsIncludeTextField;
		@FXML
		private ValidatedTextField regionsExcludeTextField;
		@FXML
		private ValidatedTextField outputFileTextField;
		@FXML
		private ValidatedTextField outputFileFullCountsTextField;
		@FXML
		private ValidatedTextField minimumReadDepthTextField;
		@FXML
		private ValidatedTextField maximumReadDepthTextField;
		@FXML
		private ValidatedTextField minimumScoreTextField;
		@FXML
		private Button inputFileButton;
		@FXML
		private Button genomeReferenceButton;
		@FXML
		private Button regionsIncludeButton;
		@FXML
		private Button regionsExcludeButton;
		@FXML
		private Button outputFileButton;
		

		@Override
		public String getFXMLResourcePath() {
			// TODO Auto-generated method stub
			return "/ngsepfx/view/RelativeAlleleCountsCalculator.fxml";
		}
		public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
			Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
			textFields.put("inputFile", inputFileTextField);
			textFields.put("outputFile", outputFileTextField);
			textFields.put("outputFileFullCounts", outputFileFullCountsTextField);
			textFields.put("genome", genomeReferenceTextField);
			textFields.put("minRD", minimumReadDepthTextField);
			textFields.put("maxRD", maximumReadDepthTextField);
			textFields.put("regionsToFilter", regionsExcludeTextField);
			textFields.put("regionsToSelect", regionsIncludeTextField);
			textFields.put("minBaseQualityScore", minimumScoreTextField);
			return textFields;
		}
		@Override
		public void handleActivationEvent(NGSEPEvent event) {
			// TODO Auto-generated method stub
			NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
			File file = analyzeEvent.file;
			setDefaultValues(RelativeAlleleCountsCalculator.class.getName());
			inputFileTextField.setText(file.getAbsolutePath());
			suggestOutputFile(file, outputFileTextField, "_AlleleCounts.txt");
			
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
	    				RelativeAlleleCountsCalculator instance = new RelativeAlleleCountsCalculator();
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
