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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;
import ngsep.discovery.MultisampleVariantsDetector;
import ngsep.discovery.SingleSampleVariantPileupListener;
import ngsep.discovery.SingleSampleVariantsDetector;

/**
 * 
 * @author Jorge Duitama
 *
 */
public class VariantsDetectorController extends AnalysisAreaController {

	public static final String TASK_NAME = "VariantsDetector";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private Button inputFileButton;
	
	@FXML
	private Label outputFileLabel;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField knownSVsFileTextField;
	
	@FXML
	private ValidatedTextField knownSTRsFileTextField;
	
	@FXML
	private ValidatedTextField knownVariantsFileTextField;
	
	@FXML
	private ValidatedTextField minMQTextField;
	
	@FXML
	private ValidatedTextField basesToIgnore5PTextField;
	
	@FXML
	private ValidatedTextField basesToIgnore3PTextField;
	
	@FXML
	private ValidatedTextField sampleIdTextField;
	
	@FXML
	private ValidatedTextField normalPloidyTextField;
	
	@FXML
	private ValidatedTextField heterozygosityRateTextField;
	
	@FXML
	private ValidatedTextField minQualityTextField;
	
	@FXML
	private ValidatedTextField maxBaseQSTextField;
	
	@FXML
	private ValidatedTextField maxAlnsPerStartPosTextField;
	
	@FXML
	private ValidatedTextField minSVQualityTextField;
	
	@FXML
	private ValidatedTextField inputGenomeSizeTextField;
	
	@FXML
	private ValidatedTextField binSizeTextField;
	
	@FXML
	private ValidatedTextField maxPCTOverlapCNVsTextField;
	
	@FXML
	private ValidatedTextField maxLengthDeletionTextField;
	
	@FXML
	private ValidatedTextField splitReadSeedTextField;
	
	@FXML
	private CheckBox findRepeatsCheckBox;
	
	@FXML
	private CheckBox runRDAnalysisCheckBox;
	
	@FXML
	private CheckBox runRPAnalysisCheckBox;
	
	@FXML
	private CheckBox runOnlySVsAnalysesCheckBox;
	
	@FXML
	private CheckBox runLongReadSVsCheckBox;
	
	@FXML
	private CheckBox printSamplePloidyCheckBox;
	
	@FXML
	private CheckBox ignoreLowerCaseRefCheckBox;
	
	@FXML
	private CheckBox processNonUniquePrimaryAlignmentsCheckBox;
	
	@FXML
	private CheckBox processSecondaryAlignmentsCheckBox;

	@FXML
	private CheckBox callEmbeddedSNVsCheckBox;

	@FXML
	private CheckBox ignoreProperPairFlagCheckBox;
	
	@FXML
	private TitledPane svsTitledPane;
	
	private List<VariantsDetectorFileData> alnFilesData=null;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VariantsDetector.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("genome", genomeTextField);
		textFields.put("knownSTRsFile", knownSTRsFileTextField);
		textFields.put("knownSVsFile", knownSVsFileTextField);
		
		textFields.put("knownVariantsFile",knownVariantsFileTextField);
		textFields.put("minMQ", minMQTextField);
		textFields.put("basesToIgnore5P", basesToIgnore5PTextField);
		textFields.put("basesToIgnore3P", basesToIgnore3PTextField);
		textFields.put("sampleId", sampleIdTextField);
		textFields.put("normalPloidy", normalPloidyTextField);
		
		textFields.put("heterozygosityRate", heterozygosityRateTextField);
		textFields.put("minQuality", minQualityTextField);
		textFields.put("maxBaseQS", maxBaseQSTextField);
		textFields.put("maxAlnsPerStartPos", maxAlnsPerStartPosTextField);
		
