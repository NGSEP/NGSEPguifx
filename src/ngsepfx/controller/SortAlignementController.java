package ngsepfx.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;
import ngsepfx.view.component.ValidatedTextField;

public class SortAlignementController extends AnalysisAreaController{
	
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
		// TODO Auto-generated method stub
		return null;
	}
	
//	@FXML
//	private void submitTask(ActionEvent actionEvent) {
//		sortAlignments("temp", inputFileTextField.getText(), outputFileTextField.getText());
//	}
	
//	public static void sortAlignments(String tmpDirName, String inFile, String outFile, Logger log) {
//		String [] args = new String [5];
//		File f = new File (outFile);
//		File sortDirectory = new File(f.getParentFile().getAbsolutePath() + File.separator+tmpDirName+"_tmpDir");
//		if (!sortDirectory.exists()) {
//			if (!sortDirectory.mkdirs()) {
//				log.severe("Could not create temporary directory for sorting");
//				return;
//			} else {
//				log.info("Temporary directory created");
//			}
//		}
//		args[0] = "SORT_ORDER=coordinate";
//		args[1] = "TMP_DIR=" + sortDirectory.getAbsolutePath();
//		args[2] = "I=" + inFile;
//		args[3] = "O=" + outFile;
//		args[4] = "CREATE_INDEX=true";
//		log.info("Sorting alignments");
//		new SortSam().instanceMain(args);
//		log.info("Sorted alignments. Deleting temporary directory");
//		try {
//			sortDirectory.delete();
//		} catch (Exception e) {
//			log.warning("Can not delete temporary directory: "+LoggingHelper.serializeException(e));
//		}
//	}


}
