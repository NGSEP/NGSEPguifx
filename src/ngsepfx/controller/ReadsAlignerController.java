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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
	private ValidatedTextField inputFile2TextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField fmIndexFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField knownSTRsFileTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField maxAlnsPerReadTextField;
	
	@FXML
	private ChoiceBox<String> inputFormatChoiceBox;
	
	@FXML
	private ValidatedTextField minInsertLengthTextField;
	
	@FXML
	private ValidatedTextField maxInsertLengthTextField;
	
	@FXML
	private ValidatedTextField windowLengthTextField;
	
	@FXML
	private CheckBox longReadsCheckBox;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/ReadsAligner.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("inputFile2", inputFile2TextField);
		textFields.put("genome", genomeTextField);
		textFields.put("fmIndexFile", fmIndexFileTextField);
		textFields.put("knownSTRsFile", knownSTRsFileTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("maxAlnsPerRead", maxAlnsPerReadTextField);
		textFields.put("minInsertLength", minInsertLengthTextField);
		textFields.put("maxInsertLength", maxInsertLengthTextField);
		textFields.put("windowLength", windowLengthTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("longReads", longReadsCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(ReadsAligner.class.getName());
		//Load history from v 4.0.1
		String savedIndexName = HistoryManager.getInstance().getLastGenomeIndexFile();
		if(savedIndexName!=null) fmIndexFileTextField.setText(savedIndexName);
		inputFileTextField.setText(file.getAbsolutePath());
		inputFormatChoiceBox.getItems().add("Fastq");
		inputFormatChoiceBox.getItems().add("Fasta");
		suggestOutputFile(file, outputFileTextField, ".bam");
		
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
    				ReadsAligner instance = new ReadsAligner();
    				fillAttributes(instance);
    				String inputFormatStr = inputFormatChoiceBox.getValue();
    				if("Fasta".equals(inputFormatStr)) instance.setInputFormat(ReadsAligner.INPUT_FORMAT_FASTA);
    				String outFileUnsorted = outputFileTextField.getText();
    				if(outFileUnsorted.toLowerCase().endsWith(".bam")) outFileUnsorted = outFileUnsorted.substring(0,outFileUnsorted.length()-4);
    				outFileUnsorted+="_unsorted.bam";
    				instance.setOutputFile(outFileUnsorted);
    				//Set explicitely FM index to reuse memory
    				if (!fmIndexFileTextField.getText().isEmpty()) {
    					ReferenceGenomeFMIndex index = HistoryManager.getInstance().getGenomeIndex(fmIndexFileTextField.getText());
        				instance.setFMIndex(index);
    				}
    				
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(outputFileTextField.getText(), "Aligner");
    				log.addHandler(logHandler);
    				instance.setLog(log);
    				instance.setProgressNotifier(this);
    				instance.run();
    				SortAlignmentController.sortAlignments(outFileUnsorted, outputFileTextField.getText(), log);
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
    			return null;
    		}
		};
	}

}
