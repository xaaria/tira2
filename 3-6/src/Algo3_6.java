import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Algo3_6 {

  public static void main(String[] args) {

    //Test.test();

    if(args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if( args.length == 2 && args[1].equals("0") ) {
      filename = "src/" + filename;
    }

    ClosestPair cp = new ClosestPair();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        //System.out.print(line + "\n");
        String[] line_ = line.split(" "); // [x, y]

        cp.addPoint( new Point( Integer.parseInt(line_[0]), Integer.parseInt(line_[1]) ) );

      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    // --- Run the algorithm ---
    cp.closestPair();

  }


  public static class ClosestPair {
    ArrayList<Point> points = new ArrayList<>(); // unordered
    Point closest_1, closest_2 = null; // TODO

    public double closestPair() {

      // Part 1 of the algorithm - sort points ASC by X and Y
      final ArrayList<Point> pointsByX = ((ArrayList<Point>) this.points.clone()).stream().sorted(Comparator.comparingInt(p -> p.x)).collect(Collectors.toCollection(ArrayList::new));
      final ArrayList<Point> pointsByY = ((ArrayList<Point>) this.points.clone()).stream().sorted(Comparator.comparingInt(p -> p.y)).collect(Collectors.toCollection(ArrayList::new));

      // give rank for each in x
      int rank = 1;
      for(Point p : pointsByX) {
        p.rank = rank++;
      }

      return closestPairRecursive(pointsByX, pointsByY, 1, pointsByX.size()); // end is exclusive, so no -1 there
    }

    // start end end NOT zero based
    private double closestPairRecursive(final ArrayList<Point> pointsX, final ArrayList<Point> pointsY, final int start, final int end) {

      // ---
      final ArrayList<Point> pointsX_clone = new ArrayList<>(pointsX.subList(start-1, end));

      // Print out details of current run
      print(String.format("Current subset %d...%d has %d points:", start, end, pointsX_clone.size()), false );
      Point.printPoints(pointsX_clone);
      // ---


      double d = Double.POSITIVE_INFINITY;

      if(start < end) {

        // First, cut X arr from X[start..end] = [start-1, end[

        //print(String.format("\t[%d..%d[ size: %d", start, end, pointsX.size()), true);
        /*
        final ArrayList<Point> pointsX_clone = new ArrayList<>(pointsX.subList(start-1, end));   // replace content instead of new?

        // Print out details of current run
        print(String.format("Current subset %d...%d has %d points:", start, end, pointsX_clone.size()), false );
        Point.printPoints(pointsX_clone);

         */


        final int mid = (start+end) / 2;
        double d1 = closestPairRecursive(pointsX_clone, pointsY, start, mid);
        double d2 = closestPairRecursive(pointsX_clone, pointsY, mid+1, end);

        d = Math.min(d1, d2);
        //final double d_ = d; // final for the lambda...

        // Calculate the center line L
        int l = (pointsX_clone.get(mid-1).x + pointsX_clone.get(mid).x) / 2;

        // "take a point in y_arr into 2d-strip only if it is inside start...end in x_arr"
        /*
        final ArrayList<Point> strip = pointsY.stream().filter( (p) -> p.rank >= start && p.rank <= end && ( Math.abs(p.x - l) <= d_ ) ).collect(Collectors.toCollection(ArrayList::new ));
        strip.sort( (p1, p2) -> (p1.y != p2.y) ? p1.y - p2.y : p1.x - p2.x ); //  primarily by y- and secondarily by x-coordinates. (!!!)

        // Print strip details
        print( String.format("Current subset %d...%d has 2d-strip with %d points:", start, end, strip.size() ), false );
        Point.printPoints(strip);


        for(Point p : strip) {
          // Compute distance from strip[i] to (at most) 7 following points.
          // pi is the index on p. Calculate to [pi+1 ... size/pi+7]
          for(int pi = strip.indexOf(p)+1; pi < Math.min(strip.size(), pi+8); pi++) {
            //print( "check @ " + pi, true );
            // If new closer pair found (smaller than current dist.)
            if( p.getDistance(strip.get(pi)) < d ) {
              closest_1 = p; closest_2 = strip.get(pi);
              d = p.getDistance(strip.get(pi));
            }
          }
        }
        */
      }

      final String dist = (d == Double.POSITIVE_INFINITY) ? "infinity" : String.format("%.3f", d); // 'infinity' if no change
      print(String.format("Current subset %d...%d returns distance: <WIP> %s", start, end, dist), true);
      return d;

    }


    public void print(String s, boolean newLine) {
      System.out.print(s);
      if(newLine) { System.out.print("\n"); }
    }



    /**
     * Adds point if not already
     */
    public void addPoint(Point p) {
      if(! this.points.contains(p)) {
        this.points.add(p);
      }
    }

  }



  public static class Point implements Comparable<Point> {
    public int x, y, rank;
    public Point(int x, int y) { this.x = x; this.y = y; }

    public double getDistance(Point p) {
      return Math.sqrt(Math.pow(this.x-p.x, 2) + Math.pow(this.y-p.y, 2) );
    }

    public static void printPoints(ArrayList<Point> points) {
      for(Point p : points) {
        System.out.print(" " + p.toString());
      }
      System.out.print("\n");
    }

    @Override
    public boolean equals(Object o) {
      if(o instanceof Point) {
        return this.x == ((Point)o).x && this.y == ((Point) o).y;
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", this.x, this.y);
    }

    @Override
    public int compareTo(Point p) {
      return (this.x != p.x) ? this.x - p.x : this.y - p.y; // first by x (smaller first) then by y (smaller first)
    }
  }


  public static class Test {

    public static void test() {

      Point p0 = new Point(0, 0);
      Point p1 = new Point(3, 4);
      Point p2 = new Point(1234, 600);

      System.out.println( p0.getDistance(p0) ); // 0.00
      System.out.println( p0.getDistance(p1) ); // 5.00
      System.out.println( p1.getDistance(p0) ); // 5.00
      System.out.println( p0.getDistance(p2) ); //

      System.out.println( p0.equals(p0) ); // true
      System.out.println( p2.equals(p1) ); // false

      // Sorting

      ArrayList<Point> ps = new ArrayList<>();
      ps.add( new Point(1233, 10) );
      ps.add(p0);
      ps.add(p2);
      ps.add(p1);
      ps.add( new Point(-2, 30000) );
      List<Point> pointsByX = ((ArrayList<Point>) ps.clone()).stream().sorted( (p1_, p2_) -> p1_.x - p2_.x ).collect(Collectors.toList());
      System.out.println( pointsByX ); // p0 1 2

      List<Point> pointsByY = ((ArrayList<Point>) ps.clone()).stream().sorted( (p1_, p2_) -> p1_.y - p2_.y ).collect(Collectors.toList());
      System.out.println( pointsByY ); // p0 1 2
      System.out.println( pointsByX ); // p0 1 2

    }

  }

}
