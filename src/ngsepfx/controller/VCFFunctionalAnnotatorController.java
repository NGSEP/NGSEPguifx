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
import ngsep.vcf.VCFFunctionalAnnotator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class VCFFunctionalAnnotatorController extends AnalysisAreaController{

	public static final String TASK_NAME = "VCFAnnotate";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField transcriptomeFileTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField offsetUpstreamTextField;
	
	@FXML
	private ValidatedTextField offsetDownstreamTextField;
	
	@FXML
	private ValidatedTextField spliceDonorOffsetTextField;
	
	@FXML
	private ValidatedTextField spliceAcceptorOffsetTextField;
	
	@FXML
	private ValidatedTextField spliceRegionIntronOffsetTextField;
	
	@FXML
	private ValidatedTextField spliceRegionExonOffsetTextField;
	
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFFunctionalAnnotator.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("transcriptomeFile", transcriptomeFileTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("offsetUpstream", offsetUpstreamTextField);
		textFields.put("offsetDownstream", offsetDownstreamTextField);
		textFields.put("spliceDonorOffset", spliceDonorOffsetTextField);
		textFields.put("spliceAcceptorOffset", spliceAcceptorOffsetTextField);
		textFields.put("spliceRegionIntronOffset", spliceRegionIntronOffsetTextField);
		textFields.put("spliceRegionExonOffset", spliceRegionExonOffsetTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(VCFFunctionalAnnotator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_annotated.vcf");
		
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
    				VCFFunctionalAnnotator instance = new VCFFunctionalAnnotator();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputFile(), "Annotate");
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
