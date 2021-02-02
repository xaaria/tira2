import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
    double dist = cp.closestPair();

    if(args.length == 2 && args[1].equals("0") ){
      cp.print(cp.closest.toString(), true);
    }

    TreeSet<Point> smallest = cp.closest.stream().min( (s1, s2) -> s1.first().x - s2.first().x ).get();
    cp.print(String.format(Locale.US, "Closest points: %s %s with distance %.3f", smallest.first(), smallest.last(), dist), true);

  }


  public static class ClosestPair {
    ArrayList<Point> points = new ArrayList<>(); // unordered
    HashSet<TreeSet<Point>> closest = new HashSet<>();

    public double closestPair() {

      // Part 1 of the algorithm - sort points ASC by X and Y
      Collections.sort(points);
      final ArrayList<Point> pointsByX = new ArrayList<>( this.points );
      final ArrayList<Point> pointsByY = new ArrayList<>( this.points );

      // give rank for each in x
      int rank = 1;
      for(Point p : pointsByX) {
        p.rank = rank++;
      }

      return closestPairRecursive(pointsByX, pointsByY, 1, pointsByX.size()); // end is exclusive, so no -1 there
    }

    // start and end NOT zero based
    private double closestPairRecursive(final ArrayList<Point> pointsX, final ArrayList<Point> pointsY, final int start, final int end) {



      // Print out details of current run
      ArrayList<Point> sublistX = new ArrayList<>(pointsX.subList(start-1, end));
      print(String.format("Current subset %d...%d has %d points:", start, end, sublistX.size()), false );
      Point.printPoints( sublistX );
      // ---

      double d = Double.POSITIVE_INFINITY;

      if(start < end) {

        final int mid = (start+end) / 2;
        final double d1 = closestPairRecursive(pointsX, pointsY, start, mid);
        final double d2 = closestPairRecursive(pointsX, pointsY, mid+1, end);

        d = Math.min(d1, d2);
        final double d_ = d; // final for the lambda...

        // Calculate the center line L
        final double l = (pointsX.get(mid-1).x + pointsX.get(mid).x) / 2.0;

        // "take a point in y_arr into 2d-strip only if it is inside start...end in x_arr"
        final ArrayList<Point> strip = pointsY.stream()
            .filter( (p) -> {
              return (p.rank >= start && p.rank <= end) && ( Math.abs(p.x - l) <= d_ );
            }).collect(Collectors.toCollection(ArrayList::new ));

        // Sort
        strip.sort( (p1, p2) -> (p1.y != p2.y) ? p1.y - p2.y : p1.x - p2.x ); //  primarily by y- and secondarily by x-coordinates. (!!!)

        // Print strip details
        print( String.format(Locale.US, "Current subset %d...%d has 2d-strip with %d points:", start, end, strip.size() ), false );
        Point.printPoints(strip);


        for(final Point p : strip) {
          // Compute distance from strip[i] to (at most) 7 following points.
          for(int pi = strip.indexOf(p)+1; pi < Math.min(strip.size(), pi+8); pi++) {
            // If new closer pair found. Notice that equal distance is also added!
            double dist = p.getDistance(strip.get(pi));
            if( dist <= d ) {

              // If smaller than [but not equal!], we can empty closest pairs found so far
              //if(dist < d) { this.closest.clear(); }

              // Make a new pair as TreeSet. add it to the set nearest points
              final TreeSet<Point> closestPair = new TreeSet<>();
              closestPair.add(p);
              closestPair.add(strip.get(pi));
              this.closest.add(closestPair);

              d = dist;
            }
          }
        }

      }

      final String dist = (d == Double.POSITIVE_INFINITY) ? "infinity" : String.format(Locale.US, "%.3f", d); // 'infinity' if no change
      print(String.format("Current subset %d...%d returns distance: %s", start, end, dist), true);
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

      Point p0 = new Point(73, 41);
      Point p1 = new Point(73, 17);
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
      Collections.sort(ps);
      System.out.println( ps ); // p0 1 2

      List<Point> pointsByY = ((ArrayList<Point>) ps.clone()).stream().sorted( (p1_, p2_) -> p1_.y - p2_.y ).collect(Collectors.toList());
      System.out.println( pointsByY ); // p0 1 2
      System.out.println( ps ); // p0 1 2

    }

  }

}
