package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ngsep.genome.GenomesAligner;
import ngsep.simulation.SingleIndividualSimulator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class GenomesAlignerController extends AnalysisAreaController {

	public static final String TASK_NAME = "GenomeAligner";
	
	@FXML
	private Label genomeFileLabel;
	@FXML
	private Label genomeFile2Label;
	@FXML
	private Label transcriptomeFileLabel;
	@FXML
	private Label transcriptomeFile2Label;
	@FXML
	private Label outputPrefixLabel;	
	@FXML
	private Label kmerLengthLabel;
	@FXML
	private Label minimumPercetangeLabel;
	@FXML
	private Label skipMCLLabel;
	@FXML
	private ValidatedTextField genomeFileTextField;
	@FXML 
	private ValidatedTextField genomeFile2TextField;
	@FXML
	private ValidatedTextField transcriptomeFileTextField;
	@FXML
	private ValidatedTextField transcriptomeFile2TextField;
	@FXML
	private ValidatedTextField outputPrefixTextField;
	@FXML
	private ValidatedTextField kmerLengthTextField;
	@FXML
	private ValidatedTextField minimunPercentageTextField;
	@FXML
	private CheckBox skipMCLCheckBox;
	@FXML
	private Button genomeFileButton;
	@FXML
	private Button genomeFile2Button;
	@FXML
	private Button transcriptomeFileButton;
	@FXML
	private Button transcriptomeFile2Button;
	
	
	
	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return "/ngsepfx/view/GenomesAligner.fxml";

	}
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("genome", genomeFileTextField);
		textFields.put("genome", genomeFile2TextField);
		textFields.put("transcriptome", transcriptomeFileTextField);
		textFields.put("transcriptome", transcriptomeFile2TextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("maxHomologsUnit", kmerLengthTextField);
		return textFields;
	}
	@Override
	protected Map<String, CheckBox> getCheckBoxComponents() {
		Map<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
		checkboxes.put("skipMCL", skipMCLCheckBox);
		return checkboxes;
	}
	
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		// TODO Auto-generated method stub
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(GenomesAligner.class.getName());
		genomeFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "_GenAlign");
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		// TODO Auto-generated method stub
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(genomeFileTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				SingleIndividualSimulator instance = new SingleIndividualSimulator();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputPrefix(), "");
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
