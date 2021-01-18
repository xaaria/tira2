import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Algo1_4 {



  public static void main(String[] args) throws IOException {

    if(args.length == 0) {
      System.out.print("Filename is missing!");
      return;
    }

    final String filename = "src/" + args[0];
    //final String filename = args[0]; // For WETO testing system

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



    // inner class
    static class BipartiteGraph {

      // TODO: add clear method that clears the state

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
        ArrayList<Node> blue = new ArrayList<>( setOne );
        ArrayList<Node> white = new ArrayList<>( setTwo );
        Collections.sort(blue);
        Collections.sort(white);

        // switch if white has blue has bigger fist value
        if(blue.get(0).number > white.get(0).number) {
          ArrayList<Node> tmp = blue;
          blue = white;
          white = tmp;
        }

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

        int color = 1;

        // BEGIN:

        q.add(root);      // Add root node
        setOne.add(root); // Root is always color 0

        while(!q.isEmpty()) {

          Node tn = q.poll();
          discovered.add(tn);         // local discovered
          this.unchecked.remove(tn);  // global

          if( setOne.contains(tn) ) { color = 0; } else { color = 1; } // Get target node's color
          System.out.println( "Target Node: " + tn + " | color: " + color );

          if(color == 0) { setOne.add(tn); } else { setTwo.add(tn); } // add node itself to a colored set

          for(Node n_ : this.g_.getNeighbours(tn)) {
            if(!discovered.contains(n_)) {
              discovered.add(n_);
              this.unchecked.remove(n_);
              q.add(n_); //
            }

            // If we now find a mismatch: (sub) graph is not bipartite -> return false
            if ((color == 0 && setOne.contains(n_)) || (color == 1 && setTwo.contains(n_))) {
              return false;
            }
            if(color == 0) {
              System.out.println("Add " + n_ + " to set color 1");
              setTwo.add(n_);
            } else {
              System.out.println("Add " + n_ + " to color 0");
              setOne.add(n_);
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
      return this.number - ((Node) n).number;
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
