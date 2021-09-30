/**
 * Authors
 * 
 * Daniel Pizzi
 * Bhavya Phogat
 */

package songlib.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import songlib.view.songLibController;

public class SongLib extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songlib/view/songlib.fxml"));
		
		AnchorPane root = (AnchorPane)loader.load();
		
		songLibController libController = loader.getController();
		libController.start(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				try {
					libController.overwrite();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		primaryStage.show();
		
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
