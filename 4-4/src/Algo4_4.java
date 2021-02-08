import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Algo4_4 {


  public static void main(String args[]) {

    if (args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if (args.length == 2 && args[1].equals("0")) {
      filename = "src/" + filename;
    }

    DirectedWeightedGraph dwg = new DirectedWeightedGraph();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        //System.out.print(line + "\n");
        String[] n = line.split(" "); // [x, y, w]

        Node n1 = new Node( Integer.parseInt(n[0]) );
        Node n2 = new Node( Integer.parseInt(n[1]) );
        Edge e = new Edge(n1, n2, Integer.parseInt(n[2])); // n1 --> n2 (w)

        dwg.addNode(n1).addNode(n2);
        dwg.addEdge(e);

      } // end of read

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    //System.out.println( dwg.toString() );

    dwg.floydWarshall();

  }


  static class DirectedWeightedGraph {

    TreeSet<Node> nodes = new TreeSet<>();
    HashSet<Edge> edges = new HashSet<>();

    public DirectedWeightedGraph addNode(Node n) {
      this.nodes.add(n);
      return this;
    }
    public DirectedWeightedGraph addEdge(Edge e) {
      this.edges.add(e);
      return this;
    }


    public void floydWarshall() {

      final ArrayList<Node> nodes = new ArrayList<>(this.nodes); // sorted!
      final Double[][]  dist    = new Double[nodes.size()][nodes.size()];
      final Node[][]    next    = new Node[nodes.size()][nodes.size()];

      // Init dist to INF
      for(Double[] arr1 : dist) {
        Arrays.fill(arr1, Double.POSITIVE_INFINITY);
      }

      for(Node[] arr2 : next) {
        Arrays.fill(arr2, null);
      }


      // For each edge e: dist[u][v] = w(e)
      for(Edge e : this.edges) {
        final int indexSource = nodes.indexOf(e.source);
        final int indexTarget = nodes.indexOf(e.target);
        dist[indexSource][indexTarget] = (double) e.w; // [s][t] = w
        next[indexSource][indexTarget] = e.target;    // (1) next[u][v] ← v
      }

      // For each Node n set distance to n->n to 0
      for(Node n : nodes) {
        final int i = nodes.indexOf(n);
        dist[i][i] = (double) 0; // for example [Node 3][Node 3] = 0
        next[i][i] = n;   // (2) next[v][v] ← v
      }

      // Step 3 - Init. done.
      int iterNum = 0;

      // Print initial status (aka k=0)
      this.printMatrix(dist, iterNum);

      // Iterations k = 1...size
      for(final Node k : nodes) {
        final int index_k = nodes.indexOf(k);
        for(final Node i : nodes) {
          final int index_i = nodes.indexOf(i);
          for(final Node j : nodes) {
            final int index_j = nodes.indexOf(j);

            if( dist[index_i][index_j] > dist[index_i][index_k] + dist[index_k][index_j] ) {
              dist[index_i][index_j] = dist[index_i][index_k] + dist[index_k][index_j];
              // Construct the next-matrix
              next[index_i][index_j] = next[index_i][index_k];
            }
          }
        }

        iterNum++;
        this.printMatrix(dist, iterNum);

        // Negative cycles found?
        final int negIndex = isNegativeCycleDetected(dist);
        if( negIndex != -1) {
          Node uv = nodes.get(negIndex); // get node from [i,i]
          System.out.println("A negative cycle detected: " + negIndex + uv);
          System.out.println( getCycle(uv, uv, next, nodes) );
          return; // if neg. cycle ... the computation will be stopped after the iteration has ended.
        }
        // otherwise continue

      } // main


    }


    private ArrayList<Node> getCycle(Node u, Node v, final Node[][] next, final ArrayList<Node> nodes) {
      ArrayList<Node> cycle = new ArrayList<>();


      if( next[nodes.indexOf(u)][nodes.indexOf(v)] == null ) {
        return cycle; // []
      }
      cycle.add(u); // root

      u = next[nodes.indexOf(u)][nodes.indexOf(v)];
      cycle.add(u);

      while(! u.equals(v) ) {
        u = next[ nodes.indexOf(u) ][ nodes.indexOf(v) ];
        cycle.add(u);
      }
      return cycle;

    }

    /**
     * "If a negative cycle is detected during the current iteration,
     * select the cell (i, i) with smallest i whose cell value is negative."
     *
     * @return int where value is the 0-based index of the smallest (row, col) where neg. value was found.
     * -1 is returned if no negative cycles
     */
    private int isNegativeCycleDetected(final Double[][] dist) {
      for(int i=0; i<dist.length; i++) {
        if( dist[i][i] < 0 ) {
          return i;
        }
      }
      return -1;
    }

    private void printMatrix(final Double[][] dist, int iterNum) {
      System.out.println("Iteration " + iterNum);

      for(Double[] row : dist) {
        for(Double val : row) {
          String output;
          if(val == Double.POSITIVE_INFINITY) {
            output = "-";
          } else {
            output = String.format("%.0f", val);
          }
          System.out.printf("%5s", output);
        }
        System.out.print("\n"); // after each row
      }
      System.out.print("\n");
    }

    @Override
    public String toString() {
      return String.format( "-- Nodes %s\n-- Edges %s", this.nodes.toString(), this.edges.toString() );
    }


  }


  static class Edge {

    Node source, target;
    int w;

    public Edge(Node s, Node t, int w) {
      this.source = s;
      this.target = t;
      this.w = w;
    }

    @Override
    public String toString() {
      return String.format("(%s, %s (%d))", source, target, w);
    }


    @Override
    public boolean equals(Object o) {
      if(o instanceof Edge) {
        return ((Edge) o).source == this.source && ((Edge) o).target == this.target && ((Edge) o).w == this.w;
      }
      return false;
    }

    // https://stackoverflow.com/a/14505443/8094012
    public int hashCode() {
      return 0;
    }

  }

  static class Node implements Comparable<Node> {

    int number; // identifier

    public Node(int n) {
      this.number = n;
    }

    @Override
    public String toString() {
      return String.format("<%d>", number);
    }

    @Override
    public int compareTo(Node n) {
      return this.number - n.number;
    }

    @Override
    public boolean equals(Object o) {
      if(!(o instanceof Node)) { return false; }
      return ((Node) o).number == this.number;
    }

    // https://stackoverflow.com/a/14505443/8094012
    public int hashCode() {
      return 0;
    }

  }

}
