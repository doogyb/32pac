package AI;

/**
 * Created by doogy on 09/03/15.
 */
public class RhymeLine {
    String line1, line2, artist, album, songName;
    public RhymeLine(String l1, String l2, String ar, String al, String song) {
        line1 = l1;
        line2 = l2;
        artist = ar;
        album = al;
        songName = song;
    }
    public String toString() {
        return "Artist: " + artist +"\nAlbum" + album + "Song: " + songName +"\n\n Lines: \n" + line1 + "\n" + line2;
    }
}
