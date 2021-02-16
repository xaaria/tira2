import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Algo4_6 {

  public static void main(String args[]) {

    if(args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if( args.length == 2 && args[1].equals("0") ) {
      filename = "src/" + filename;
    }

    Mismatch mis = new Mismatch();


    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      // Read each line
      int lineNum = 1;
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if(lineNum == 1) {
          mis.seqOne = line;
        }
        if(lineNum == 2) {
          mis.seqTwo = line;
        }
        if(lineNum == 3) {
          // Read the gap penalty
          mis.setGapPenalty( Integer.parseInt(line) );
        }

        String[] parts = line.split(" ");
        //System.out.println( Arrays.toString(parts) );
        // get index for protein. For example "A" => 0
        int y = Mismatch.PROTEINS.get(parts[1]);
        int x = Mismatch.PROTEINS.get(parts[0]);
        int penalty = Integer.parseInt(parts[2]);
        mis.modifyPenalties(y, x, penalty); // update 2d matrix [y,x] and [x,y] => penalty

        lineNum++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


    mis.printPenaltyMatrix();



  }


  public static class Mismatch {

    public static final HashMap<String, Integer> PROTEINS = new HashMap<String, Integer>() {{
      put("A", 0);
      put("C", 1);
      put("G", 2);
      put("T", 3);
    }};

    String seqOne, seqTwo;
    int gapPenalty = 0;
    int[][] penalties = new int[4][4];

    public Mismatch() {
    }

    public static class Pair {
      int y, x;
      public Pair(int y, int x) {
        this.y = y;
        this.x = x;
      }

      @Override
      public void equals(Pair p) {

      }
    }

    // The main algorithm
    public void getAllMismatches() {

      final int m = this.seqOne.length();
      final int n = this.seqTwo.length();

      final int[][] M = getSequenceAlignment(this.seqOne, this.seqTwo);

      LinkedList<Pair> S = new LinkedList<>();

      while(! S.isEmpty()) {
        if( S.peek().y != 0 && S.peek().x != 0 ) {
          Pair w = S.peek(); // take the M[][] as w

          // DFS-search. Check "neighbour nodes"
          // Connection exists if criteria is fullfilled

          if( M[w.y][w.x] == M[w.y-1][w.x] + this.gapPenalty ) {
            S.push( new Pair(w.y-1, w.x) );
          }

          if( M[w.y][w.x] == M[w.y-1][w.x-1] + getPenalty( String.valueOf(seqOne.charAt(w.y)),  String.valueOf(seqOne.charAt(w.x)) ) ) {
            S.push( new Pair(w.y-1, w.x-1) );
          }

          if( M[w.y][w.x] == M[w.y-1][w.x] + this.gapPenalty ) {
            S.push( new Pair(w.y, w.x-1) );
          }
        } else {
          S.pop();
        }
      }


    }


    public int[][] getSequenceAlignment(final String seqOne, final String seqTwo) {

      final int m = this.seqOne.length();
      final int n = this.seqTwo.length();
      int[][] M = new int[m][n];

      for(int i=0; i<m; i++) { M[i][0] = i * this.gapPenalty; }
      for(int j=0; j<n; j++) { M[0][j] = j * this.gapPenalty; }

      for(int i = 1; i<m; i++) {
        for(int j = 1; j<n; j++) {
          int case_a = getPenalty( String.valueOf(seqOne.charAt(i)), String.valueOf(seqTwo.charAt(j)) ) + M[i-1][j-1];
          int case_b = this.gapPenalty + M[i-1][j];
          int case_c = this.gapPenalty + M[i][j-1];
          M[i][j] = Math.min( case_a, Math.min(case_b, case_c) );
        }
      }
      return M;
    }


    public void setGapPenalty(int penalty) {
      this.gapPenalty = penalty;
    }

    public void modifyPenalties(int y, int x, int newVal) {
      this.penalties[y][x] = newVal;
      this.penalties[x][y] = newVal;
    }

    public int getPenalty(String protein1, String protein2) {
      if(protein1.equals(protein2)) { return 0; }
      return this.penalties[ PROTEINS.get(protein1) ][ PROTEINS.get(protein2) ];
    }

    public void printPenaltyMatrix() {
      for(int y=0; y<penalties.length; y++) {
        for(int x=0; x<penalties.length; x++) {
          System.out.printf("[%d]", this.penalties[y][x]);
        }
        System.out.println();
      }
    }

  }


}
