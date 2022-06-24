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
import ngsep.variants.imputation.GenotypeImputer;

public class GenotypeImputerController extends AnalysisAreaController{

	public static final String TASK_NAME = "GenotypeImputer";

	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField parentIdsTextField;
	
	@FXML
	private ValidatedTextField numHaplotypeClustersTextField;
	
	@FXML
	private ValidatedTextField windowSizeTextField;
	
	@FXML
	private ValidatedTextField overlapTextField;
	
	@FXML
	private ValidatedTextField avgCMPerKbpTextField;

	@FXML
	private CheckBox skipTransitionsTrainingCheckBox;

	@FXML
	private CheckBox inbredParentsCheckBox;

	@FXML
	private CheckBox inbredSamplesCheckBox;

	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/GenotypeImputer.fxml";
	}

	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("parentIds", parentIdsTextField);
		textFields.put("numHaplotypeClusters", numHaplotypeClustersTextField);
		textFields.put("windowSize", windowSizeTextField);
		textFields.put("overlap", overlapTextField);
		textFields.put("avgCMPerKbp", avgCMPerKbpTextField);
		return textFields;
	}

	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("skipTransitionsTraining", skipTransitionsTrainingCheckBox);
		checkboxes.put("inbredParents", inbredParentsCheckBox);
		checkboxes.put("inbredSamples", inbredSamplesCheckBox);
		return checkboxes;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(GenotypeImputerController.class.getName());
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
					GenotypeImputer instance = new GenotypeImputer();
					fillAttributes(instance);
					//Log 
					Logger log = Logger.getAnonymousLogger();
					logHandler = new FileHandler(instance.getOutputPrefix()+"_Imputer.log");
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
