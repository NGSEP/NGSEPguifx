package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import ngsep.gbs.KmerPrefixReadsClusteringAlgorithm;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class DeNovoGBSController extends AnalysisAreaController {

	public static final String TASK_NAME = "DeNovoGBS";
	
	@FXML
	private ValidatedTextField inputDirectoryTextField;
	
	@FXML
	private ValidatedTextField outputPrefixTextField;
	
	@FXML
	private ValidatedTextField filesDescriptorTextField;
	
	@FXML
	private ValidatedTextField kmerLengthTextField;
	
	@FXML
	private ValidatedTextField maxNumClustersTextField;
	
	@FXML
	private ValidatedTextField numThreadsTextField;
	
	@FXML
	private ValidatedTextField maxBaseQSTextField;
	
	@FXML
	private ValidatedTextField minQualityTextField;
	
	@FXML
	private ValidatedTextField heterozygosityRateTextField;
	
	@FXML
	private ValidatedTextField normalPloidyTextField;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/DeNovoGBS.fxml";
	}

	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputDirectory", inputDirectoryTextField);
		textFields.put("outputPrefix", outputPrefixTextField);
		textFields.put("filesDescriptor", filesDescriptorTextField);
		textFields.put("kmerLength", kmerLengthTextField);
		textFields.put("maxNumClusters", maxNumClustersTextField);
		textFields.put("numThreads", numThreadsTextField);
		textFields.put("maxBaseQS", maxBaseQSTextField);
		textFields.put("minQuality", minQualityTextField);
		textFields.put("heterozygosityRate", heterozygosityRateTextField);
		textFields.put("normalPloidy", normalPloidyTextField);
		return textFields;
	}
	
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(KmerPrefixReadsClusteringAlgorithm.class.getName());
		inputDirectoryTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputPrefixTextField, "_DeNovoGBS.vcf");
		
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return new NGSEPTask<Void>() {	
    		@Override 
    		public Void call() {
    			updateMessage(inputDirectoryTextField.getText());
    			updateMessage(outputPrefixTextField.getText());
    			updateMessage(filesDescriptorTextField.getText());
    			updateMessage(kmerLengthTextField.getText());
    			updateMessage(maxNumClustersTextField.getText());
    			updateMessage(numThreadsTextField.getText());
    			updateMessage(maxBaseQSTextField.getText());
    			updateMessage(minQualityTextField.getText());
    			updateMessage(heterozygosityRateTextField.getText());
    			updateMessage(normalPloidyTextField.getText());
				updateTitle(TASK_NAME);
    			FileHandler logHandler = null;
    			try {
    				KmerPrefixReadsClusteringAlgorithm instance = new KmerPrefixReadsClusteringAlgorithm();
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
