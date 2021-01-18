import com.sun.deploy.util.ArrayUtil;
import sun.awt.image.ImageWatched;

import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {


  public static void main(String[] args) throws IOException {

    if(args.length == 0) {
      return;
    }

    final String filename = "src/" + args[0];

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
    //System.out.println( g.toString() );
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


    public boolean isBipartite() {
      BipartiteGraph bg = new BipartiteGraph(this);
      boolean r = bg.isBipartite();
      bg.printResults();
      return r;
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

        if(this.isBipartite()) {
          System.out.println(String.format("The graph has %d component(s)", getNumberOfComponents()));
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
        }
      }



      public boolean isBipartite() {

        // Trivial check
        if(this.g_.nodes.size() <= 1) { return true; }

        setOne.clear(); setTwo.clear();

        Node[] nodes = this.g_.nodes.keySet().toArray(new Node[this.g_.nodes.size()]); // order not defined

        LinkedList<Node> q = new LinkedList<>();    // Queue of unchecked nodes
        HashSet<Node> discovered = new HashSet<>(); // Checked nodes

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
