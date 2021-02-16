import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Algo5_4 {

  public static void main(String[] args) {

    //Test.test();

    if (args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if (args.length == 2 && args[1].equals("0")) {
      filename = "src/" + filename;
    }

    DirectedWeightedGraph graph = new DirectedWeightedGraph();
    FordFulkerson ff = new FordFulkerson(graph);

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      // Read each line
      int lineNo = 1;
      int s = -999;
      int t = -999; // start and end nodes


      for (String line = br.readLine(); line != null; line = br.readLine()) {
        //System.out.print(line + "\n");
        String[] line_ = line.split(" "); // [x, y]

        // First line is for start and end nodes S & T
        if(lineNo++ == 1) {
          s = Integer.parseInt(line_[0]);
          t = Integer.parseInt(line_[1]);
          continue;
        }

        Node n1 = new Node( Integer.parseInt(line_[0]));
        Node n2 = new Node( Integer.parseInt(line_[1]));
        Edge e = new Edge(n1, n2, Integer.parseInt(line_[2]));

        // Add nodes and edges
        graph.addNode(n1).addNode(n2).addEdge(e);

        // And if node is start or end for FF alg. add it IF NOT ALREADY OTHERWISE WILL REF. TO 'new' Obj
        // that was not added to graph as node. So only do it once
        if(n1.number == s && ff.start==null) {
          ff.start = n1;
        }
        if(n2.number == s && ff.start==null) {
          ff.start = n2;
        }
        if(n1.number == t && ff.end==null) {
          ff.end = n1;
        }
        if(n2.number == t && ff.start==null) {
          ff.end = n2;
        }

      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    // The main program:
    System.out.println( ff.toString() );

    /*Node test = new Node(5);
    Node test2 = new Node(2);
    System.out.println( graph.getNodeNeighbours(test) );
    System.out.println( graph.getNodeNeighbours(test2) );
    */

    ff.searchAugmentingPath();

  }


  static class FordFulkerson {

    private DirectedWeightedGraph graph;
    private Node start, end;    // s and t nodes

    public FordFulkerson(DirectedWeightedGraph graph) {
      this.graph = graph;
    }


    public void searchAugmentingPath() {

      // Capacity of edge (u, v) is same as its weight!

      int flow = 0;
      LinkedList<Node> q = new LinkedList<>();
      q.add(this.start);
      Node v;
      TreeSet<Node> visited = new TreeSet<>(); // initially empty
      boolean endReached = false; // ??

      // Maintain flow
      HashMap<Edge, Integer> capacity = new HashMap<>();
      for(final Edge e : graph.edges ) {
        capacity.put(e, e.w);
      }

      while(!q.isEmpty() && !endReached) {
        v = q.poll(); //
        // for each neighbor w of v in descending order
        for(Node w : graph.getNodeNeighbours(v)) {
          // If w is unvisited and edge from v to w has residual capacity > 0
          Edge e = graph.getEdge(v, w);
          if(! visited.contains(w) && capacity.get(e) > 0) {
            System.out.format("Set %s prev: %s\n", w, v);

            // Set prev and update residual capacity
            w.prevs.add(v);  // link w to v in a possible path
            int cap = capacity.get(e);
            capacity.put(e, cap-)

            q.add(w);

            //
            visited.add(w);
            if(w.equals(this.end)) { endReached = true; }
          }
        }
      }

      // If t was reached: form an augmenting path by tracing the prev-values from t back to s
      if(endReached) {

        // Trace back from end (node t) to start (s). This will be the augmenting path
        Node n = this.end;
        do {
          System.out.format( "%s (<-%s), ", n, n.prev );
          n = n.prev;
        } while( n != null );

      } else {
        return;
      }

    }



    @Override
    public String toString() {
      return String.format(">> Nodes: %s\n>> Edges: %s\n>> s/t: %s, %s", graph.nodes, graph.edges, start, end);
    }

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

    public Edge getEdge(final Node source, final Node target) {
      return edges.stream().filter( (e) -> e.source.equals(source) && e.target.equals(target) ).findFirst().orElse(null);
    }

    // Order is descending by default 10,9,8...
    public NavigableSet<Node> getNodeNeighbours(final Node n) {
      // Take edges where start node is n, and then take their targets. Then convert to reversed ordered set
      NavigableSet<Node> neig = this.edges.stream().filter(e -> e.source.equals(n)).map(edge -> edge.target).collect(Collectors.toCollection(TreeSet::new));
      return neig.descendingSet();
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

    public TreeSet<Node> prevs = new TreeSet<>(); // for FF algo.

    public Node(int n) {
      this.number = n;
    }

    @Override
    public String toString() {
      return String.format("%d", number);
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
