import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
