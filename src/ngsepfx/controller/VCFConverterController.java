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
import java.util.logging.SimpleFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;
import ngsep.vcf.VCFConverter;

/**
 * @author Erick Duarte
 */
public class VCFConverterController extends AnalysisAreaController{

	public static final String TASK_NAME = "VCFConverter";

	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField sequenceNameTextField;
	
	@FXML
	private ValidatedTextField populationFileTextField;
	
	@FXML
	private ValidatedTextField idParent1TextField;
	
	@FXML
	private ValidatedTextField idParent2TextField;

	@FXML
	private CheckBox printDarwinCheckBox;

	@FXML
	private CheckBox printEigensoftCheckBox;

	@FXML
	private CheckBox printEmmaCheckBox;

	@FXML
	private CheckBox printFastaCheckBox;

	@FXML
	private CheckBox printFineStructureCheckBox;

	@FXML
	private CheckBox printFlapjackCheckBox;

	@FXML
	private CheckBox printGenePopCheckBox;
	
	@FXML
	private CheckBox printGWASPolyCheckBox;

	@FXML
	private CheckBox printHaploviewCheckBox;

	@FXML
	private CheckBox printHapmapCheckBox;
	
	@FXML
	private CheckBox printJoinMapCheckBox;
	
	@FXML
	private CheckBox printMatrixCheckBox;
	
	@FXML
	private CheckBox printPhaseCheckBox;
	
	@FXML
	private CheckBox printPlinkCheckBox;
	
	@FXML
	private CheckBox printPowerMarkerCheckBox;
	
	@FXML
	private CheckBox printrrBLUPCheckBox;
	
	@FXML
	private CheckBox printSpagediCheckBox;
	
	@FXML
	private CheckBox printStructureCheckBox;
	
	@FXML
	private CheckBox printTreeMixCheckBox;

	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/VCFConverter.fxml";
	}

	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("sequenceName", sequenceNameTextField);
		textFields.put("populationFile", populationFileTextField);
		textFields.put("idParent1", idParent1TextField);
		textFields.put("idParent2", idParent2TextField);
		return textFields;
	}

	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("printDarwin", printDarwinCheckBox);
		checkboxes.put("printEigensoft", printEigensoftCheckBox);
		checkboxes.put("printEmma", printEmmaCheckBox);
		checkboxes.put("printFasta", printFastaCheckBox);
		checkboxes.put("printFineStructure", printFineStructureCheckBox);
		checkboxes.put("printFlapjack", printFlapjackCheckBox);
		checkboxes.put("printGenePop", printGenePopCheckBox);
		checkboxes.put("printGWASPoly", printGWASPolyCheckBox);
		checkboxes.put("printHaploview", printHaploviewCheckBox);
		checkboxes.put("printHapmap", printHapmapCheckBox);
		checkboxes.put("printJoinMap", printJoinMapCheckBox);
		checkboxes.put("printMatrix", printMatrixCheckBox);
		checkboxes.put("printPhase", printPhaseCheckBox);
		checkboxes.put("printPlink", printPlinkCheckBox);
		checkboxes.put("printPowerMarker", printPowerMarkerCheckBox);
		checkboxes.put("printrrBLUP", printrrBLUPCheckBox);
		checkboxes.put("printSpagedi", printSpagediCheckBox);
		checkboxes.put("printStructure", printStructureCheckBox);
		checkboxes.put("printTreeMix", printTreeMixCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(VCFConverter.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "");

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
					VCFConverter instance = new VCFConverter();
					fillAttributes(instance);
					//Log 
					Logger log = Logger.getAnonymousLogger();
					logHandler = new FileHandler(instance.getOutputPrefix()+"_Converter.log");
					logHandler.setFormatter(new SimpleFormatter());
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
