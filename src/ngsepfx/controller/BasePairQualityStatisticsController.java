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
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ngsep.alignments.BasePairQualityStatisticsCalculator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.controller.SelectBasePairQualityStatsDialogController.AlignmentsFileData;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class BasePairQualityStatisticsController extends AnalysisAreaController {
	
	public static final String TASK_NAME = "Base Pair Quality Statics Calculator";
	
	@FXML
	private Label inputFileLabel;
	@FXML
	private ValidatedTextField inputFileTextField;
	@FXML
	private Button inputFileButton;
	@FXML
	private Button addFileButton;
	@FXML
	private Label genomeLabel;
	@FXML
	private ValidatedTextField genomeTextField;
	@FXML
	private Button genomeButton;
	@FXML
	private Label minMQLabel;
	@FXML
	private ValidatedTextField minMQTextField;
	@FXML
	private Label outputFileLabel;
	@FXML
	private ValidatedTextField outputFileTextField;
	private List<AlignmentsFileData> filesData;
	private String individualFile;
	
	private static String [] extensions = {".sam",".bam",".cram"};

	
	public static int getExtensionIndex (String filename) {
		String nameLC = filename.toLowerCase();
		for(int j = 0;j<extensions.length;j++) {
			if(nameLC.endsWith(extensions[j])) {
				return nameLC.lastIndexOf(extensions[j]);
			}
		}
		return -1;
	}

	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return "/ngsepfx/view/BasePairQualityStatistics.fxml";
	}
	
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("outputFile", outputFileTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("minMQ", minMQTextField);
		return textFields;
	}
	
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		// TODO Auto-generated method stub
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(BasePairQualityStatisticsCalculator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_QualityStatistics.txt");
		if (file.isDirectory()) {
			try {
				openSelectReadsDialog(file);
			} catch (IOException e) {
				showExecutionErrorDialog(Thread.currentThread().getName(), e);
				return;
			}
			inputFileTextField.setText("Selected "+filesData.size()+" samples");
			inputFileTextField.setEditable(false);
			inputFileButton.setDisable(true);
			outputFileLabel.setText("(*) Output directory:");
			outputFileTextField.setText(file.getAbsolutePath()+"_QualityStatistics.txt");
			}
		else {
			individualFile = inputFileTextField.getText();
			}
		}
	
	
	protected void changeOutput(ActionEvent event) {
		if(filesData!=null) super.changeOutputDirectory(event);
		else super.changeOutputFile(event);
	}
		
	
	
	private void openSelectReadsDialog(File directory) throws IOException {
		//Parent parent = FXMLLoader.load(getClass().getResource("/ngsepfx/view/SelectReadsForAlignmentDialog.fxml"));
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ngsepfx/view/SelectBasePairQualityStatsDialog.fxml"));
        Parent parent = fxmlLoader.load();
        SelectBasePairQualityStatsDialogController controller = fxmlLoader.getController();
        controller.setDirectory(directory);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        filesData = controller.getSelectedReadFilesData();
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
    				BasePairQualityStatisticsCalculator instance = new BasePairQualityStatisticsCalculator();
    				fillAttributes(instance);
    				ArrayList<String> inputFiles = new ArrayList<String>();
    				if(filesData!=null) {
    					for(AlignmentsFileData data:filesData) inputFiles.add(data.getFile().getAbsolutePath());
    				}
    				else {
    					inputFiles.add(individualFile);
    				}
    				instance.setInputFiles(inputFiles);
     				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputFile(), "");
    				log.addHandler(logHandler);
					instance.setOutputFile(outputFileTextField.getText());
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
