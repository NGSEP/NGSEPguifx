package ngsepfx.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


import java.util.logging.FileHandler;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ngsep.vcf.VCFIndividualGenomeBuilder;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class VCFIndividualGenomeBuilderController extends AnalysisAreaController  {
 
		
		public static final String TASK_NAME = "Individual genome builder";
	
		@FXML
		private Label genomeInput;
		@FXML
		private Label inputVCF;
		@FXML
		private Label outputFile;
		@FXML
		private Label ploidy;
		@FXML
		private ValidatedTextField inputFileTextField;
		@FXML
		private ValidatedTextField inputVCFTextField;
		@FXML
		private ValidatedTextField outputFileTextField;
		@FXML
		private ValidatedTextField ploidyTextField;
		
		
		
		@Override
		public String getFXMLResourcePath() {
			// TODO Auto-generated method stub
			return "/ngsepfx/view/VCFIndividualGenomeBuilder.fxml";
		}
		public Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
			Map<String, ValidatedTextField> textFields = new HashMap<String, ValidatedTextField>();
			textFields.put("genome", inputFileTextField);
			textFields.put("variantsFile", inputVCFTextField);
			textFields.put("outputFile", outputFileTextField);
			textFields.put("ploidy", ploidyTextField);
			return textFields;
		}
	
		@Override
		public void handleActivationEvent(NGSEPEvent event) {
			// TODO Auto-generated method stub
			NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
			File file = analyzeEvent.file;
			setDefaultValues(VCFIndividualGenomeBuilder.class.getName());
			inputFileTextField.setText(file.getAbsolutePath());

			suggestOutputFile(file, outputFileTextField, "_GenomeBuilder.fa");
			
									
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
	    				VCFIndividualGenomeBuilder instance = new VCFIndividualGenomeBuilder();
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

