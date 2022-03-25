package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import ngsep.transcriptome.MutatedPeptidesExtractor;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class MutatedPeptidesExtractorController extends AnalysisAreaController{

	public static final String TASK_NAME = "MutatedPeptidesExtractor";
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ValidatedTextField transcriptomeFileTextField;
	
	@FXML
	private ValidatedTextField genomeTextField;
	
	@FXML
	private ValidatedTextField mutatedSampleIdTextField;
	
	@FXML
	private ValidatedTextField controlSampleIdTextField;
	
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/MutatedPeptidesExtractor.fxml";
	}
	
	@Override
	public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
		textFields.put("inputFile", inputFileTextField);
		textFields.put("outputFile", outputFileTextField);
		textFields.put("transcriptomeFile", transcriptomeFileTextField);
		textFields.put("genome", genomeTextField);
		textFields.put("offsetUpstream", mutatedSampleIdTextField);
		textFields.put("offsetDownstream", controlSampleIdTextField);
		return textFields;
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		setDefaultValues(MutatedPeptidesExtractor.class.getName());
		inputFileTextField.setText(file.getAbsolutePath());
		suggestOutputFile(file, outputFileTextField, "_peptides.fa");
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
    				MutatedPeptidesExtractor instance = new MutatedPeptidesExtractor();
    				fillAttributes(instance);
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				logHandler = createLogHandler(instance.getOutputFile(), "peptides");
    				log.addHandler(logHandler);
    				instance.setLog(log);
    				instance.setProgressNotifier(this);
    				instance.run();
    				/*ReferenceGenome genome = new ReferenceGenome(genomeTextField.getText());
    				instance.loadTranscriptome(transcriptomeFileTextField.getText(), genome);
    				try (PrintStream out = new PrintStream(outputFileTextField.getText())) {
    					instance.findMutatedPeptides(inputFileTextField.getText(), out);
    				}*/
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
