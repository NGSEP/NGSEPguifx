/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
    Debigulator - A batch compression utility
Copyright (C) 2003-2018 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * Modified for this specific software.
 */

package ngsepfx.controller.fileexplorer;

import java.io.File;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * {@link TreeItem} for the {@link File}.
 * @author Oracle and/or its affiliates and Hugues Johnson.
 * @author Fernando Reyes
 *
 */
public class FileTreeItem extends TreeItem<String> {
	public static Image folderCollapseImage = new Image(
			ClassLoader.getSystemResourceAsStream("ngsepfx/view/images/folder.png"));
	public static Image folderExpandImage = new Image(
			ClassLoader.getSystemResourceAsStream("ngsepfx/view/images/folder-open.png"));
	public static Image fileImage = new Image(
			ClassLoader.getSystemResourceAsStream("ngsepfx/view/images/text-x-generic.png"));
	// https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeItem.html
	// We cache whether the File is a leaf or not. A File is a leaf if
	// it is not a directory and does not have any files contained within
	// it. We cache this as isLeaf() is called often, and doing the
	// actual check on File is expensive.
	private boolean isLeaf;

	// We do the children and leaf testing only once, and then set these
	// booleans to false so that we do not check again during this
	// run. A more complete implementation may need to handle more
	// dynamic file system situations (such as where a folder has files
	// added after the TreeView is shown). Again, this is left as an
	// exercise for the reader.
	private boolean isFirstTimeChildren = true;
	private boolean isFirstTimeLeaf = true;

	private final File file;
	private final String absolutePath;
	private final boolean isDirectory;

	/**
	 * Build the {@link FileTreeItem} for the {@link File}, add it's
	 * {@link EventHandler}s and set the {@link TreeItem}'s 
	 * {@link TreeItem#valueProperty()}'s value and 
	 * {@link TreeItem#graphicProperty()}'s value.
	 * @param file {@link File} for the {@link TreeItem}.
	 */
	public FileTreeItem(File file) {
		super(file.getAbsolutePath());
		this.file = file;
		this.absolutePath = file.getAbsolutePath();
		this.isDirectory = file.isDirectory();
		if (this.isDirectory) {
			this.setGraphic(new ImageView(folderCollapseImage));
			// add event handlers
			this.addEventHandler(TreeItem.branchCollapsedEvent(), 
					new EventHandler<TreeItem.TreeModificationEvent<String>>() {
						@Override
						public void 
						handle(TreeItem.TreeModificationEvent<String> e) {
							FileTreeItem source = (FileTreeItem) e.getSource();
							if (!source.isExpanded()) {
								ImageView iv = (ImageView) source.getGraphic();
								iv.setImage(folderCollapseImage);
								System.out.println(source.getValue());
							}
						}
					});
			this.addEventHandler(TreeItem.branchExpandedEvent(), 
					new EventHandler<TreeItem.TreeModificationEvent<String>>() {
				@Override
				public void 
				handle(TreeItem.TreeModificationEvent<String> e) {
					FileTreeItem source = (FileTreeItem) e.getSource();
					if (source.isExpanded()) {
						ImageView iv = (ImageView) source.getGraphic();
						iv.setImage(folderExpandImage);
					}
				}
			});
		} else {
			this.setGraphic(new ImageView(fileImage));
		}
		// set the value (which is what is displayed in the tree)
		String fullPath = file.getAbsolutePath();
		if (!fullPath.endsWith(File.separator)) {
			String value = file.toString();
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > 0) {
				this.setValue(value.substring(indexOf + 1));
			} else {
				this.setValue(value);
			}
		}
	}

	public File getFile() {
		return (this.file);
	}

	public String getAbsolutePath() {
		return (this.absolutePath);
	}

	public boolean isDirectory() {
		return (this.isDirectory);
	}

	@Override
	public ObservableList<TreeItem<String>> getChildren() {
		if (isFirstTimeChildren) {
			isFirstTimeChildren = false;

			// First getChildren() call, so we actually go off and
			// determine the children of the File contained in this TreeItem.
			super.getChildren().setAll(buildChildren(this));
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		if (isFirstTimeLeaf) {
			isFirstTimeLeaf = false;
			isLeaf = this.file.isFile();
		}

		return isLeaf;
	}

	/**
	 * Build the {@link FileTreeItem}'s children.
	 * @param treeItem {@link FileTreeItem} whose children are to be build.
	 * @return An {@link ObservableList} with the children.
	 */
	private ObservableList<FileTreeItem> buildChildren(FileTreeItem treeItem) {
		File f = treeItem.getFile();
		if ((f != null) && (f.isDirectory())) {
			File[] files = f.listFiles();
			if (files != null) {
				ObservableList<FileTreeItem> children = FXCollections.observableArrayList();
				for (File childFile : files) {
					children.add(new FileTreeItem(childFile));
				}
				children.sort(new Comparator<FileTreeItem>() {

					@Override
					public int compare(FileTreeItem o1, FileTreeItem o2) {
						// TODO Auto-generated method stub
						FileTreeItem ft1 = o1;
						FileTreeItem ft2 = o2;
						return ft1.getValue().compareToIgnoreCase(ft2.getValue());
					}
					
				});
				return children;
			}
		}
		return FXCollections.emptyObservableList();
	}
	
	public void refresh() {
		ObservableList<FileTreeItem> children = buildChildren(this);
		if (children.size() != getChildren().size()) {
			super.getChildren().setAll(buildChildren(this));
		}
		for (TreeItem<String> treeItem : getChildren()) {
			FileTreeItem child = (FileTreeItem) treeItem;
			if (!child.isFirstTimeChildren) {
				child.refresh();
			}
			
		}
	}
}
