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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ngsep.genome.ReferenceGenome;

/**
 * @author Erick Duarte
 */
public class SelectReadsForAlignmentController {

	@FXML
	private Label directoryLabel;
	@FXML
    private TableView<ReadsAlignerFileData> readFilesDataTableView;
    @FXML
    private TableColumn<ReadsAlignerFileData, String> idColumn;
    @FXML
    private TableColumn<ReadsAlignerFileData, String> fileColumn;
    @FXML
    private TableColumn<ReadsAlignerFileData, String> file2Column;
    @FXML
    private TableColumn<ReadsAlignerFileData, String> inputFormatColumn;
    @FXML
    private TableColumn<ReadsAlignerFileData, String> outBamFileColumn;
    @FXML
    private Button findPairsButton;

    private File directory;
    private ObservableList<ReadsAlignerFileData> readFilesData;
    private boolean pairedEnd = false;
    
	public void setDirectory(File directory) {
		this.directory = directory;
		directoryLabel.setText("Select reads to map from "+directory.getAbsolutePath());
		readFilesDataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().sampleId);
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().fileName);
        file2Column.setCellValueFactory(cellData -> cellData.getValue().file2Name);
        inputFormatColumn.setCellValueFactory(cellData -> cellData.getValue().inputFormat);
        outBamFileColumn.setCellValueFactory(cellData -> cellData.getValue().outBamFile);
		populateTableSingleReads();
		
	}
	public void populateTableSingleReads() {
		File [] files = directory.listFiles();
		readFilesData = FXCollections.observableArrayList();
		for(File file:files) {
			String name = file.getName();
			int k = ReadsAlignerController.getExtensionIndex(name);
			if(k<1) continue;
			String sampleId = name.substring(0,k);
			ReadsAlignerFileData data = new ReadsAlignerFileData(sampleId, file);
			if(name.toLowerCase().substring(k).startsWith(".fastq")) data.setInputFormat(AnalysisAreaController.FORMAT_FASTQ);
			else data.setInputFormat(AnalysisAreaController.FORMAT_FASTA);
			data.setOutBamFile(sampleId+".bam");
			readFilesData.add(data);
		}
		Collections.sort(readFilesData,(f1,f2)->f1.getSampleId().compareTo(f2.getSampleId()));
		
		readFilesDataTableView.setItems(readFilesData);	
	}
	
	@FXML
	public void selectAll(ActionEvent event) {
		readFilesDataTableView.getSelectionModel().selectAll();
	}
	@FXML
	public void findPairs(ActionEvent event) {
		List<ReadsAlignerFileData>  pairsList = new ArrayList<ReadsAlignerFileData>();
		ReadsAlignerFileData nextData = null;
		String lastId = null;
		for(ReadsAlignerFileData currentData:readFilesData) {
			//System.out.println("Next sample id: "+currentData.getSampleId()+" file: "+currentData.getFile());
			int idxSingleDiff = getIndexSingleDifference(currentData.getSampleId(), lastId);
			if (idxSingleDiff<1 || !nextData.getInputFormat().equals(currentData.getInputFormat())) {
				nextData = new ReadsAlignerFileData(currentData.getSampleId(), currentData.getFile());
				nextData.setInputFormat(currentData.getInputFormat());
				nextData.setOutBamFile(currentData.getOutBamFile());
				pairsList.add(nextData);
			} else {
				String sampleId = lastId.substring(0,idxSingleDiff);
				char last = sampleId.charAt(idxSingleDiff-1);
				if(last == '_' || last=='.') sampleId = sampleId.substring(0,idxSingleDiff-1);
				nextData.setSampleId(sampleId);
				nextData.setFile2(currentData.getFile());
				nextData.setOutBamFile(sampleId+".bam");
				nextData = null;
			}
			lastId = currentData.getSampleId();
		}
		readFilesData.clear();
		readFilesData.addAll(pairsList);
		findPairsButton.setDisable(true);
		pairedEnd = true;
	}
	public int getIndexSingleDifference(String sampleId1, String sampleId2) {
		if(sampleId1==null || sampleId2 == null) return -1;
		if (sampleId1.length() != sampleId2.length()) return -1;
		int indexDifference = -1;
		for(int i=0;i<sampleId1.length();i++) {
			char c1 = sampleId1.charAt(i);
			char c2 = sampleId2.charAt(i);
			if(c1!=c2) {
				if(indexDifference!=-1) return -1;
				indexDifference = i;
			}
		}
		return indexDifference;
	}

	@FXML
	public void finish(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
	}
	public List<ReadsAlignerFileData> getSelectedReadFilesData() {
		return readFilesDataTableView.getSelectionModel().getSelectedItems();
	}
	public boolean isPairedEnd() {
		return pairedEnd;
	}
	
	
}
class ReadsAlignerFileData {
	SimpleStringProperty sampleId;
	SimpleStringProperty fileName;
	SimpleStringProperty file2Name;
	SimpleStringProperty inputFormat;
	SimpleStringProperty outBamFile;
	private File file;
	private File file2;
	private ReferenceGenome genome;
	
	public ReadsAlignerFileData(String sampleId, File file) {
		this.sampleId = new SimpleStringProperty(sampleId);
		this.fileName = new SimpleStringProperty(file.getName());
		this.file = file;
		this.file2Name = new SimpleStringProperty();
		this.inputFormat = new SimpleStringProperty();
		this.outBamFile = new SimpleStringProperty();
	}
	public String getSampleId() {
		return sampleId.get();
	}
	public void setSampleId(String sampleId) {
		this.sampleId.set(sampleId);
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
		this.fileName.set(file.getName());
	}
	public File getFile2() {
		return file2;
	}
	public void setFile2(File file2) {
		this.file2 = file2;
		this.file2Name.set(file2.getName());
	}
	
	public ReferenceGenome getGenome() {
		return genome;
	}
	public void setGenome(ReferenceGenome genome) {
		this.genome = genome;
	}
	public String getInputFormat() {
		return inputFormat.get();
	}
	public void setInputFormat(String inputFormat) {
		this.inputFormat.set(inputFormat);
	}
	public String getOutBamFile() {
		return outBamFile.get();
	}
	public void setOutBamFile(String outBamFile) {
		this.outBamFile.set(outBamFile);
	}
}