		textFields.put("minSVQuality", minSVQualityTextField);
		textFields.put("inputGenomeSize", inputGenomeSizeTextField);
		textFields.put("binSize", binSizeTextField);
		textFields.put("maxPCTOverlapCNVs", maxPCTOverlapCNVsTextField);
		textFields.put("maxLengthDeletion", maxLengthDeletionTextField);
		textFields.put("splitReadSeed", splitReadSeedTextField);
		return textFields;
	}
	
	

	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("findRepeats", findRepeatsCheckBox);
		checkboxes.put("runRDAnalysis", runRDAnalysisCheckBox);
		checkboxes.put("runRPAnalysis", runRPAnalysisCheckBox);
		checkboxes.put("runOnlySVsAnalyses", runOnlySVsAnalysesCheckBox);
		checkboxes.put("runLongReadSVs", runLongReadSVsCheckBox);
		checkboxes.put("printSamplePloidy", printSamplePloidyCheckBox);
		checkboxes.put("ignoreLowerCaseRef", ignoreLowerCaseRefCheckBox);
		checkboxes.put("processNonUniquePrimaryAlignments", processNonUniquePrimaryAlignmentsCheckBox);
		checkboxes.put("processSecondaryAlignments", processSecondaryAlignmentsCheckBox);
		checkboxes.put("callEmbeddedSNVs", callEmbeddedSNVsCheckBox);
		checkboxes.put("ignoreProperPairFlag", ignoreProperPairFlagCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		if (file.isDirectory()) {
			try {
				openSelectAlnsDialog(file);
			} catch (IOException e) {
				e.printStackTrace();
				showExecutionErrorDialog(Thread.currentThread().getName(), e);
				return;
			}
			setDefaultValues(MultisampleVariantsDetector.class.getName());
			inputFileTextField.setText("Selected "+alnFilesData.size()+" samples");
			inputFileTextField.setEditable(false);
			inputFileButton.setDisable(true);
			sampleIdTextField.setText("");
			sampleIdTextField.setEditable(false);
			outputFileLabel.setText("(*) Output file:");
			outputFileTextField.setText(file.getAbsolutePath()+File.separator+"population.vcf");
			svsTitledPane.setVisible(false);
			findRepeatsCheckBox.setDisable(true);
			runRDAnalysisCheckBox.setDisable(true);
			runRPAnalysisCheckBox.setDisable(true);
			runOnlySVsAnalysesCheckBox.setDisable(true);
			runLongReadSVsCheckBox.setDisable(true);
		} else {
			setDefaultValues(SingleSampleVariantsDetector.class.getName());
			inputFileTextField.setText(file.getAbsolutePath());
			outputFileLabel.setText("(*) Output files prefix:");
			suggestOutputFile(file, outputFileTextField, "_variants");
			Set<String> sampleIds = SelectAlignmentsForVariantsDetectorController.findSampleIds(file);
			if(sampleIds!=null && sampleIds.size()==1) sampleIdTextField.setText(sampleIds.iterator().next());
		}
		//normalPloidyTextField.setOnAction((e)-> ploidyChanged());
	}
	
	private void openSelectAlnsDialog(File directory) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ngsepfx/view/SelectAlignmentsForVariantsDetectorDialog.fxml"));
        Parent parent = fxmlLoader.load();
        SelectAlignmentsForVariantsDetectorController controller = fxmlLoader.getController();
        controller.setDirectory(directory);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        alnFilesData = controller.getSelectedAlignmentFilesData();
	}
	@FXML
	public void ploidyChanged(KeyEvent event) {
		try {
			int ploidy = Integer.parseInt(normalPloidyTextField.getText());
			if(ploidy==1) heterozygosityRateTextField.setText(""+SingleSampleVariantPileupListener.DEF_HETEROZYGOSITY_RATE_HAPLOID);
			else heterozygosityRateTextField.setText(""+SingleSampleVariantPileupListener.DEF_HETEROZYGOSITY_RATE_DIPLOID);
			printSamplePloidyCheckBox.setSelected(ploidy!=2);
		} catch (NumberFormatException e) {
		}
		
		
		
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
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(outputFileTextField.getText(), "VD");
    				log.addHandler(logHandler);
					if(alnFilesData!=null) {
						MultisampleVariantsDetector instance = new MultisampleVariantsDetector();
						fillAttributes(instance);
						List<String> inputFiles = new ArrayList<String>();
						for(VariantsDetectorFileData data:alnFilesData) inputFiles.add(data.getFile().getAbsolutePath());
						instance.setInputFiles(inputFiles);
						instance.setOutFilename(outputFileTextField.getText());
						instance.setLog(log);
						instance.setProgressNotifier(this);
	    				instance.run();
					} else {
	    				SingleSampleVariantsDetector instance = new SingleSampleVariantsDetector();
	    				fillAttributes(instance);
	    				instance.setInputFile(inputFileTextField.getText());
	    				instance.setOutputPrefix(outputFileTextField.getText());
	    				instance.setLog(log);
	    				instance.setProgressNotifier(this);
	    				instance.run();	
					}
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
