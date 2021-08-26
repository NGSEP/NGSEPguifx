package ngsepfx.controller;

import java.io.File;
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

public class SelectBasePairQualityStatsDialogController {
	@FXML
	private Label directoryLabel;
	@FXML
    private TableView<AlignmentsFileData> readFilesDataTableView;
    @FXML
    private TableColumn<AlignmentsFileData, String> selectColumn;
    @FXML
    private TableColumn<AlignmentsFileData, String> idColumn;
    @FXML
    private TableColumn<AlignmentsFileData, String> fileColumn;
    @FXML
    private TableColumn<AlignmentsFileData, String> inputFormatColumn;
    @FXML
    private TableColumn<AlignmentsFileData, String> outputFileColumn;
    @FXML
    private Button finishButton;
    @FXML
    private Button selectAllButton;
    
    
	private File directory;
    private ObservableList<AlignmentsFileData> readFilesData;


	
	public void populateTableSingleReads() {
		File [] files = directory.listFiles();
		readFilesData = FXCollections.observableArrayList();
		for(File file:files) {
			String name = file.getName();
			int k = BasePairQualityStatisticsController.getExtensionIndex(name);
			if(k<1) continue;
			String sampleId = name.substring(0,k);
			AlignmentsFileData data = new AlignmentsFileData(sampleId, file);
			if(name.toLowerCase().substring(k).startsWith(".bam")) data.setInputFormat(AnalysisAreaController.FORMAT_BAM);
			else if(name.toLowerCase().substring(k).startsWith(".sam")) data.setInputFormat(AnalysisAreaController.FORMAT_SAM);
			else data.setInputFormat(AnalysisAreaController.FORMAT_CRAM);
			data.setOutputFile(sampleId+".txt");
			readFilesData.add(data);
		}
		Collections.sort(readFilesData,(f1,f2)->f1.getSampleId().compareTo(f2.getSampleId()));
		
		readFilesDataTableView.setItems(readFilesData);	
	}
	
	
	public void setDirectory(File directory) {
		this.directory = directory;
		directoryLabel.setText("Select reads to map from "+directory.getAbsolutePath());
		readFilesDataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		idColumn.setCellValueFactory(cellData -> cellData.getValue().sampleId);
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().fileName);
        inputFormatColumn.setCellValueFactory(cellData -> cellData.getValue().inputFormat);
        outputFileColumn.setCellValueFactory(cellData -> cellData.getValue().outputFile);
		populateTableSingleReads();
	}
	public List<AlignmentsFileData> getSelectedReadFilesData() {
		return readFilesDataTableView.getSelectionModel().getSelectedItems();
	}
	@FXML
	public void selectAll(ActionEvent event) {
		readFilesDataTableView.getSelectionModel().selectAll();
	}
	@FXML
	public void finish(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
	}
	
		
	class AlignmentsFileData {
		SimpleStringProperty sampleId;
		SimpleStringProperty fileName;
		SimpleStringProperty inputFormat;
		SimpleStringProperty outputFile;
		private File file;
		private ReferenceGenome genome;
		
		public AlignmentsFileData(String sampleId, File file) {
			this.sampleId = new SimpleStringProperty(sampleId);
			this.fileName = new SimpleStringProperty(file.getName());
			this.file = file;
			this.inputFormat = new SimpleStringProperty();
			this.outputFile = new SimpleStringProperty();
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
			return outputFile.get();
		}
		public void setOutputFile(String outputFiles) {
			this.outputFile.set(outputFiles);
		}
	}

}
