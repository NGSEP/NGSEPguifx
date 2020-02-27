package ngsepfx.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ngsep.main.io.ConcatGZIPInputStream;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;

public class OpenFileController extends AnalysisAreaController {

	@FXML
	private Text contentText;
	
	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/OpenFile.fxml";
	}

	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(os);
		out.println(file.getName());
		out.println();
		InputStream stream=null;
		try (FileInputStream fis = new FileInputStream(file)) {
			if(file.getName().toLowerCase().endsWith(".gz")) {
				stream = new ConcatGZIPInputStream(fis);
			} else {
				stream = fis;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			int lines = 0;
			String line = bufferedReader.readLine(); 
			while (line!=null && lines < 1000) {
				out.println(line);
				lines++;
				line = bufferedReader.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contentText.setText(os.toString());
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		// TODO Auto-generated method stub
		return null;
	}

}
