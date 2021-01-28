import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Algo2_5 {

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

    // Jobs
    ArrayList<Job> jobs = new ArrayList<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {

        List<Integer> times = Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toList()); // [start, end]
        //System.out.print(line + "\n");
        jobs.add( new Job(times.get(0), times.get(1)) );

      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    Collections.sort(jobs);
    //System.out.println( "All jobs " + jobs );

    // -- Run --
    // Do it for each job and if len is GTE, keep it
    int maxSize = 0;
    ArrayList< ArrayList<Job> > possibleSchedules = new ArrayList<>();

    for(int i=0; i<jobs.size(); i++) {

      // Get the optimal greedy schedule starting from job i
      ArrayList<Job> jobsSub = new ArrayList<>( jobs.subList(i, jobs.size()) );
      ArrayList<Job> schedule = IntervalSchedule.run( jobsSub );

      // Should we keep the schedule?
      if(schedule.size() > maxSize) { possibleSchedules.clear(); } // we don't need the smaller sc. anymore
      if(schedule.size() >= maxSize) {
        maxSize = schedule.size();
        possibleSchedules.add( schedule ); // Add solution
      }
      //System.out.println( i + " gives: " + schedule );
    }

    // Init
    IntervalSchedule.pintResults( possibleSchedules );
  }


  public static class IntervalSchedule {

    public static ArrayList<Job> run(ArrayList<Job> jobs) {

      // Init algorithm
      ArrayList<Job> selected = new ArrayList<>();
      Collections.sort(jobs);
      selected.add( jobs.get(0) ); // always select the 1.

      // If there are two or more equally good candidates for selecting the next job (= their finish times are the same)
      // choose the one that starts latest (= lasts the shortest time).
      //System.out.println(jobs.subList(fromIndex, jobs.size()));

      Job next = getBest(selected.get(0), jobs);
      while(next != null) {
        // Final check. Would it create an overlap?
        if(! doesOverlap(selected.get(0), next) ) {
          selected.add(next);
          next = getBest(selected.get( selected.size()-1 ), jobs);
        } else {
          break; // we are done
        }
      }


      return selected;
    }

    // Find the best Job from array of jobs after target Job
    public static Job getBest(Job target, final ArrayList<Job> jobs) {

      Job best = null;
      for(Job j : jobs.stream().filter(el -> el.start >= target.end).collect(Collectors.toList())) {
        // if best still null OR end is smaller OR (same, but starts later) -> update
        if(best == null || j.end < best.end || (j.end == best.end && j.start > best.start ) ) {
          best = j;
        }
      }
      return best;

    }

    // Will j overlap with target. target[01-hh] + j[hh-26] not possible
    public static boolean doesOverlap(Job target, Job j) {
      return j.end - 24 > target.start; // EQ allowed
    }

    public static void pintResults(final ArrayList<ArrayList<Job>> schedules) {
      // If there is more than one equally good solution (has the same maximal number of jobs),
      // select the one whose first job has the earliest start time. If two optimal solution candidates
      // have equal start times, select from them one with the earliest finish time.
      //System.out.println("All schedules: " + schedules);
      if(!schedules.isEmpty()) {
        ArrayList<Job> bestSchedule = schedules.get(0);  // ref. to the best schedule
        for (ArrayList<Job> sc : schedules) {

          if (sc.get(0).start < bestSchedule.get(0).start) {
            bestSchedule = sc;
          }
          // else if start is same, compare ending times. Update if sc (end time) is better than current best
          else if (sc.get(0).start == bestSchedule.get(0).start && sc.get(0).end < bestSchedule.get(0).end) {
            bestSchedule = sc;
          }
        }

        System.out.println("Maximum number of jobs: " + bestSchedule.size());
        System.out.print("Selected jobs:");
        for( Job j :  bestSchedule ) {
          System.out.print( " " + j.toString() );
        }
        System.out.print("\n");
      }

    }
    // Should never be null/empty.


  }


  public static class Job implements Comparable<Job> {

    int start, end; // end might be > 24

    public Job(int start, int end) {
        this.start = start;
        if(end < start) {
          this.end = 24 + end;
        } else {
          this.end = end;
        }

    }

    public boolean isReversed() {
      return this.end < this.start;
    }

    public int getDuration() {
      return this.end - this.start;
    }

    @Override
    public int compareTo(Job j) {
      return (this.end != j.end) ? this.end - j.end : j.start - this.start;    // by end
    }

    @Override
    public boolean equals(Object o) {
      if(o == null) { return false; }
      return (this.start == ((Job)o).start && this.end == ((Job) o).end);
    }

    @Override
    public String toString() {
      return String.format("%d-%d", this.start, this.end % 24);
    }
  }

  public static class Test {

    public static void test() {
      Job j1 = new Job(16, 21);
      Job j2 = new Job(16, 22);
      Job j3 = new Job(16, 2);
      Job j4 = new Job(1, 23);
      Job j5 = new Job(17, 10);
      Job j6 = new Job(17, 1);
      Job j7 = new Job(17, 18);
      Job j8 = new Job(23, 22);


      ArrayList<Job> jobs = new ArrayList<>();
      jobs.add(j1);
      jobs.add(j2);
      jobs.add(j3);
      jobs.add(j4);
      jobs.add(j5);
      jobs.add(j6);
      jobs.add(j7);
      jobs.add(j8);
      Collections.sort(jobs);
      System.out.println( jobs );

      System.out.println( j1.compareTo(j2) ); // 6
      System.out.println( j1.compareTo(j3) ); // -4
      System.out.println( j3.compareTo(j1) ); // 4
      System.out.println( j1.compareTo(j4) ); // 11
      System.out.println( j2.compareTo(j3) ); //

      System.out.println( "Duration " + j1.getDuration() ); //
      System.out.println( "Duration " + j2.getDuration() ); //
      System.out.println( "Duration " + j3.getDuration() ); //
      System.out.println( "Duration " + j8.getDuration() ); //
    }

  }

}
