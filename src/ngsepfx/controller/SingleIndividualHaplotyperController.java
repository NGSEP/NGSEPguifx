package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import ngsep.haplotyping.SingleIndividualHaplotyper;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class SingleIndividualHaplotyperController extends AnalysisAreaController{

	public static final String TASK_NAME = "SIH";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField alignmentsFileTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField minMQTextField;
	
	@FXML
	private ChoiceBox<String> algorithmNameChoiceBox;
	
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/SingleIndividualHaplotyper.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("alignmentsFile", alignmentsFileTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("minMQ", minMQTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(SingleIndividualHaplotyper.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		algorithmNameChoiceBox.getItems().add(SingleIndividualHaplotyper.ALGORITHM_NAME_REFHAP);
		algorithmNameChoiceBox.getItems().add(SingleIndividualHaplotyper.ALGORITHM_NAME_DGS);
		algorithmNameChoiceBox.getSelectionModel().select(0);
		suggestOutputFile(file, outputFileTextField, "_phased.vcf");
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
    				SingleIndividualHaplotyper instance = new SingleIndividualHaplotyper();
    				fillAttributes(instance);
    				instance.setAlgorithmName(algorithmNameChoiceBox.getValue());
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
