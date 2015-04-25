package AI;

/**
 * Created by doogy on 09/03/15.
 */
public class RhymeLine {
    protected String line1, line2;
    protected int syllables, score;
    public RhymeLine(String l1, String l2, Integer syl) {
        line1 = l1;
        line2 = l2;
        syllables = syl;
        score = 0;
    }
    
    public String toString() {
        return line1 + "\n" + line2;
    }
    
    public int get_score(){
    	return score;
    }
    
    public void set_score(int input){
    	score = input;
    }
}
