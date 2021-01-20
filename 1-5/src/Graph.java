import java.util.*;

public class Graph {

  // has vertices to these nodes. If not directed, store both directions! A->B and B->A
  // <1>: { <2>, <3>, <99> }
  Map<Node, HashSet<Node>> nodes;

  public Graph() {
    this.nodes = new HashMap<>();
  }

  public void addToGraph(Node n) {
    if(!this.nodes.containsKey(n)) {
      this.nodes.put(n, new HashSet<>());
    }
  }

  public HashSet<Node> getNeighbours(Node n) {
    return this.nodes.getOrDefault(n, null);
  }

  public void addEdge(Node start, Node target) {
    if(this.nodes.containsKey(start)) {
      this.nodes.get(start).add(target);
    }
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Map.Entry<Node, HashSet<Node>> node : nodes.entrySet()) {
      sb.append( node.toString() + "\n" );
    }
    return sb.toString();
  }




    static class KosarajusAlgorithm {

      final Graph g;
      HashSet<Graph.Node> unvisited = new HashSet<>();
      HashMap<Graph.Node, HashSet<Graph.Node>> components = new HashMap<>();
      ArrayList<Node> vertices = new ArrayList<>(); // aka list L. Stores visited Nodes in reverse order(!)

      HashSet<Node> assigned = new HashSet<>(); // for step 2, a little bit redundancy


      public KosarajusAlgorithm(final Graph g) {
        this.g = g;
        unvisited.addAll(g.nodes.keySet());
      }

      // The main task - See if graph is strongly connected
      // https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm

      public void getStrongComponents() {

        // Step 1
        for(Node n : unvisited) {
          visit(n); // recursion
        }

        // Step 2
        for(Node u : vertices ) {
          assign(u, u);
        }

      }


      public void visit(final Graph.Node n) {

        if(unvisited.contains(n)) {
          unvisited.remove(n); // is visited
          for(Node n_ : g.getNeighbours(n) ) {
            visit(n_);
            vertices.add(0, n); // ! [5,4,3 ...]
          }
        } // else nothing happens
      }

      public void assign(final Node u, final Node root) {

        // if node u is not yet assigned to a component (not in set assigned)
        // Assign u as belonging to the component whose root is root.
        if(! assigned.contains(u)) {
          HashSet<Node> comps = new HashSet<>();
          comps.add(u);
          components.put(root, comps ); // note that root/key is also added to the set itself
          assigned.add(u);
          // For each neighbour, call recursively
          for( Node n_ : g.getNeighbours(u) ) {
            assign(n_, root);
          }
        } // else do nothing
      }

      public void printResults() {

        System.out.println( "Components: " + this.components.size() );
        System.out.println( this.components );

        for( Node n : components.keySet() ) {
          System.out.println("Root: " + n);
        }

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
        return ((Node) o).number == this.number;
      }

      // https://stackoverflow.com/a/14505443/8094012
      public int hashCode() {
        return 0;
      }

    }


}
