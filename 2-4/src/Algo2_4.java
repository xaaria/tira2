import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Algo2_4 {


  public static void main(String[] args) {

    if(args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if( args.length == 2 && args[1].equals("0") ) {
      filename = "src/" + filename;
    }

    IntervalPartitioning ip = new IntervalPartitioning();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {

        //System.out.print(line + "\n");
        List<Integer> times = Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toList()); // [start, end]

        // Add to list of lectures
        ip.lectures.add( new Lecture(times.get(0), times.get(1) ));

      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    //
    ip.sortLectures();
    // == Run & Print ==
    ip.printSchedule( ip.run() );


  }


  public static class IntervalPartitioning {

    LinkedList<Lecture> lectures = new LinkedList<>();


    // The main algorithm
    public HashMap<Integer, ArrayList<Lecture>> run() {

      // Key is 1-based class room number. Value is the list of selected lectures
      // 1 => [ [9-12], [12-13], [14-20] ... ]
      LinkedList<Lecture> lectures = (LinkedList<Lecture>) this.lectures.clone();
      HashMap<Integer, ArrayList<Lecture>> schedule = new HashMap<>();
      int classRooms = 1;

      // Order the lectures
      Collections.sort(lectures);

      // For each lecture l find a class room
      lectureLoop:
      while(! lectures.isEmpty()) {

        Lecture l = lectures.pop();
        //System.out.println( ">>" + l.toString() );

        // For each class room cr, see if lecture fits in. If not, allocate a new class room and append there
        for( Integer cr : schedule.keySet()) {

          // Get the ending time of the last Lecture. If fits in, put it there. For ex. put [12-14] in [ [10-12] ]
          ArrayList<Lecture> lecturesInClassRoom = schedule.get(cr);
          Lecture last = lecturesInClassRoom.get( lecturesInClassRoom.size()-1 ); // size should always be 1 or more

          //
          if( l.start >= last.end ) {
            lecturesInClassRoom.add(l);
            continue lectureLoop; // get out of the for loop and start from while
          }
          // else continue to the next class room [for]
        }

        // If we checked every class room, and no free space was found, allocate a new one, and put it there!
        // This step is skipped with continue if class room was found. see above.
        ArrayList<Lecture> lecturesOfNewClassRoom = new ArrayList<>();
        lecturesOfNewClassRoom.add(l);
        schedule.put(classRooms++,  lecturesOfNewClassRoom);

      }

      // finally return the schedule
      return schedule;

    }

    public void printSchedule(final HashMap<Integer, ArrayList<Lecture>> schedule) {

      //
      System.out.println( String.format("Required number of class rooms: %d", schedule.size()) );

      // Each classroom in order
      for(Integer classRoom : schedule.keySet()) {
        System.out.print("Schedule for class room " + classRoom + ":" );
        for(Lecture lec : schedule.get(classRoom)) {
          System.out.print(" " + lec);
        }
        System.out.print("\n");
      }
    }


    public LinkedList<Lecture> sortLectures() {
      Collections.sort(this.lectures);
      return this.lectures;
    }

    @Override
    public String toString() {
      return String.format("<Lectures %s>", this.lectures.toString());
    }

  }




  public static class Lecture implements Comparable<Lecture> {

    public int start;
    public int end;

    public Lecture(int start, int end) {
      this.start  = start;
      this.end    = end;
    }


    @Override
    public int compareTo(Lecture o) {
      if(this.start == o.start) {
        return this.end - o.end;
      }
      return this.start - o.start;
    }

    @Override
    public boolean equals(Object o) {
      return this.start == ((Lecture) o).start && this.end == ((Lecture) o).end;
    }

    @Override
    public String toString() {
      return String.format("%d-%d", start, end);
    }
  }


  public static class Test {

    public void test() {
      ArrayList<Lecture> lecs = new ArrayList<>();
      lecs.add( new Lecture(12, 13) );
      lecs.add( new Lecture(9, 11) );
      lecs.add( new Lecture(9, 12) );
      lecs.add( new Lecture(13, 15) );
      lecs.add( new Lecture(10, 14) );
      lecs.add( new Lecture(20, 23) );
      lecs.add( new Lecture(13, 15) );
      lecs.add( new Lecture(0, 23) );
      lecs.add( new Lecture(9, 11) );
      Collections.sort(lecs);
      System.out.println( lecs.toString() );
    }

  }


}
