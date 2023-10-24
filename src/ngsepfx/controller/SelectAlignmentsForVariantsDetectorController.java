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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ngsep.alignments.io.ReadAlignmentFileReader;

/**
 * @author Erick Duarte
 */
public class SelectAlignmentsForVariantsDetectorController {

	@FXML
	private Label directoryLabel;
	@FXML
    private TableView<VariantsDetectorFileData> alnFilesDataTableView;
    @FXML
    private TableColumn<VariantsDetectorFileData, String> idColumn;
    @FXML
    private TableColumn<VariantsDetectorFileData, String> fileColumn;

    private File directory;
    private ObservableList<VariantsDetectorFileData> alnFilesData;
    private boolean pairedEnd = false;
    
	public void setDirectory(File directory) {
		this.directory = directory;
		directoryLabel.setText("Select alignment files from "+directory.getAbsolutePath());
		alnFilesDataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().sampleId);
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().fileName);
		populateTable();
		
	}
	public void populateTable() {
		File [] files = directory.listFiles();
		alnFilesData = FXCollections.observableArrayList();
		for(File file:files) {
			Set<String> sampleIds = findSampleIds(file);
			if(sampleIds!=null) {
				VariantsDetectorFileData data = new VariantsDetectorFileData(sampleIds,file);
				alnFilesData.add(data);
			}
		}
		Collections.sort(alnFilesData,(f1,f2)->f1.getSampleId().compareTo(f2.getSampleId()));
		alnFilesDataTableView.setItems(alnFilesData);
	}
	
	public static Set<String> findSampleIds(File file) {
		String name = file.getName();
		if(name.length()<4 || !(name.toLowerCase().endsWith(".bam")))return null;
		Set<String> sampleIds = new TreeSet<String>();
		try (ReadAlignmentFileReader reader = new ReadAlignmentFileReader(file.getAbsolutePath())) {
			Map<String,String> readGroups = reader.getSampleIdsByReadGroup();
			sampleIds.addAll(readGroups.values());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//if (sampleIds.size()==0) sampleIds.add(name.substring(0,name.length()-4));
		if (sampleIds.size()==0) return null;
		return sampleIds;
	}
	@FXML
	public void selectAll(ActionEvent event) {
		alnFilesDataTableView.getSelectionModel().selectAll();
	}

	@FXML
	public void finish(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
	}
	public List<VariantsDetectorFileData> getSelectedAlignmentFilesData() {
		return alnFilesDataTableView.getSelectionModel().getSelectedItems();
	}
	public boolean isPairedEnd() {
		return pairedEnd;
	}
	
	
}
class VariantsDetectorFileData {
	SimpleStringProperty sampleId;
	SimpleStringProperty fileName;
	private File file;
	private Set<String> sampleIds;
	
	public VariantsDetectorFileData(Set<String> sampleIds, File file) {
		if(sampleIds.size()==1) this.sampleId = new SimpleStringProperty(sampleIds.iterator().next());
		else this.sampleId = new SimpleStringProperty(sampleIds.toString());
		this.sampleIds = sampleIds;
		this.fileName = new SimpleStringProperty(file.getName());
		this.file = file;
	}
	public String getSampleId() {
		return sampleId.get();
	}
	public void setSampleId(String sampleId) {
		this.sampleId.set(sampleId);
		this.sampleIds.clear();
		this.sampleIds.add(sampleId);
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
		this.fileName.set(file.getName());
	}
	public Set<String> getSampleIds() {
		return sampleIds;
	}
}