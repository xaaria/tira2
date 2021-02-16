import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Algo5_6 {

  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if (args.length == 2 && args[1].equals("0")) {
      filename = "src/" + filename;
    }

    DirectedGraph graph = new DirectedGraph();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        //System.out.print(line + "\n");
        String[] line_ = line.split(" "); // nodes [u, v]
        Node u = new Node( Integer.parseInt(line_[0]));
        Node v = new Node( Integer.parseInt(line_[1]));
        Edge e = new Edge(u, v);

        // Add nodes and edges
        //graph.addNode(u).addNode(v) // not needed in this ex.
        graph.addEdge(e);
        graph.setOne.add(u);
        graph.setTwo.add(v);

      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    // The main program:
    System.out.println( graph.toString() );

    MaximumMatching mm = new MaximumMatching(graph);
    mm.getMaximumMatching();


  }


  public static class MaximumMatching {

    DirectedGraph graph;

    public MaximumMatching(DirectedGraph g) {
      this.graph = g;
    }

    // Returns the largest matching from all possible
    // r: TreeSet<Edge>
    public void getMaximumMatching() {

      HashSet<Edge> resultSet = new HashSet<>();
      HashSet<Node> discovered = new HashSet<>();

      LinkedList<Node> Q = new LinkedList<>(graph.setOne); // root
      LinkedList<Edge> R = new LinkedList<>();

      visit(Q, R);




    }

    public void visit(LinkedList<Node> lefts, LinkedList<Edge> R) {


      if(lefts.peek() != null) {

        Node ln = lefts.pop();
        System.out.format("@ %s\n", ln);

        for (final Node rn : graph.getNodeNeighbours(ln)) {
          //System.out.format("\t%s - %s\n", ln, rn);
          Edge e = new Edge(ln, rn);
          R.add(e);
          //System.out.format("%s\n", R);
          visit((LinkedList<Node>) lefts.clone(), R);
          R.removeLast();
        }
      }

      System.out.format("end >> %s\n", R);

    }

  }





  static class DirectedGraph {

    TreeSet<Node> nodes = new TreeSet<>();
    HashSet<Edge> edges = new HashSet<>();

    public TreeSet<Node> setOne = new TreeSet<>();
    public TreeSet<Node> setTwo = new TreeSet<>();

    public DirectedGraph addNode(Node n) {
      this.nodes.add(n);
      return this;
    }

    public DirectedGraph addEdge(Edge e) {
      this.edges.add(e);
      return this;
    }

    @Override
    public String toString() {
      return String.format("setOne: %s\nSetTwo: %s", setOne, setTwo);
    }

    public TreeSet<Node> getNodeNeighbours(final Node n) {
      // Take edges where start node is n, and then take their targets. Then convert to reversed ordered set
      return this.edges.stream().filter(e -> e.source.equals(n)).map(edge -> edge.target).collect(Collectors.toCollection(TreeSet::new));
    }

    public Edge getEdge(final Node source, final Node target) {
      return edges.stream().filter( (e) -> e.source.equals(source) && e.target.equals(target) ).findFirst().orElse(null);
    }

  }

  static class Edge {

    Node source, target;

    public Edge(Node s, Node t) {
      this.source = s;
      this.target = t;
    }

    @Override
    public String toString() {
      return String.format("(%s, %s)", source, target);
    }


    @Override
    public boolean equals(Object o) {
      if(o instanceof Edge) {
        return ((Edge) o).source == this.source && ((Edge) o).target == this.target;
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
    //TreeSet<Node> prevs = new TreeSet<>();

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
