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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ngsep.alignments.ReadAlignment;
import ngsep.alignments.ReadsAligner;
import ngsep.genome.ReferenceGenomeFMIndex;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.controller.fileexplorer.HistoryManager;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * 
 * @author Maria del Rosario Leon
 *
 */
public class ReadsAlignerController extends AnalysisAreaController {

	public static final String TASK_NAME = "ReadsAligner";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private Button inputFileButton;
	
	@FXML
	private ValidatedTextField inputFile2TextField;
	
	@FXML
	private Button inputFile2Button;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField fmIndexFileTextField;
	
	@FXML
	private Label outputFileLabel;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField knownSTRsFileTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField maxAlnsPerReadTextField;
	
	@FXML
	private ChoiceBox<ReadAlignment.Platform> platformChoiceBox;
	
	@FXML
	private ValidatedTextField sampleIdTextField;
	
	@FXML
	private ChoiceBox<String> inputFormatChoiceBox;
	
	@FXML
	private ValidatedTextField minInsertLengthTextField;
	
	@FXML
	private ValidatedTextField maxInsertLengthTextField;
	
	@FXML
	private ValidatedTextField windowLengthTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	private List<ReadsAlignerFileData> filesData;
	private boolean pairedEnd = false;
	
	private static String [] extensions = {".fastq",".fastq.gz",".fq",".fq.gz", ".fasta",".fasta.gz",".fa",".fa.gz"};
	
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
		return "/ngsepfx/view/ReadsAligner.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("genome", genomeTextField);
		textFields.put("knownSTRsFile", knownSTRsFileTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("maxAlnsPerRead", maxAlnsPerReadTextField);
		textFields.put("minInsertLength", minInsertLengthTextField);
		textFields.put("maxInsertLength", maxInsertLengthTextField);
		textFields.put("windowLength", windowLengthTextField);
		textFields.put("numThreads", numThreadsTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(ReadsAligner.class.getName());
		//Load history
		String savedIndexName = HistoryManager.getInstance().getLastGenomeIndexFile();
		if(savedIndexName!=null) fmIndexFileTextField.setText(savedIndexName);
		inputFormatChoiceBox.getItems().add(FORMAT_FASTQ);
		inputFormatChoiceBox.getItems().add(FORMAT_FASTA);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.ILLUMINA);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.IONTORRENT);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.PACBIO);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.ONT);
		platformChoiceBox.getSelectionModel().select(0);
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
			inputFile2TextField.setEditable(false);
			inputFile2Button.setDisable(true);
			sampleIdTextField.setEditable(false);
			inputFormatChoiceBox.setDisable(true);
			outputFileLabel.setText("(*) Output directory:");
			outputFileTextField.setText(file.getAbsolutePath());
			if (pairedEnd) {
				platformChoiceBox.setDisable(true);
				windowLengthTextField.setDisable(true);
				inputFile2TextField.setText("Files are paired-end");
			}
			else {
				minInsertLengthTextField.setEditable(false);
				maxInsertLengthTextField.setEditable(false);
			}
		} else {
			inputFileTextField.setText(file.getAbsolutePath());
			String filename = file.getName();
			int k = getExtensionIndex(filename);
			if(k>0) {
				sampleIdTextField.setText(filename.substring(0,k));
				if(filename.toLowerCase().substring(k).startsWith(".fastq")) inputFormatChoiceBox.getSelectionModel().select(0);
				else inputFormatChoiceBox.getSelectionModel().select(1);
			}
			suggestOutputFile(file, outputFileTextField, ".bam");
		}
	}
	@FXML
	protected void changeOutput(ActionEvent event) {
		if(filesData!=null) super.changeOutputDirectory(event);
		else super.changeOutputFile(event);
	}

	private void openSelectReadsDialog(File directory) throws IOException {
		//Parent parent = FXMLLoader.load(getClass().getResource("/ngsepfx/view/SelectReadsForAlignmentDialog.fxml"));
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ngsepfx/view/SelectReadsForAlignmentDialog.fxml"));
        Parent parent = fxmlLoader.load();
        SelectReadsForAlignmentController controller = fxmlLoader.getController();
        controller.setDirectory(directory);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        filesData = controller.getSelectedReadFilesData();
        pairedEnd = controller.isPairedEnd();
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputFileTextField.getText());
				updateTitle(TASK_NAME);
				if(filesData!=null) {
					if(this.isCancelled()) return null;
					File outDir = new File (outputFileTextField.getText());
					if (!outDir.exists()) return null;
					if (!outDir.canWrite()) return null;
					for(ReadsAlignerFileData fileData:filesData) {
						System.out.println("Processing sample "+fileData.getSampleId()+" file: "+fileData.getFile());
						fileData.setOutBamFile(outDir.getAbsolutePath()+File.separator+fileData.getOutBamFile());
						processAlignmentsFile(fileData, this);
						if(this.isCancelled()) break;
					}
				} else {
					File f1 = new File(inputFileTextField.getText());
					File f2 = new File(inputFile2TextField.getText());
					if(f1.exists()) {
						ReadsAlignerFileData singleFileData = new ReadsAlignerFileData(sampleIdTextField.getText(), f1);
						if(f2.exists()) singleFileData.setFile2(f2);
						singleFileData.setInputFormat(inputFormatChoiceBox.getValue());
						singleFileData.setOutBamFile(outputFileTextField.getText());
						processAlignmentsFile(singleFileData, this);
					}
				}
    			
    			return null;
    		}
		};
	}

	private void processAlignmentsFile(ReadsAlignerFileData data, NGSEPTask<Void> task) {
		FileHandler logHandler = null;
		try {
			ReadsAligner instance = new ReadsAligner();
			fillAttributes(instance);
			instance.setPlatform(platformChoiceBox.getValue());
			instance.setInputFile(data.getFile().getAbsolutePath());
			if(data.getFile2()!=null) instance.setInputFile2(data.getFile2().getAbsolutePath());
			String ifStr = data.getInputFormat();
			if(FORMAT_FASTA.equals(ifStr)) instance.setInputFormat(ReadsAligner.INPUT_FORMAT_FASTA);
			instance.setSampleId(data.getSampleId());
			
			String outFileSorted = data.getOutBamFile();
			String outFilePrefix = outFileSorted;
			if(outFilePrefix.toLowerCase().endsWith(".bam")) outFilePrefix = outFilePrefix.substring(0,outFilePrefix.length()-4);
			String outFileUnsorted = outFilePrefix+"_unsorted.bam";
			instance.setOutputFile(outFileUnsorted);
			//Log 
			Logger log = Logger.getAnonymousLogger();
			logHandler = createLogHandler(outFilePrefix, "Aligner");
			log.addHandler(logHandler);
			//Set explicitely FM index to reuse memory
			if (!fmIndexFileTextField.getText().isEmpty()) {
				ReferenceGenomeFMIndex index = HistoryManager.getInstance().getGenomeIndex(instance.getGenome(),fmIndexFileTextField.getText(), log);
				instance.setFmIndex(index);
			}
			
			instance.setLog(log);
			instance.setProgressNotifier(task);
			instance.run();
			SortAlignmentController.sortAlignments(outFileUnsorted, outFileSorted, platformChoiceBox.getValue(), log);
			//Delete unsorted file
			File f = new File(outFileUnsorted);
			if(f.exists()) f.delete();
		} catch (Exception e) {
			e.printStackTrace();
			showExecutionErrorDialog(Thread.currentThread().getName(), e);
		} finally {
			if(logHandler!=null) {
				logHandler.flush();
				logHandler.close();
			}
		}
	}
}
