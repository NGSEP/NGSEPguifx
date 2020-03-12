package ngsepfx.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import net.sf.picard.sam.SortSam;
import ngsep.alignments.ReadsAligner;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;
import ngsepfx.view.component.ValidatedTextField;

public class SortAlignmentController extends AnalysisAreaController{
	
	//Constants.
	
	public static final String TASK_NAME = "SortAlignement";
	
	//FXML parameters.
	
	@FXML
	private ValidatedTextField inputFileTextField;
	
	@FXML
	private ValidatedTextField outputFileTextField;
	
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/SortAlignement.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		// TODO Auto-generated method stub
		
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
    				logHandler = createLogHandler(outFile, "");
    				log.addHandler(logHandler);
    				sortAlignments(inputFileTextField.getText(), outputFileTextField.getText(), log);
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
	
	public static void sortAlignments(String inFile, String outFile, Logger log) {
		String [] args = new String [5];
		
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
		log.info("Sorting alignments");
		new SortSam().instanceMain(args);
		log.info("Sorted alignments. Deleting temporary directory");
		try {
			sortDirectory.delete();
		} catch (Exception e) {
			log.warning("Can not delete temporary directory: "+serializeException(e));
		}
	}


}
