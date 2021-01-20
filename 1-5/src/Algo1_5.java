
/*
* Implement a program that reads a description of a directed graph from an input text
* file and prints out all strong components of the graph.
*
* */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

        System.out.print(line + "\n");
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
    System.out.println( g.toString() );

    // Graph is initialized
    Graph.KosarajusAlgorithm kosaraju = new Graph.KosarajusAlgorithm(g);
    kosaraju.getStrongComponents(); // run the algorithm
    kosaraju.printResults();


  }



}
