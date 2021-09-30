/**
 * Authors
 * 
 * Daniel Pizzi
 * Bhavya Phogat
 */

package songlib.app;

public class Song {

	private String title;
	private String artist;
	private String album;
	private int year;
	
	public Song(String title, String artist, String album, int year){
		this.setTitle(title);
		this.setArtist(artist);
		this.setAlbum(album);
		this.setYear(year);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString() {
		return getTitle() + "\n" + getArtist() + "\n" + getAlbum() + "\n" + getYear() + "\n";
	}
}