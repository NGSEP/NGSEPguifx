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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import ngsep.genome.ReferenceGenome;
import ngsep.vcf.VCFFilter;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.controller.fileexplorer.HistoryManager;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * @author Fernando Reyes
 *
 */
public class VCFFilterController extends AnalysisAreaController {
	
	//Constants.
	
	private static final String TASK_NAME = "VCF Filter";
	
	private static final String REFERENCE_GENOME_TASK_NAME = "Loading reference genome";
	
	//FXML Attributes.
		
		//Main Arguments
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField filterRegionsFileTextField;
	
	@FXML
	private ValidatedTextField selectRegionsFileTextField;
	
	@FXML
	private ValidatedTextField minDistanceTextField;
	
	@FXML
	private ValidatedTextField minMAFTextField;
	
	@FXML
	private ValidatedTextField maxMAFTextField;
	
	@FXML
	private ValidatedTextField minOHTextField;
	
	@FXML
	private ValidatedTextField maxOHTextField;
	
	@FXML
	private ValidatedTextField minSamplesGenotypedTextField;
	
	@FXML
	private ValidatedTextField maxSamplesCNVSTextField;
	
	@FXML
	private ValidatedTextField minGenotypeQualityTextField;
	
	@FXML
	private ValidatedTextField minReadDepthTextField;
	
	@FXML
	private CheckBox keepBiallelicSNVsCheckBox;
	
	@FXML
	private CheckBox filterInvariantCheckBox;
	
	@FXML
	private CheckBox filterInvariantAlternativeCheckBox;
	
	@FXML
	private CheckBox filterInvariantReferenceCheckBox;
	
		//GC Content Filter
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField minGCContentTextField;
	
	@FXML 
	private ValidatedTextField maxGCContentTextField;
	
		//Functional Filter
	
	@FXML
	private ValidatedTextField geneIdTextField;
	
	@FXML 
	private CheckBox synonymousVariantCheckbox;
	
	@FXML 
	private CheckBox missenseVariantCheckbox;
	
	@FXML 
	private CheckBox stopLostCheckbox;
	
	@FXML 
	private CheckBox stopGainedCheckbox;
	
	@FXML 
	private CheckBox startLostCheckbox;
	
	@FXML 
	private CheckBox inframeDeletionCheckbox;
	
	@FXML 
	private CheckBox inframeInsertionCheckbox;
	
	@FXML 
	private CheckBox frameshiftVariantCheckbox;
	
	@FXML 
	private CheckBox spliceDonorVariantCheckbox;
	
	@FXML 
	private CheckBox spliceAcceptorVariantCheckbox;
	
	@FXML 
	private CheckBox exonicSpliceRegionVariantCheckbox;
	
	@FXML 
	private CheckBox spliceRegionVariantCheckbox;
	
	@FXML 
	private CheckBox FivePrimeUTRVariantCheckbox;
	
	@FXML 
	private CheckBox ThreePrimeUTRVariantCheckbox;
	
	@FXML 
	private CheckBox nonCodingTranscriptExonVariantCheckbox;
	
	@FXML 
	private CheckBox upstreamTranscriptVariantCheckbox;
	
	@FXML 
	private CheckBox downstreamTranscriptVariantCheckbox;
	
	@FXML 
	private CheckBox intronVariantCheckbox;
	
	@FXML 
	private CheckBox intergenicVariantCheckbox;
	
		//Sample selection.
	
	@FXML
	private ComboBox<String> sampleSelectionComboBox;
	
