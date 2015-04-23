package AI;

/**
 * Created by doogy on 09/03/15.
 */
public class RhymeLine {
    protected String line1, line2;
    protected int syllables, score=0;
    public RhymeLine(String l1, String l2, Integer syl) {
        line1 = l1;
        line2 = l2;
        syllables=syl;
    }
    public String toString() {
        return line1 + "\n" + line2;
    }
}
