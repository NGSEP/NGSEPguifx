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
package ngsepfx;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ngsepfx.concurrent.ExecutorSingleton;

/**
 * Main class of the application. Loads Main.fxml and application wide 
 * application.css. Sets Scene dimensions.
 * @author Fernando Reyes
 *
 */
public class Main extends Application {
	
	// Main
	
	/**
	 * Launch the {@link Application}.
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	// Application methods.
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = FXMLLoader.load(getClass()
					.getResource("view/Main.fxml"));
			Scene scene = new Scene(root,1200,675);
			primaryStage.setScene(scene);			
			primaryStage.show();
			scene.getStylesheets().add(getClass()
					.getResource("/ngsepfx/view/application.css").toExternalForm());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		ExecutorSingleton.stopExecutor();
	}
	
}
