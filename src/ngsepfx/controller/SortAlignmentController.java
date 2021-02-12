package ngsepfx.controller;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import net.sf.picard.sam.SortSam;
import ngsep.alignments.ReadAlignment;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.view.component.ValidatedTextField;

public class SortAlignmentController extends AnalysisAreaController{
	
	//Constants.
	
	public static final String TASK_NAME = "SortAlignment";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	@FXML
	private ChoiceBox<ReadAlignment.Platform> platformChoiceBox;
	
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/SortAlignement.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		inputFileTextField.setText(file.getAbsolutePath());
		platformChoiceBox.getItems().add(ReadAlignment.Platform.ILLUMINA);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.IONTORRENT);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.PACBIO);
		platformChoiceBox.getItems().add(ReadAlignment.Platform.ONT);
		platformChoiceBox.getSelectionModel().select(0);
		suggestOutputFile(file, outputFileTextField, "_sorted.bam");
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
    				//Log 
    				Logger log = Logger.getAnonymousLogger();
    				String outFile = inputFileTextField.getText().trim();
    				logHandler = createLogHandler(outFile, "Sort");
    				log.addHandler(logHandler);
    				sortAlignments(inputFileTextField.getText(), outputFileTextField.getText(), platformChoiceBox.getValue(), log);
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
	
	public static void sortAlignments(String inFile, String outFile, ReadAlignment.Platform platform, Logger log) {
		String [] args = new String [6];
		
		File f = new File (outFile);
		String tmpDir = f.getName();
		if (tmpDir.contains(".")) tmpDir = tmpDir.substring(0,tmpDir.lastIndexOf("."));
		tmpDir+="_tmpDir";
		File sortDirectory = new File(f.getParentFile().getAbsolutePath() + File.separator+tmpDir);
		if (!sortDirectory.exists()) {
			if (!sortDirectory.mkdirs()) {
				log.severe("Could not create temporary directory for sorting");
				return;
			} else {
				log.info("Temporary directory created");
			}
		}
		args[0] = "SORT_ORDER=coordinate";
		args[1] = "TMP_DIR=" + sortDirectory.getAbsolutePath();
		args[2] = "I=" + inFile;
		args[3] = "O=" + outFile;
		args[4] = "CREATE_INDEX=true";
		if(platform == ReadAlignment.Platform.PACBIO || platform == ReadAlignment.Platform.ONT ) {
			args[5] = "MAX_RECORDS_IN_RAM=20000";
		} else {
			args[5] = "MAX_RECORDS_IN_RAM=500000";
		}
		log.info("Sorting alignments. Arg input: "+args[2]+" argOutput: "+args[3]);
		new SortSam().instanceMain(args);
		log.info("Sorted alignments. Deleting temporary directory");
		try {
			sortDirectory.delete();
		} catch (Exception e) {
			log.warning("Can not delete temporary directory: "+serializeException(e));
		}
	}


}
