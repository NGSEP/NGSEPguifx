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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import ngsep.main.Command;
import ngsep.main.CommandOption;
import ngsep.main.CommandsDescriptor;
import ngsepfx.concurrent.NGSEPTask;
import ngsepfx.controller.validator.ValidationReport;
import ngsepfx.event.NGSEPAnalyzeFileEvent;
import ngsepfx.event.NGSEPEvent;
import ngsepfx.event.NGSEPExecuteTaskEvent;
import ngsepfx.view.component.ValidatedTextField;

/**
 * Abstract class used to implement all NGSEP analysis. Gives the root node
 * to be loaded into the {@link Scene} and handles {@link NGSEPEvent}. The root 
 * is loaded from an .fxml.
 * @author Fernando Reyes
 */
public abstract class AnalysisAreaController {
	
	// Attributes.
	
	@FXML
	private Parent root;
	
	// Methods.
	
	/**
	 * Initialize the {@link AnalysisAreaController} loading the .fxml file and setting the root.
	 */
	public void initializeController() {
		try {
			URL fxmlLocation = getClass().getResource(getFXMLResourcePath());
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
			fxmlLoader.setController(this);
			this.root = fxmlLoader.load();
			String cssForm = getClass().getResource(getCSSResourcePath()).toExternalForm();
			if (cssForm != null) {
				this.root.getStylesheets().add(cssForm);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("ValidationError loading .fxml", e);
		}
	}
	
	/**
	 * Get the root node to display in the center area of the application.
	 * @return
	 */
	public Parent getRootNode(){
		return root;
	}
	/**
	 * 
	 * @return String path to the CSS resource associated with the visualization
	 */
	public String getCSSResourcePath() {
		return "/ngsepfx/view/default.css";
	}
	
	/**
	 * @return String path to the FXML resource with the visualization
	 */
	public abstract String getFXMLResourcePath();
	
	/**
	 * Returns the text field components controlled by this class
	 * @return Map<String, ValidatedTextField> Map with attributes as keys
	 * and ValidatedTextField objects as values
	 */
	protected Map<String, ValidatedTextField> getValidatedTextFieldComponents() {
		return new HashMap<String, ValidatedTextField>();
	}
	
	/**
	 * Returns the checkbox components controlled by this class
	 * @return Map<String, CheckBox> Map with attributes as keys
	 * and CheckBox objects as values
	 */
	protected Map<String, CheckBox> getCheckBoxComponents() {
		return new HashMap<String, CheckBox>();
	}
	
	/**
	 * Handle the {@link NGSEPEvent} received to activate the analysis area.
	 * This is usually an {@link NGSEPAnalyzeFileEvent} fired from the context menu
	 * but it is left with its parent {@link EventType} as parameter for future development.
	 * @param event The {@link NGSEPEvent} to be processed.
	 */
	public abstract void handleActivationEvent(NGSEPEvent event);
	
	protected void suggestOutputFile (File inputFile, ValidatedTextField outputFileTextField, String suffix ) {
		String dir = inputFile.getParent();
		String name = inputFile.getName();
		String extensionlessName = removeExtension(name);
		String outputFileAbsolutePath = dir + File.separator + extensionlessName + suffix;
		outputFileTextField.setText(outputFileAbsolutePath);
	}
	
	protected String getExtension(String name) {
		int index = calculateExtensionIndex(name);
		if(index>0 && index < name.length()-1) return name.substring(index+1);
		return name;
	}
	private String removeExtension(String name) {
		int index = calculateExtensionIndex(name);
		if(index>0) return name.substring(0,index);
		return name;
	}
	private int calculateExtensionIndex(String name) {
		int index = name.lastIndexOf(".");
		if(index<=0 || index == name.length()-1) return index;
		String suffix = name.substring(index+1);
		if(suffix.toLowerCase().equals("gz")) {
			String prefix = name.substring(0,index);
			int index2 = prefix.lastIndexOf(".");
			if(index2>0) index = index2;
		}
		return index;
	}
	
	@FXML
	protected void changeInputFile(ActionEvent event) {
		Node node = (Node) event.getSource() ;
		ValidatedTextField textField = (ValidatedTextField) node.getUserData();
		File currentDir = selectCurrentDirectory(textField);
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(currentDir);
		chooser.setTitle("Select input file");
		File selectedFile = chooser.showOpenDialog(null);
		if (selectedFile != null) {
			textField.setText(selectedFile.getAbsolutePath());
		}
	}
	
	/**
	 * Creates a dialog to change the directory selected in the text field associated with the event
	 * @param parent window of the dialog
	 * @param textField To change
	 */
	@FXML
	public void changeInputDirectory(ActionEvent event) {
		Node node = (Node) event.getSource() ;
		ValidatedTextField textField = (ValidatedTextField) node.getUserData();
		File currentDir = selectCurrentDirectory(textField);
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(currentDir);
		chooser.setTitle("Select input directory");
		File selectedDir = chooser.showDialog(null);
		if (selectedDir != null) {
			textField.setText(selectedDir.getAbsolutePath());
		}
	}
	
	@FXML
	protected void changeOutputFile(ActionEvent event) {
		Node node = (Node) event.getSource();
		ValidatedTextField textField = (ValidatedTextField)node.getUserData();
		File currentDir = selectCurrentDirectory(textField);
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(currentDir);
		chooser.setTitle("Select output file");
		File selectedFile = chooser.showSaveDialog(null);
		if (selectedFile != null) {
			textField.setText(selectedFile.getAbsolutePath());
		}
	}
	
	private File selectCurrentDirectory(ValidatedTextField textField) {
		String currentValue = textField.getText().trim();
		File currentDir = null;
		if(!currentValue.isEmpty()) {
			File currentFile = new File(currentValue);
			currentDir = currentFile.getParentFile();
		}
		if(currentDir == null) {
			currentDir = new File(System.getProperty("user.home"));
		}
		return currentDir;
	}
	
	@FXML
	private void submitTask(ActionEvent actionEvent) {
		if (!validateFields()) return;
		NGSEPTask<Void> task = getTask(); 
		if (task!=null) getRootNode().fireEvent(new NGSEPExecuteTaskEvent(task));
	}
	
	/**
	 * 
	 * @return NGSEPTask associated with this controller
	 */
	protected abstract NGSEPTask<Void> getTask();
	
	protected FileHandler createLogHandler (String outputFile, String suffix) throws IOException {
		String logFilename = outputFile;
		if (logFilename.contains(".")) logFilename = logFilename.substring(0,logFilename.lastIndexOf("."));
		logFilename = logFilename+"_"+suffix+".log";
		return new FileHandler(logFilename);
	}
	
	/**
	 * Set the default values in the ValidatedTextField objects controlled by this class
	 * @param commandClassName Name of the core class to find default values
	 */
	protected void setDefaultValues (String commandClassName) {
		CommandsDescriptor commands = CommandsDescriptor.getInstance();
		Command command = commands.getCommandByClass(commandClassName);
		Map<String,CommandOption> options = command.getOptions();
		Map<String,String> defaultValuesByParameter = new HashMap<String, String>();
		for(CommandOption option: options.values()) {
			if(option.getAttribute()!=null && option.getDefaultValue()!=null) {
				defaultValuesByParameter.put(option.getAttribute(), option.getDefaultValue());
			}
		}
		Map<String, ValidatedTextField> textFieldsMap = getValidatedTextFieldComponents();
		System.out.println("Command class name: "+commandClassName+" Command: "+command.getId()+" fields: "+textFieldsMap.size());
		for(String parameter: textFieldsMap.keySet()) {
			String defaultValue = defaultValuesByParameter.get(parameter);
			System.out.println("Parameter: "+parameter+" default value: "+defaultValue);
			if(defaultValue==null) {
				continue;
			}
			ValidatedTextField textField = textFieldsMap.get(parameter);
			textField.setText(defaultValue);
		}
	}
	/**
	 * Validates the ValidatedTextField objects controlled by this class
	 * @return boolean true if the validation was successful. False otherwise
	 */
	protected boolean validateFields() {
		ArrayList<ValidationReport> errorsArray = new ArrayList<>();
		Map<String, ValidatedTextField> textFieldsMap = getValidatedTextFieldComponents();
		for(ValidatedTextField textField: textFieldsMap.values()) {
			validateTextField(textField, errorsArray);
		}
		
		if(!errorsArray.isEmpty()) {
			showValidationErrorDialog(errorsArray);
			return false;
		}		
		return true;
	}
	
	protected void validateTextField(ValidatedTextField textField, ArrayList<ValidationReport> errorsArray) {
		ValidationReport error = textField.validate();
		if (error.hasErrors()) {
			textField.getStyleClass().add("error");
			errorsArray.add(error);
		} else {
			textField.getStyleClass().remove("error");
		}
	}
	
	/**
	 * Shows an error dialog with the messages included in the given String
	 * @param errorsMessage Message
	 */
	protected void showValidationErrorDialog(ArrayList<ValidationReport> reports) {
		String errorsMessage = collectValidationErrors(reports);
		Alert alert = new Alert(AlertType.ERROR);
		TextArea textArea = new TextArea(errorsMessage);
		textArea.setEditable(false);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 0);

		alert.setTitle("Validation Error");
		alert.setHeaderText("Errors in one or more fields");
		alert.setContentText("Errors in one or more fields");
		alert.initModality(Modality.NONE);
		alert.getDialogPane().setExpandableContent(expContent);		
		alert.getDialogPane().expandedProperty().set(true);

		alert.show();	
	}
	private String collectValidationErrors(ArrayList<ValidationReport> reports) {
		StringBuilder answer = new StringBuilder();
		for(ValidationReport report:reports) {
			answer.append(report.toHierarchichalString());
		}
		return answer.toString();
	}
	
