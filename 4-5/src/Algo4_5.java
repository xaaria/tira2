public class Algo4_5 {

  public static void main(String[] args) {

  }

  public static void alignment(final String x, int a, int b, final String y, double c, double d) {

    final double mid = (c+d) / 2.0;

    // compute edit distance between X[a+1..b] and Y[c+1..mid]
    // let F be the last column of the distance table for X[a+1..b] and Y[c+1..mid]

    // compute edit distance between reversed strings X[b..a+1] and Y[d..mid+1]
    // let R be the last column of the distance table for X[b..a+1] and Y[d..mid+1]

    // select first row i where F[i] + R[b-a-i] has minimal value
    int row_i = -1;

    if(c + 1 < mid) {  // Recurse to the left/upper side?
      alignment(x, a, row_i, y, c, mid);
    if(mid + 1 < d) {  // Recurse to the right/lower side?
      alignment(x, row_i, b, y, mid, d);
    }
  }

  private static double editDistance(final String a, final String b) {


    }


}
