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
package ngsepfx.controller.fileexplorer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

/**
 * Controller for the File Explorer area of the application.
 * Handles the {@link TreeView}.
 * @author Fernando Reyes
 *
 */
public class FileExplorerController {
	
	//FXML parameters.

	@FXML
	private TreeView<String> treeviewFileBrowse;

	@FXML
	void initialize() {
		// populate file browser
		this.treeviewFileBrowse.setCellFactory((TreeView<String> p) -> new FileExplorerTreeCell());
		userHomeRoot();
		
	}

	// Methods.
	
	/**
	 * Load user's home folder as the root of the file explorer.
	 */
	private void userHomeRoot() {
		if (System.getProperty("user.home") != null) {
			FileTreeItem rootNode = new FileTreeItem(new File(System.getProperty("user.home")));
			rootNode.setExpanded(true);
			this.treeviewFileBrowse.setRoot(rootNode);
		} else {
			rootDirectoriesRoot();
		}
	}
	
	/**
	 * Set root directories as children of root node.
	 */
	private void rootDirectoriesRoot() {
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException x) {
			System.out.println(x);
		} // TODO error dialog
		assert hostName != null : "Unable to get local host name.";
		TreeItem<String> rootNode = new TreeItem<String>(hostName, new ImageView(
				new Image(ClassLoader.getSystemResourceAsStream("ngsepfx/view/images/computer.png"))));
		Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
		for (Path name : rootDirectories) {
			FileTreeItem treeNode = new FileTreeItem(new File(name.toString()));
			rootNode.getChildren().add(treeNode);
		}
		rootNode.setExpanded(true);
		this.treeviewFileBrowse.setRoot(rootNode);
	}
	
	//FXML methods.
	
	/**
	 * Change de root node to the desired directory.
	 * @param ae {@link ActionEvent} not in use.
	 */
	@FXML
	private void changeDir(ActionEvent ae) {		
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		chooser.setTitle("Select folder");
		File selectedDirectory = chooser.showDialog(null);
		if (selectedDirectory != null) {
			FileTreeItem rootNode = new FileTreeItem(selectedDirectory);
			rootNode.setExpanded(true);
			this.treeviewFileBrowse.setRoot(rootNode);
		}
	}
	
	@FXML
	public void refresh(ActionEvent ae) {
		((FileTreeItem) treeviewFileBrowse.getRoot()).refresh();
	}

}