	@FXML
	private ValidatedTextField sampleIdsFileTextField;
	
	

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getCSSResourcePath()
	 */
	@Override
	public String getCSSResourcePath() {
		return "/ngsepfx/view/VCFFilter.css";
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getFXMLResourcePath()
	 */
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFFilter.fxml";
	}
	
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getValidatedTextFieldComponents()
	 */
	@Override
	protected Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("regionsToFilter", filterRegionsFileTextField);
		textFields.put("regionsToSelect", selectRegionsFileTextField);
		textFields.put("minDistance", minDistanceTextField);
		textFields.put("minMAF", minMAFTextField);
		textFields.put("maxMAF", maxMAFTextField);
		textFields.put("minOH", minOHTextField);
		textFields.put("maxOH", maxOHTextField);
		textFields.put("minSamplesGenotyped", minSamplesGenotypedTextField);
		textFields.put("maxSamplesCNVs", maxSamplesCNVSTextField);
		textFields.put("minGenotypeQuality", minGenotypeQualityTextField);
		textFields.put("minReadDepth", minReadDepthTextField);
		textFields.put("minGCContent", minGCContentTextField);
		textFields.put("maxGCContent", maxGCContentTextField);
		textFields.put("geneId", geneIdTextField);
		textFields.put("sampleIdsFile", sampleIdsFileTextField);
		return textFields;
	}
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getCheckBoxComponents()
	 */
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("keepBiallelicSNVs", keepBiallelicSNVsCheckBox);
		checkboxes.put("filterInvariant", filterInvariantCheckBox);
		checkboxes.put("filterInvariantReference",filterInvariantReferenceCheckBox);
		checkboxes.put("filterInvariantAlternative", filterInvariantAlternativeCheckBox);
		return checkboxes;
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#handleActivationEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent ngsepAnalyzeFileEvent = (NGSEPAnalyzeFileEvent) event;
		File file = ngsepAnalyzeFileEvent.file;
		setDefaultValues(VCFFilter.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_filter.vcf");
	}
	
	private Set<String> encodeAnnotations() {
		Set<String>annotations = new TreeSet<String>();
		if(synonymousVariantCheckbox.isSelected()) {
			annotations.add(synonymousVariantCheckbox.getText());
		}
		if(missenseVariantCheckbox.isSelected()) {
			annotations.add(missenseVariantCheckbox.getText());
		}
		if(stopLostCheckbox.isSelected()) {
			annotations.add(stopLostCheckbox.getText());
		}
		if(stopGainedCheckbox.isSelected()) {
			annotations.add(stopGainedCheckbox.getText());
		}
		if(startLostCheckbox.isSelected()) {
			annotations.add(startLostCheckbox.getText());
		}
		if(inframeDeletionCheckbox.isSelected()) {
			annotations.add(inframeDeletionCheckbox.getText());
		}
		if(inframeInsertionCheckbox.isSelected()) {
			annotations.add(inframeInsertionCheckbox.getText());
		}
		if(frameshiftVariantCheckbox.isSelected()) {
			annotations.add(frameshiftVariantCheckbox.getText());
		}
		if(spliceDonorVariantCheckbox.isSelected()) {
			annotations.add(spliceDonorVariantCheckbox.getText());
		}
		if(spliceAcceptorVariantCheckbox.isSelected()) {
			annotations.add(spliceAcceptorVariantCheckbox.getText());
		}
		if(exonicSpliceRegionVariantCheckbox.isSelected()) {
			annotations.add(exonicSpliceRegionVariantCheckbox.getText());
		}
		if(spliceRegionVariantCheckbox.isSelected()) {
			annotations.add(spliceRegionVariantCheckbox.getText());
		}
		if(FivePrimeUTRVariantCheckbox.isSelected()) {
			annotations.add(FivePrimeUTRVariantCheckbox.getText());
		}
		if(ThreePrimeUTRVariantCheckbox.isSelected()) {
			annotations.add(ThreePrimeUTRVariantCheckbox.getText());
		}
		if(nonCodingTranscriptExonVariantCheckbox.isSelected()) {
			annotations.add(nonCodingTranscriptExonVariantCheckbox.getText());
		}
		if(upstreamTranscriptVariantCheckbox.isSelected()) {
			annotations.add(upstreamTranscriptVariantCheckbox.getText());
		}
		if(downstreamTranscriptVariantCheckbox.isSelected()) {
			annotations.add(downstreamTranscriptVariantCheckbox.getText());
		}
		if(intronVariantCheckbox.isSelected()) {
			annotations.add(intronVariantCheckbox.getText());
		}
		if(intergenicVariantCheckbox.isSelected()) {
			annotations.add(intergenicVariantCheckbox.getText());
		}
		return annotations;
	}
	
	
	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#getTask()
	 */
	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {
    		@Override 
    		public Void call() {
    			//Progress bar info.
				updateMessage(inputFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				VCFFilter instance = new VCFFilter();
    				fillAttributes(instance);
    				//The genome is set manually to avoid unwanted filtering
    				if(genomeTextField.getText().length()>0) {
    					ReferenceGenome savedGenome = HistoryManager.getInstance().getGenome(genomeTextField.getText());
    					instance.setGenome(savedGenome);
    				}
    				
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputFile(), "VCFFilter");
    				log.addHandler(logHandler);
    				instance.setLog(log);
    				instance.setProgressNotifier(this);
    				
    					//Functional Filter
    				Set<String> annotations = encodeAnnotations();
    				if(annotations.size()>0) instance.setAnnotations(annotations);
    				
    					//Sample Selection.
    				
    				if (!sampleIdsFileTextField.getText().trim().isEmpty()) {
    					instance.setFilterSamples(sampleSelectionComboBox.getSelectionModel().getSelectedIndex()==0);
    				}
    				
    					//GC Content Filter.
    				
    				if(!genomeTextField.getText().trim().isEmpty()) {
    					//Progress bar info.
	    				updateMessage(genomeTextField.getText());
	    				updateTitle(TASK_NAME + ":" + REFERENCE_GENOME_TASK_NAME);
    					ReferenceGenome genome = HistoryManager.getInstance().getGenome(genomeTextField.getText().trim());
	    				instance.setGenome(genome);    			
    				}
    				
    				//Progress bar info.
    				updateMessage(inputFileTextField.getText());
    				updateTitle(TASK_NAME);
    				
    				//Start analysis.
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
