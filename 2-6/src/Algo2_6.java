import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Algo2_6 {

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

    WeightedGraph g = new WeightedGraph();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {

        //System.out.print(line + "\n");
        String[] line_ = line.split(" "); // [u, v, w]

        Node n1 = new Node(Integer.parseInt(line_[0]));
        Node n2 = new Node(Integer.parseInt(line_[1]));

        g.addToGraph(n1);
        g.addToGraph(n2);
        g.addEdge( n1, n2, Integer.parseInt(line_[2]) );



      } // end of read
    } catch (IOException e) {
      e.printStackTrace();
    }

    Test.test();
    //System.out.println( g.toString() );


  }


  // Undirected!
  public static class WeightedGraph {

    // has vertices to these nodes. If not directed, store both directions! A->B and B->A
    // <1>: { <2>, <3>, <99> }
    ArrayList<Node> nodes;
    HashSet<Edge> edges;

    public WeightedGraph() {
      this.nodes  = new ArrayList<>();
      this.edges  = new HashSet<>();
    }

    public void addToGraph(Node n) {
      if(! this.nodes.contains(n)) {
        this.nodes.add(n);
      }
    }

    /*public HashSet<Node> getNeighbours(Node n) {
      return this.nodes.getOrDefault(n, new HashSet<>()); // or empty hash set
    }*/

    public void addEdge(Node n1, Node n2, Integer w) {
      this.edges.add( new Edge(n1, n2, w) );
    }


    //
    public void runBoruvka(final ArrayList<Node> nodes, final HashSet<Edge> edges) {

      HashMap<Node, HashSet<Node>> nodes_  = new HashMap<>();       // aka components / labels
      HashMap<HashSet<Node>, Edge> candidates   = new HashMap<>();  // the trees content/set is the key here

      // Init nodes as single trees and candidateEdges to point to null
      for(Node n : nodes) {
        HashSet<Node> tree = new HashSet<>();
        nodes_.put(n, tree);
        candidates.put(tree, null ); // each tree has a candidate Edge
      }

      mainloop:
      while(! "cats".equals("dogs") ) {

        // For each edge
        for(Edge e : edges) {

          // If they belong to different trees/components...
          final Node[] ns = (Node[]) e.nodes.toArray(); // cannot get from set without converting...
          if( nodes_.get(ns[0]) != nodes_.get(ns[1]) ) {

            final HashSet<Node> treeOfNode1 = nodes_.get( ns[0] );
            final HashSet<Node> treeOfNode2 = nodes_.get( ns[1] );

            // Update this edge to be the candidate Edge for both
            // Get nodes current tree, use it as a key to get the candidate of that tree. See if better Edge (smaller weight)
            final Edge candidate = candidates.get( nodes_.get(ns[0]));
            if( candidate == null || e.w < candidate.w ) {
              candidates.putIfAbsent(treeOfNode1, candidate);
              candidates.putIfAbsent(treeOfNode2, candidate);
            }
          }
        }

        // Each edge is now checked. If any candidates were found (aka not null), process them
        for(HashSet<Node> tree: candidates.keySet()) {
          final Edge e = candidates.get(tree);
          if(e != null ) {
            final Node[] ns = (Node[]) e.nodes.toArray(); // again, convert Edge's set of nodes to different format
            // Check if Nodes (still) belong to different trees/components. Aka the ref. to sets are different
            HashSet<Node> tree1 = nodes_.get(ns[0]);
            HashSet<Node> tree2 = nodes_.get(ns[1]);
            if( tree1 != tree2 ) {
              // see which set is merged to where
              // label ???
              if(tree1.size() > tree2.size() || (tree1.size() == tree2.size() && )) {

              }
            }
          }
        }
      }

    }



    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (Node node : nodes) {
        sb.append(node.toString() + ", ");
      }
      //
      for (Edge e : edges) {
        sb.append(e.toString() + "\n");
      }

      return sb.toString();
    }

  } // end of class Graph

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
      if(o == null) { return false; }
      return ((Node) o).number == this.number;
    }

    // https://stackoverflow.com/a/14505443/8094012
    public int hashCode() {
      return 0;
    }

  }

  public static class Edge {

    HashSet<Node> nodes = new HashSet<>();
    Integer w = 0; // weight

    public Edge(Node n1, Node n2, Integer weight) {
      this.nodes.add(n1);
      this.nodes.add(n2);
      this.w      = weight;
    }

    @Override
    public String toString() {
      return String.format("(%s (w=%d))", nodes, w );
    }

    @Override
    public boolean equals(Object o) {
      if(o == null) { return false; }


      return  (
          this.w.equals( ((Edge) o).w ) && ( this.nodes.equals( ((Edge) o).nodes) )
      );
    }

    // https://stackoverflow.com/a/14505443/8094012
    public int hashCode() {
      return 0;
    }

  }


  public static class Test {

    public static void test() {

      Node n1 = new Node(1);
      Node n2 = new Node(2);
      Node n3 = new Node(3);

      System.out.println(new Edge(n1, n2, 1).equals(new Edge(n1, n2, 1))); // t
      System.out.println(new Edge(n1, n2, 1).equals(new Edge(n2, n1, 1))); // t

      System.out.println(new Edge(n1, n1, 1).equals(new Edge(n1, n1, 1))); // t
      System.out.println(new Edge(n1, n1, Integer.MAX_VALUE).equals(new Edge(n1, n1, Integer.MAX_VALUE))); // t?
      System.out.println(new Edge(n1, n2, 1).equals(new Edge(n3, n2, 1))); // f
      System.out.println(new Edge(n1, n2, 1).equals(new Edge(n2, n1, -1234))); // f
    }

  }


}
