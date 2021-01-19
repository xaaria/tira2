import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
* Data structures and alg. 2
* ex 1.4
* OP - 2021
* ---
* Your program is called with one command line parameter: the graph input filename.
* ...where both u and v are non-negative integers.
* Each line means that the graph contains an undirected edge between the nodes u and v. The nodes are not listed separately;
* the list of nodes can be inferred from the list of edges (the graph contains only nodes that are attached to at least one edge).
* Note that there is no guarantee that the graph is connected (= that each node can be reached from each other node).
* The whole graph is bipartite if all of its separate components are bipartite. Therefore if the graph consists of
* several separate components, you need to check each component separately. This is easy to do: keep track of which
* nodes have not yet been processed (visited). If you first started the bipartiteness
* check from some node p and at least some node q is still unprocessed after the check, nodes p and q are in different
* component and you need to check the component of q separately (= redo the check but this time starting from q).
* Repeat until no unprocessed nodes remain.
*
* Output description
* Your program should first print out one line of form "The graph has x component(s)",
* where x tells the number of separate connected components in the graph.
* The next output line should be either "The graph is bipartite" or "The graph is not bipartite"
* depending on whether the graph was found to be bipartite or not. If the graph is bipartite, the program prints out the
* two different node sets of the graph as described below.
* In order to make the program output unique (and hence easier for WETO to check),
* the program must compose and output the two node sets of a bipartite graph in the following manner.
* We call the overall resulting two node sets BLUE and WHITE.
* ---
* */
public class Algo1_4 {



  public static void main(String[] args) throws IOException {

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

        Node n1 = new Node(Integer.parseInt(n[0]));
        Node n2 = new Node(Integer.parseInt(n[1]));

        g.addToGraph(n1);
        g.addToGraph(n2);

        // Add edges
        g.nodes.get(n1).add(n2);
        g.nodes.get(n2).add(n1);

      } // end of read

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // Run
    g.isBipartite();


  }



  static class Graph {

    Map<Node, HashSet<Node>> nodes; // has vertices to these nodes

    public Graph() {
      this.nodes = new HashMap<>();
    }

    public void addToGraph(Node n) {
      if(!this.nodes.containsKey(n)) {
        this.nodes.put(n, new HashSet<>());
      }
    }

    // Gets connected Nodes
    public Node[] getNeighbours(Node n) {
      return this.nodes.get(n).toArray(new Node[this.nodes.get(n).size()]);
    }

    public boolean isBipartite() {
      BipartiteGraph bg = new BipartiteGraph(this);
      bg.run();
      bg.printResults();
      return bg.isBipartite;
    }


    static class BipartiteGraph {

      private Graph g_;
      private int components;
      public boolean isBipartite = false;

      private final LinkedList<Node> unchecked = new LinkedList<>();
      private final HashSet<Node> setOne = new HashSet<>(); // blue 0
      private final HashSet<Node> setTwo = new HashSet<>(); // white 1

      final String SET_ONE = "BLUE";
      final String SET_TWO = "WHITE";

      public BipartiteGraph(Graph g) {
        this.g_ = g;
        this.components = 0;
      }

      public void run() {
        isBipartite = this.isGraphBipartite();
      }

      // This is the main function
      private boolean printResults() {

        // Blue set contains the smallest node
        ArrayList<Node> blue  = new ArrayList<>( setOne );
        ArrayList<Node> white = new ArrayList<>( setTwo );
        Collections.sort(blue);
        Collections.sort(white);


        if(isBipartite) {
          System.out.println(String.format("The graph has %d component(s)", this.components));
          System.out.println("The graph is bipartite");

          System.out.print(String.format("%s:", SET_ONE));
          for( Node n : blue ) {
            System.out.print( " " + n.number );
          }
          System.out.print("\n");
          System.out.print(String.format("%s:", SET_TWO));
          for( Node n : white ) {
            System.out.print( " " + n.number );
          }
          System.out.print("\n");

        } else {
          System.out.println(String.format("The graph has %d component(s)", this.components));
          System.out.println("The graph is not bipartite");
        }

        return isBipartite;
      }


      /**
       * Check if entire Graph is bipartite
       * @return boolean
       */
      private boolean isGraphBipartite() {

        // Store all nodes as unchecked here and pick 1. as a root node
        // When in isBipartite(), remove all nodes that are accessed at least once
        // Every time we return from it AND we still have unchecked nodes in entire graph
        // we know that there are in separate sub-graphs
        // then continue. raise components+1.
        // If any isBipartite() returns false, the entire graph can't be bipartite
        this.unchecked.addAll( this.g_.nodes.keySet() );

        Node root = unchecked.poll();
        while(root != null) {
          this.components++;
          if(!isBipartite(root)) {
            return false; // if any fails, entire graph is not bipartite
          }
          root = unchecked.poll();
        }
        return true;

      }

      /**
       * Checks if graph is bipartite from given root Node.
       */
      private boolean isBipartite(Node root) {

        // Trivial check
        if(this.g_.nodes.size() <= 1) { return true; }

        LinkedList<Node> q = new LinkedList<>();    // Queue of unchecked nodes
        HashSet<Node> discovered = new HashSet<>(); // Checked nodes

        // Local temp. sets
        HashSet<Node> setOne_ = new HashSet<>();
        HashSet<Node> setTwo_ = new HashSet<>();

        int color = 0;

        // BEGIN:

        q.add(root);        // Add root node
        setOne_.add(root);  // Root is always color 0

        while(!q.isEmpty()) {

          Node tn = q.poll();
          discovered.add(tn);         // local discovered
          this.unchecked.remove(tn);  // global

          if( setOne_.contains(tn) ) { color = 0; } else { color = 1; } // Get target node's color
          //System.out.println( "Target Node: " + tn + " | color: " + color );

          if(color == 0) { setOne_.add(tn); } else { setTwo_.add(tn); } // add node itself to a colored set

          for(Node n_ : this.g_.getNeighbours(tn)) {
            if(!discovered.contains(n_)) {
              discovered.add(n_);
              this.unchecked.remove(n_);
              q.add(n_); //
            }

            // If we now find a mismatch: (sub) graph is not bipartite -> return false
            if ((color == 0 && setOne_.contains(n_)) || (color == 1 && setTwo_.contains(n_))) {
              return false;
            }
            if(color == 0) {
              //System.out.println("Add " + n_ + " to set color 1");
              setTwo_.add(n_);
            } else {
              //System.out.println("Add " + n_ + " to color 0");
              setOne_.add(n_);
            }


          }

          // End of all neighbours
          /*System.out.println( "--- Situation now: ---" );
          System.out.println( setOne.toString() );
          System.out.println( setTwo.toString() );
          System.out.println( "--- >> ---" );
          */
        }

        // Every node was checked and no same color neighbours was found
        // Then merge local sets : Set that has smallest node ID, will be set one/blue
        // Blue set contains the smallest node
        // switch if white has blue has bigger fist value [note: why doesn't the compare work without .number?]
        if( Collections.min(setOne_).number > Collections.min(setTwo_).number ) {
          this.setOne.addAll( setTwo_ );
          this.setTwo.addAll( setOne_ );
        } else {
          this.setOne.addAll( setOne_ );
          this.setTwo.addAll( setTwo_ );
        }

        return true;

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

    // https://stackoverflow.com/a/14505443/8094012 :D
    public int hashCode() {
      return 0;
    }

  }



}