	protected void fillAttributes(Object programInstance) {
		System.out.println("Setting parameters for: "+programInstance.getClass().getName());
		CommandsDescriptor commands = CommandsDescriptor.getInstance();
		
		Command command = commands.getCommandByClass(programInstance.getClass().getName());
		System.out.println("Command: "+command.getId());
		Map<String, ValidatedTextField> textFieldsMap = getValidatedTextFieldComponents();
		System.out.println("Text fields: "+textFieldsMap.size());
		Map<String,CommandOption> options = command.getOptions();
		System.out.println("Command options: "+options.size());
		Map<String,CommandOption> optionsByAttribute = new HashMap<String, CommandOption>();
		for(CommandOption option: options.values()) {
			System.out.println("Option id: "+option.getId()+". Option attribute: "+option.getAttribute());
			if(option.getAttribute()!=null) {
				optionsByAttribute.put(option.getAttribute(), option);
			}
		}
		System.out.println("Options with attribute: "+optionsByAttribute.size()+" text fields map keys: "+textFieldsMap.keySet().size());
		for(String attribute: textFieldsMap.keySet()) {
			ValidatedTextField textField = textFieldsMap.get(attribute);
			String value = textField.getText().trim();
			CommandOption option = optionsByAttribute.get(attribute);
			System.out.println("Attribute: "+attribute+". Option found: "+option+" value: "+value);
			if(option==null) {
				//By now it can happen that not all text fields are tied with command options
				continue;
			}
			Method setter = option.findStringSetMethod(programInstance);
			
			if(value.isEmpty()) continue;
			try {
				setter.invoke(programInstance, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Error setting value \""+value+"\" for parameter \""+option.getId()+"\" of type: "+option.getType()+". "+e.getMessage());
				throw new RuntimeException(e);
			}
		}	
	}
	

	/**
	 * Shows an error dialog with the message included in the given String
	 * @param errorsMessage Message
	 */
	public static void showExecutionErrorDialog(String taskName, Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Excecution Error");
		alert.setHeaderText("Error executing "+taskName);
		alert.setContentText(e.getMessage());
		alert.initModality(Modality.NONE);
		alert.show();
	}


}
