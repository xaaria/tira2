import com.sun.deploy.util.ArrayUtil;
import sun.awt.image.ImageWatched;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {


  public static void main(String[] args) throws IOException {


    System.out.println("input1.txt");
    final String filename = "src/input1.txt"; //args[1]; // might crash but never mind

    Graph g = new Graph();

    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));

      // Read each line
      for (String line = br.readLine(); line != null; line = br.readLine()) {

        System.out.print(line + "\n");
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

    // Print graph structure
    System.out.println( g.toString() );
    System.out.println( g.isBipartite() );

  }



  static class Graph {

    Map<Node, HashSet<Node>> nodes; // has vertices to these nodes




    public Graph() {
      this.nodes = new HashMap<>();
    }

    public boolean hasNode(Node n) {
      return this.nodes.containsKey(n);
    }

    public void addToGraph(Node n) {
      if(!this.nodes.containsKey(n)) {
        this.nodes.put(n, new HashSet<>());
      } else {
        System.out.print("Key was already in a graph!\n");
      }
    }

    // Gets connected Nodes
    public Node[] getNeighbours(Node n) {
      return this.nodes.get(n).toArray(new Node[this.nodes.get(n).size()]);
    }



    public int getNumberOfComponents() {
      return 1;
    }




    // inner class
    class BipartiteGraph {

      private Graph g_;
      private final HashSet<Node> setOne = new HashSet<>(); // blue 0
      private final HashSet<Node> setTwo = new HashSet<>(); // white 1

      final String SET_ONE = "BLUE";
      final String SET_TWO = "WHITE";

      public BipartiteGraph(Graph g) {
        this.g_ = g;
      }

      public void printResults() {
        if(this.isBipartite()) {
          System.out.println("The graph has x component(s)");
          System.out.println("The graph is bipartite");
          System.out.println(String.format("%s: ", SET_ONE) );
          System.out.println(String.format("%s: ", SET_TWO) );
        }
      }


      // Note: does not clear sets
      public boolean isBipartite() {

        // Trivial check
        if(this.g_.nodes.size() <= 1) { return true; }

        Node[] nodes = this.g_.nodes.keySet().toArray(new Node[this.g_.nodes.size()]); // order not defined

        LinkedList<Node> q = new LinkedList<Node>();    // Queue of unchecked nodes
        HashSet<Node> discovered = new HashSet<Node>(); // Checked nodes

        int color = 0;

        // BEGIN:

        q.add(nodes[0]); // Add root node, random

        while(!q.isEmpty()) {

          Node tn = q.poll();
          discovered.add(tn);
          if( setOne.contains(tn) ) { color = 0; } else { color = 1; } // Get target node's color
          System.out.println( "Target Node: " + tn + " | color: " + color );

          if(color == 0) { setOne.add(tn); } else { setTwo.add(tn); } // add node itself to a colored set

          for(Node n_ : this.g_.getNeighbours(tn)) {
            if(!discovered.contains(n_)) {
              discovered.add(n_);
              q.add(n_); //

              if(color == 0) {
                setTwo.add(n_);
              } else {
                setOne.add(n_);
              }

              // If we now find a mismatch: return false
              if ((color == 0 && setOne.contains(n_)) || (color == 1 && setTwo.contains(n_))) {
                return false;
              }

            }
          }
          // End of all neighbours
          System.out.println( "--- Situation now: ---" );
          System.out.println( setOne.toString() );
          System.out.println( setTwo.toString() );
          System.out.println( "--- >> ---" );

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


  static class Node {

    int number; // identifier

    public Node(int n) {
      this.number = n;
    }

    @Override
    public String toString() {
      return String.format("<%d>", number);
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
