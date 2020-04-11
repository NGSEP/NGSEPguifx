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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ngsep.alignments.ReadAlignment;
import ngsep.alignments.io.ReadAlignmentFileReader;
import ngsep.main.io.ConcatGZIPInputStream;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;

/**
 * 
 * @author Jorge Duitama
 *
 */
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
		
		String name = file.getName();
		
		try (PrintStream out = new PrintStream(os)){
			out.println(name);
			out.println();
			if(name.toLowerCase().endsWith(".bam")) {
				
				dumpBAMFile (file, out);
			} else {
				dumpRegularFile(file, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
			showExecutionErrorDialog(Thread.currentThread().getName(), e);
		}
		contentText.setText(os.toString());
	}

	private void dumpBAMFile(File file, PrintStream out) throws IOException {
		try (ReadAlignmentFileReader reader = new ReadAlignmentFileReader(file.getAbsolutePath())) {
			Iterator<ReadAlignment> it = reader.iterator();
			for(int i=0;i<1000 && it.hasNext();i++) {
				ReadAlignment aln = it.next();
				out.print(aln.getReadName()+"\t"+aln.getFlags()+"\t"+aln.getSequenceName()+"\t"+aln.getFirst()+"\t"+aln.getAlignmentQuality());
				out.println("\t"+aln.getCigarString()+"\t"+aln.getMateSequenceName()+"\t"+aln.getMateFirst()+"\t"+aln.getInferredInsertSize());
			}
		}
	}

	private void dumpRegularFile(File file, PrintStream out) throws IOException {
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
		}
	}

	@Override
	protected NGSEPTask<Void> getTask() {
		// TODO Auto-generated method stub
		return null;
	}

}
