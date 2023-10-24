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

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ngsep.discovery.TillingPoolsIndividualGenotyper;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Jorge Duitama
 */
public class TillingPoolsIndividualGenotyperController extends AnalysisAreaController {

	public static final String TASK_NAME = "Tilling Pools Individual Genotyper";
	private String inputDirectory;
	private List<String> vcfFiles = new ArrayList<String>();
	
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private ValidatedTextField poolsDescriptorTextField;
	@FXML
	private ValidatedTextField genomeTextField;
	@FXML
	private ValidatedTextField outputFileTextField;
	@FXML
	private ValidatedTextField maxPoolsTextField;
	@FXML
	private CheckBox onlyBiallelicCheckBox;
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("poolsDescriptor", poolsDescriptorTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("maxPools", maxPoolsTextField);
		return textFields;
	}
	
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("onlyBiallelic", onlyBiallelicCheckBox);
		return checkboxes;
	}
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/TillingPoolsIndividualGenotyper.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(TillingPoolsIndividualGenotyper.class.getName());
		inputDirectory = file.getAbsolutePath();
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(file);
		chooser.setTitle("Select VCF files");
		chooser.getExtensionFilters().add(new ExtensionFilter("VCFFiles","*.vcf"));
		chooser.getExtensionFilters().add(new ExtensionFilter("VCFFiles","*.vcf.gz"));
		List<File> selected = chooser.showOpenMultipleDialog(null);
		for(File f:selected) vcfFiles.add(f.getAbsolutePath());
		selectedFilesLabel.setText("Selected "+vcfFiles.size()+" files");
		outputFileTextField.setText(inputDirectory+File.separator+"IndividualVariants.vcf");
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
    				
    				TillingPoolsIndividualGenotyper instance = new TillingPoolsIndividualGenotyper();
    				fillAttributes(instance);
    				instance.setInputFiles(vcfFiles.toArray(new String[0]));
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
