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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ngsep.genome.GenomicRegionSortedCollection;
import ngsep.genome.ReferenceGenome;
import ngsep.sequences.QualifiedSequenceList;
import ngsep.variants.GenomicVariant;
import ngsep.vcf.ConsistentVCFFilesMerge;
import ngsep.vcf.IndividualSampleVariantsMerge;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.controller.fileexplorer.HistoryManager;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Erick Duarte
 */
public class VariantFilesMergeController extends AnalysisAreaController {

	public static final String TASK_NAME = "VariantFilesMerge";
	private String inputDirectory;
	private List<String> vcfFiles = new ArrayList<String>();
	private boolean mergeVariants = false;
	
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private ValidatedTextField outputFileTextField;
	@FXML
	private ValidatedTextField genomeTextField;
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("genome", genomeTextField);
		return textFields;
	}
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VariantFilesMerge.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		inputDirectory = file.getAbsolutePath();
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(file);
		chooser.setTitle("Select VCF files");
		chooser.getExtensionFilters().add(new ExtensionFilter("VCFFiles","*.vcf"));
		chooser.getExtensionFilters().add(new ExtensionFilter("VCFFiles","*.vcf.gz"));
		List<File> selected = chooser.showOpenMultipleDialog(null);
		for(File f:selected) vcfFiles.add(f.getAbsolutePath());
		String genomeFile = HistoryManager.getInstance().getLastGenomeFile();
		//System.out.println("Genome file "+genomeFile);
		if(genomeFile!=null) genomeTextField.setText(genomeFile);
		selectedFilesLabel.setText("Selected "+vcfFiles.size()+" files");
		outputFileTextField.setText(inputDirectory+File.separator+"variants.vcf");
	}
	@FXML
	private void mergeVariants(ActionEvent actionEvent) {
		mergeVariants = true;
		if (!validateFields()) return;
		NGSEPTask<Void> task = getTask(); 
		if (task!=null) getRootNode().fireEvent(new NGSEPExecuteTaskEvent(task));
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {
			@Override
			protected Void call() throws Exception {
				updateMessage(inputDirectory);
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				String outputFile = outputFileTextField.getText();
    				logHandler = createLogHandler(outputFile, "Merge");
    				log.addHandler(logHandler);
    				ReferenceGenome savedGenome = HistoryManager.getInstance().getGenome(genomeTextField.getText());
    				QualifiedSequenceList metadata = savedGenome.getSequencesMetadata();
    				if(mergeVariants) {
    					IndividualSampleVariantsMerge instance = new IndividualSampleVariantsMerge();
    					instance.setLog(log);
    					instance.setProgressNotifier(this);
    					GenomicRegionSortedCollection<GenomicVariant> variants = instance.mergeVariants(vcfFiles,metadata);
    					log.info("Merged variants. Total "+variants.size());
    					instance.printVariants(outputFile, variants);
    				} else {
    					ConsistentVCFFilesMerge instance = new ConsistentVCFFilesMerge();
    					instance.setLog(log);
    					instance.setProgressNotifier(this);
    					instance.mergeFiles(metadata, vcfFiles, outputFile);
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
