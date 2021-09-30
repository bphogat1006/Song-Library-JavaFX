/**
 * Authors
 * 
 * Daniel Pizzi
 * Bhavya Phogat
 */

package songlib.view;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import songlib.app.Song;

public class songLibController {
	
	@FXML private ListView<String> library;
	@FXML private TextField songTitle;
	@FXML private TextField artistName;
	@FXML private TextField album;
	@FXML private TextField year;
	@FXML private VBox libraryControls;
	@FXML private Button add;
	@FXML private Button edit;
	@FXML private Button delete;
	@FXML private VBox confirmationDialog;
	@FXML private Button confirm;
	@FXML private Button cancel;
	@FXML private Text infoSectionHeader;

	private Stage primaryStage;
	private ObservableList<String> obsList;
    private ArrayList<Song> songs = new ArrayList<Song>();
	private String programState;
	
	public void start(Stage mPrimaryStage) throws Exception {
		
		primaryStage = mPrimaryStage;
		
		// get all songs from userData.txt and populate library
        String file = new String(Files.readAllBytes(Paths.get("userdata.txt")));
        String[] data = file.split("\\r?\\n");
        int numSongs = data.length/4;
        for(int i=0; i < numSongs; i++) {
    		String name = data[i*4];
    		String artist = data[i*4+1];
    		String album = data[i*4+2];
    		int year = Integer.parseInt(data[i*4+3]);
    		Song song = new Song(name, artist, album, year);
    		songs.add(song);
        }
        populateLibrary();
		
		// if there are > 0 songs in the library:
		if(obsList.size() > 0) {
			// select the first song
			library.getSelectionModel().select(0);
			// populate song information section
			showItem();
		}
		
		// set click listeners for each song in library
		library.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
			(obs, oldVal, newVal) -> {
				showItem();
			}
		);
		
