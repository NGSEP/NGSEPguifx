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
import ngsep.gbs.KmerPrefixReadsClusteringAlgorithm;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Andrea Parra
 *
 */
public class DeNovoGBSController extends AnalysisAreaController {

	public static final String TASK_NAME = "DeNovoGBS";
	
	@FXML
	private ValidatedTextField inputDirectoryTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField filesDescriptorTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField maxNumClustersTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private ValidatedTextField maxBaseQSTextField;
	
	@FXML
	private ValidatedTextField minQualityTextField;
	
	@FXML
	private ValidatedTextField heterozygosityRateTextField;
	
	@FXML
	private ValidatedTextField normalPloidyTextField;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/DeNovoGBS.fxml";
	}

	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputDirectory", inputDirectoryTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("filesDescriptor", filesDescriptorTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("maxNumClusters", maxNumClustersTextField);
		textFields.put("numThreads", numThreadsTextField);
		textFields.put("maxBaseQS", maxBaseQSTextField);
		textFields.put("minQuality", minQualityTextField);
		textFields.put("heterozygosityRate", heterozygosityRateTextField);
		textFields.put("normalPloidy", normalPloidyTextField);
		return textFields;
	}
	
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(KmerPrefixReadsClusteringAlgorithm.class.getName());
		inputDirectoryTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "_DeNovoGBS");
		
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputDirectoryTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				KmerPrefixReadsClusteringAlgorithm instance = new KmerPrefixReadsClusteringAlgorithm();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), null);
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
