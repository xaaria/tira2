
/*
* Implement a program that reads a description of a directed graph from an input text
* file and prints out all strong components of the graph.
*
* */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Algo1_5 {


  public static void main(String args[]) {

    if(args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    String filename = args[0];

    // is WETO testing env. 0 = false
    if( args.length == 2 && args[1].equals("0") ) {
      filename = "src/" + filename;
    }

    Graph g = new Graph();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {

        //System.out.print(line + "\n");
        String[] n = line.split(" "); // [x, y]

        Graph.Node n1 = new Graph.Node(Integer.parseInt(n[0]));
        Graph.Node n2 = new Graph.Node(Integer.parseInt(n[1]));

        // Does not add duplicates
        g.addToGraph(n1);
        g.addToGraph(n2);

        g.addEdge(n1, n2);

      } // end of read

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // print
    //System.out.println( g.toString() );

    // Graph is initialized
    Graph.KosarajusAlgorithm kosaraju = new Graph.KosarajusAlgorithm(g);
    kosaraju.getStrongComponents(); // run the algorithm
    kosaraju.printResults();


  }


  // -------


  public static class Graph {

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
      return this.nodes.getOrDefault(n, new HashSet<>()); // or empty hash set
    }

    public void addEdge(Node start, Node target) {
      if(this.nodes.containsKey(start)) {
        this.nodes.get(start).add(target);
      }
    }

    /*
     * Returns a reversed graph. Only meaningful when graph is directed.
     * For example Graph; A: { B, C } becomes
     * B: { A }
     * C: { A }
     * */
    public Map<Node, HashSet<Node>> getReversed() {

      Map<Node, HashSet<Node>> rev = new HashMap<>();

      for( Node n : this.nodes.keySet() ) {
        for( Node u : this.nodes.get(n) ) {
          HashSet<Node> conns = rev.getOrDefault(u, new HashSet<>());
          conns.add(n);
          rev.put(u, conns);
        }
      }
      //System.out.println(rev);
      return rev;

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

      Graph g;

      // Step 1
      Map<Graph.Node, Boolean> unvisited = new HashMap<>();
      // Step 2
      HashMap<Graph.Node, HashSet<Graph.Node>> components = new HashMap<>();
      ArrayList<Node> vertices  = new ArrayList<>();  // aka list L. Stores visited Nodes in reverse order(!)
      HashSet<Node> assigned    = new HashSet<>();    // for step 2, a little bit redundancy


      public KosarajusAlgorithm(final Graph g) {
        this.g = g;

        // Create Map Node : Boolean from graphs nodes
        for(Node n : g.nodes.keySet()) {
          unvisited.put(n, Boolean.TRUE);
        }
      }

      // The main task - See if graph is strongly connected
      // https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm

      public void getStrongComponents() {

        // Step 1
        //System.out.println( unvisited );

        for(Node n : unvisited.keySet()) {
          visit(n); // recursion
        }

        // Step 2 :: Reverse graph nodes
        this.g.nodes = this.g.getReversed();
        //System.out.println( vertices );

        // Step 2
        for(Node u : vertices ) {
          assign(u, u);
        }

      }


      public void visit(final Graph.Node n) {
        //System.out.println("visit" + n );
        if(unvisited.containsKey(n) && unvisited.get(n) == Boolean.TRUE) {

          // 1 Mark as visited
          unvisited.put(n, Boolean.FALSE); // is visited
          //System.out.println("\tMark visited: " + n);

          // 2 Visit each neighbour
          for(Node n_ : g.getNeighbours(n) ) {
            visit(n_);
          }

          // 3 Prepend node n to list L (this.vertices)
          //System.out.println("\tprepend " + n);
          vertices.add(0, n); // ! [5,4,3 ...]
        } // else nothing happens
      }

      public void assign(final Node u, final Node root) {

        // if node u is not yet assigned to a component (not in set assigned)
        // Assign u as belonging to the component whose root is root.
        if(! assigned.contains(u)) {

          // Get the current component set or if doesn't yet exists, create a new one
          HashSet<Node> component = components.getOrDefault(root, new HashSet<>());
          component.add(u);
          assigned.add(u);
          //System.out.println("add " + u + " to " + root);
          //System.out.println( root + " >> component is now: " + component );
          components.put(root, component ); // note that root/key is also added to the set itself during the first call

          // For each neighbour, call recursively
          for( Node n_ : g.getNeighbours(u) ) {
            assign(n_, root);
          }
        } // else do nothing
      }

      public void printResults() {

        System.out.println( String.format("The graph has %d strong component(s)", this.components.size() ) );
        //System.out.println( this.components );

        int round = 1;
        HashSet<Node> compKeys = new HashSet<>(this.components.keySet());
        while(!compKeys.isEmpty()) {

          Node hasSmallest  = null; // points to the KEY
          Node smallest     = null; // the actual smallest found

          for(Node ck : compKeys ) {
            Node localSmallest = Collections.min( components.get(ck) );
            if(smallest == null || localSmallest.number < smallest.number) {
              smallest = localSmallest;
              hasSmallest = ck; // update KEY (this one holds the smallest found node so far)
            }
          }
          // We should now know witch set contains the smallest Node
          // Remove it and output
          //System.out.println( "smallest was " + smallest );
          System.out.println( String.format("Component %d:%s", round++, this.sortAndPrintNodes(hasSmallest)) );
          compKeys.remove(hasSmallest);

        }

        /*
        int i = 1;
        for( Node root : components.keySet() ) {
          System.out.println( String.format("Component %d: %s", i, this.sortAndPrintNodes(root)) );
          i++;
        }*/

      }


      /**
       * Helper function for output. Gets nodes, sorts them and outputs
       * { <1>, <5>, <2> } -> String "1 2 5" for example
       */
      public String sortAndPrintNodes(final Node key) {
        HashSet<Node> nodes = components.getOrDefault(key, null);
        ArrayList<Node> nodes_  = new ArrayList<>( nodes );
        Collections.sort(nodes_);
        StringBuilder sb = new StringBuilder();
        for(Node n : nodes_) {
          sb.append( " " + n.number );
        }
        return sb.toString();
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






}
