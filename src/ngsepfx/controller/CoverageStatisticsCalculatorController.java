package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ngsep.discovery.CoverageStatisticsCalculator;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class CoverageStatisticsCalculatorController extends AnalysisAreaController {
	public static final String TASK_NAME = "Coverage Statistics Calculator";
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
	
	
	@Override
	public String getFXMLResourcePath() {
		// TODO Auto-generated method stub
		return "/ngsepfx/view/CoverageStatisticsCalculator.fxml";
	}

	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
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
		setDefaultValues(CoverageStatisticsCalculator.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_CoverageStatistics.txt");
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
    				CoverageStatisticsCalculator instance = new CoverageStatisticsCalculator();
    				fillAttributes(instance);
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