		// set program state to ready
		programState = "READY";
	}

	
	public void controls(ActionEvent e) {
			
		Button b = (Button)e.getSource();
		
		if (b == add) {
			programState = "ADD";
			toggleEditableFields();
			confirmationDialog.setVisible(true);
			libraryControls.setDisable(true);
			library.setDisable(true);
			infoSectionHeader.setText("--- Enter Information Below ---");
			songTitle.setText("");
			artistName.setText("");
			album.setText("");
			year.setText("");

		} else if (b == edit) {
			int index = library.getSelectionModel().getSelectedIndex();
			if(index == -1) {
				showErrorAlert("No Song Selected");
				return;
			}
			programState = "EDIT";
			toggleEditableFields();
			confirmationDialog.setVisible(true);
			libraryControls.setDisable(true);
			library.setDisable(true);
			infoSectionHeader.setText("--- Edit Information Below ---");
			
		} else if (b == delete) {
			int index = library.getSelectionModel().getSelectedIndex();
			if(index == -1) {
				showErrorAlert("No Song Selected");
				return;
			}
			programState = "DELETE";
			confirmationDialog.setVisible(true);
			libraryControls.setDisable(true);
			library.setDisable(true);
			
		} else if (b == confirm) {
			if(programState == "ADD") {
				String title = String.valueOf(songTitle.getText()).trim();
				String artist = String.valueOf(artistName.getText()).trim();
				String alb = String.valueOf(album.getText()).trim();
				String yrText = year.getText().trim();
				int yr;
				
				// check inputs' validity
				if(title.isEmpty() || artist.isEmpty()) {
					showErrorAlert("Enter a song name and an artist name.");
					return;
				}
				if (yrText.isEmpty()) {
					yr = 0;
				} else {
					try {
						yr = Integer.parseInt(year.getText());
					} catch (Exception ex) {
						showErrorAlert("Enter a valid year (a positive number)");
						return;
					}
					if(yr <= 0) {
						showErrorAlert("Enter a valid year (a positive number)");
						return;
					}
				}
				if(getSongIndex(title, artist) != -1) {
					showErrorAlert("Duplicate song found!");
					return;
				}
				
				int index = addSong(title, artist, alb, yr);
				populateLibrary();
				library.getSelectionModel().select(index);
				showItem();
				readyProgram();
				
			} else if(programState == "EDIT") {
				int index = library.getSelectionModel().getSelectedIndex();
				String title = String.valueOf(songTitle.getText()).trim();
				String artist = String.valueOf(artistName.getText()).trim();
				String alb = String.valueOf(album.getText()).trim();
				String yrText = year.getText().trim();
				int yr;
				
				// check inputs' validity
				if(title.isEmpty() || artist.isEmpty()) {
					showErrorAlert("Enter a song name and an artist name.");
					return;
				}
				if (yrText.isEmpty()) {
					yr = 0;
				} else {
					try {
						yr = Integer.parseInt(year.getText());
					} catch (Exception ex) {
						showErrorAlert("Enter a valid year (a positive number)");
						return;
					}
					if(yr <= 0) {
						showErrorAlert("Enter a valid year (a positive number)");
						return;
					}
				}
				if(getSongIndex(title, artist) > -1 && getSongIndex(title, artist) != index) {
					showErrorAlert("Cannot overwrite an existing song!");
					return;
				}
				
				songs.remove(index);
				index = addSong(title, artist, alb, yr);
				populateLibrary();
				library.getSelectionModel().select(index);
				showItem();
				readyProgram();
				
			} else if(programState == "DELETE") {
				int index = library.getSelectionModel().getSelectedIndex();
				songs.remove(index);
				populateLibrary();
				if (!songs.isEmpty()) {
					if (index == songs.size()) {
						library.getSelectionModel().select(index-1);
					} else {
						library.getSelectionModel().select(index);
					}
				}
				showItem();
				readyProgram();
			}
			
		} else if (b == cancel) {
			showItem();
			readyProgram();
		}
	}
	
	private void readyProgram() {
		confirmationDialog.setVisible(false);
		libraryControls.setDisable(false);
		library.setDisable(false);
		infoSectionHeader.setText("Song Information");
		programState = "READY";
		toggleEditableFields();
	}
	
	private void populateLibrary() {
		obsList = FXCollections.observableArrayList();
        for(Song song : songs) {
    		obsList.add(song.getTitle()+" - "+song.getArtist());
        }
		library.setItems(obsList);
	}
	
	// method to select any song to show it's title, artist, album and year
	private void showItem() {
		int index = library.getSelectionModel().getSelectedIndex();
		if(index == -1) {
			songTitle.setText("");
			artistName.setText("");
			album.setText("");
			year.setText("");
			return;
		}
		songTitle.setText(songs.get(index).getTitle());
		artistName.setText(songs.get(index).getArtist());
		if (songs.get(index).getAlbum() != null) {
			album.setText(songs.get(index).getAlbum());
		} else {
			album.setText("");
		}
		if (songs.get(index).getYear() != 0) {
			year.setText(String.format("%d",songs.get(index).getYear()));
		} else {
			year.setText("");
		}
	}
	
	private int addSong(String title, String artist, String album, int year) {
		Song song = new Song(title, artist, album, year);
		songs.add(song);
		sort(songs);
		return getSongIndex(title, artist);
	}
	
	public void sort(ArrayList<Song> songs) {
		
		for (int i = 0; i < songs.size(); i++) {
			
			for (int j = 0; j < songs.size(); j++) {
				
				if ((songs.get(j).getTitle()).toLowerCase().compareTo((songs.get(i).getTitle()).toLowerCase()) > 0) {
						
					Collections.swap(songs, i, j);
						
				} else if ((songs.get(j).getTitle()).toLowerCase().compareTo((songs.get(i).getTitle()).toLowerCase()) == 0){
					
					if ((songs.get(j).getArtist()).toLowerCase().compareTo((songs.get(i).getArtist()).toLowerCase()) > 0) {
						
						Collections.swap(songs, i, j);
						
					}
				}
			}
		}
	}
	
	private int getSongIndex(String title, String artist) {
		for(int i=0; i < songs.size(); i++) {
			Song song = songs.get(i);
			if(song.getTitle().toLowerCase().equals(title.toLowerCase()) && song.getArtist().toLowerCase().equals(artist.toLowerCase())) {
				return i;
			}
		}
		return -1;
	}
	
	private void toggleEditableFields() {
		if (programState == "READY") {
			songTitle.setEditable(false);
			artistName.setEditable(false);
			album.setEditable(false);
			year.setEditable(false);
			
		} else if (programState == "ADD" || programState == "EDIT") {
			songTitle.setEditable(true);
			artistName.setEditable(true);
			album.setEditable(true);
			year.setEditable(true);
			
		}
	}
	
	private void showErrorAlert(String text) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setTitle("Input Error");
		alert.setHeaderText(text);
		alert.setContentText("Check your inputs and try again.");
		alert.showAndWait();
	}

	// method to overwrite the userdata.txt file
	public void overwrite() {
		try {
			FileWriter fw;
			fw = new FileWriter("userdata.txt", false);
			for (int i = 0; i < songs.size(); i++) {
				fw.write(""+songs.get(i));
			}
			fw.close();
		} catch (IOException e) {
			return;
		}
	}
}


