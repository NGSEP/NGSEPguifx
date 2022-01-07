package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import ngsep.genome.GenomesAligner;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class GenomesAlignerController extends AnalysisAreaController {

	public static final String TASK_NAME = "Genomes aligner";
	
	@FXML
	private Label inputFileLabel;
	@FXML
	private Label inputDirectoryLabel;
	@FXML
	private Label outputPrefixLabel;	
	@FXML
	private Label kmerLengthLabel;
	@FXML
	private Label minimumPercentageLabel;
	@FXML
	private Label minFrequencySoftCoreLabel;
	@FXML
	private Label skipMCLLabel;

	@FXML
	private ValidatedTextField inputFileTextField;
	@FXML
	private ValidatedTextField inputDirectoryTextField;
	@FXML 
	private ValidatedTextField outputPrefixTextField;
	@FXML
	private ValidatedTextField kmerLengthTextField;
	@FXML
	private ValidatedTextField minimunPercentageTextField;
	@FXML
	private ValidatedTextField minFrequencySoftCoreTextField;
	@FXML
	private CheckBox skipMCLCheckBox;

	
	
	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return "/ngsepfx/view/GenomesAligner.fxml";

	}
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputDirectory", inputDirectoryTextField);
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("kmerLength",kmerLengthTextField);
		textFields.put("minPctKmers",minimunPercentageTextField);
		textFields.put("minFrequencySoftCore", minFrequencySoftCoreTextField);
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
		inputDirectoryTextField.setText(file.getAbsolutePath());		
		outputPrefixTextField.setText(file.getAbsolutePath() +File.separator + GenomesAligner.DEF_OUT_PREFIX);
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		// TODO Auto-generated method stub
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputDirectoryTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				GenomesAligner instance = new GenomesAligner();
    				fillAttributes(instance);
    				instance.setInputDirectory(inputDirectoryTextField.getText());
    				File f = new File(inputFileTextField.getText());
    				String fileName = f.getName();
    				instance.setInputFile(fileName);
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
