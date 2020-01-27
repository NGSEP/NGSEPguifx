/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ngsepfx.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;

/**
 * Test controller demonstrating the use on {@link AnalysisAreaController}.
 * @author Fernando Reyes
 */
public class CountFileLinesController extends AnalysisAreaController {
	
	// Attributes.
	
	@FXML
	private Text fileNameText;
	
	@FXML
	private Text numberLinesText;

	@Override
	public String getFXMLResourcePath() {
		return "/ngsepfx/view/CountFileLinesAnalysis.fxml";
	}

	/* (non-Javadoc)
	 * @see ngsepfx.controller.AnalysisAreaController#handleNGSEPEvent(ngsepfx.event.NGSEPEvent)
	 */
	@Override
	public void handleActivationEvent(NGSEPEvent event) {
		NGSEPAnalyzeFileEvent analyzeEvent = (NGSEPAnalyzeFileEvent) event;
		File file = analyzeEvent.file;
		fileNameText.setText(file.getName());
		try (FileReader fileReader = new FileReader(file);
			 BufferedReader bufferedReader = new BufferedReader(fileReader);){
			// https://stackoverflow.com/questions/1277880/how-can-i-get-the-count-of-line-in-a-file-in-an-efficient-way
			int lines = 0;
			while (bufferedReader.readLine() != null) lines++;
			numberLinesText.setText(lines + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		return null;
	}
}
