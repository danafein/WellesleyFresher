/*   
 * Dana Fein-Schaffer, Mina Hattori, and Vivian Zhang
 * CS230 Project 
 * 12/15/2016
 * AdjMatGraph.java
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

 /****************************************************************** 
 * AdjMatGraph.java is adapted from Exam 3 starter code and changed to
 * accomodate a weighted graph. 
 * 
 * Includes new methods:
 * getSuccessors(T vertex, LinkedList<String> visited)
 * getSuccessorWeights(T vertex)
 * getClosest(T vertex)
 * getWeight(T srcVertex, T destVertex)
 * ******************************************************************/
public class AdjMatGraph<T> implements Graph<T>, Iterable<T> {
 public static final int NOT_FOUND = -1;
 private static final int DEFAULT_CAPACITY = 1; // Small so that we can test expand
 private static final boolean VERBOSE = false;  // print while reading TGF?

 private int n;   // number of vertices in the graph
 private int[][] arcs;   // adjacency matrix of arcs
 private T[] vertices;   // values of vertices

 /******************************************************************
    Constructor. Creates an empty graph.
  ******************************************************************/
 @SuppressWarnings("unchecked")
 public AdjMatGraph() {
  n = 0;
  this.arcs = new int[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
  this.vertices = (T[])(new Object[DEFAULT_CAPACITY]);
 }
 
 
 /********** NEW METHODS *******************************************/

 /**
  * Construct a copy (clone) of a given graph.
  * The new graph will have all the same vertices and arcs as the original.
  * A *shallow* copy is performed: the graph structure is copied, but
  * the new graph will refer to the exact same vertex objects as the original.
  */
 @SuppressWarnings("unchecked")
 public AdjMatGraph(AdjMatGraph<T> g) {
  n = g.n;
  vertices = (T[]) new Object[g.vertices.length];
  arcs = new int[g.arcs.length][g.arcs.length];
  for (int i = 0; i < n; i++) {
   vertices[i] = g.vertices[i];
   for (int j = 0; j < n; j++) {
    arcs[i][j] = g.arcs[i][j];
   }
  }
 }

 /******************************************************************
  * Load vertices and edges from a TGF file into a given graph.
  * @param tgfFile - name of the TGF file to read
  * @param g - graph to which vertices and arcs will be added.
  *            g must be empty to start!
  * @throws FileNotFoundException 
  *****************************************************************/
 public static void loadTGF(String tgf_file_name, AdjMatGraph<String> g) throws FileNotFoundException {
  if (!g.isEmpty()) throw new RuntimeException("Refusing to load TGF data into non-empty graph.");
  Scanner fileReader = new Scanner(new File(tgf_file_name));
  // Keep a mapping from TGF vertex ID to AdjMatGraph vertex ID.
  // This allows vertex IDs to be written out of order in TGF.
  // It also supports non-integer vertex IDs.
  HashMap<String,Integer> vidMap = new HashMap<String,Integer>();
  try {
   // Read vertices until #
   while (fileReader.hasNext()) {
    // Get TGF vertex ID
    String nextToken = fileReader.next();
    if (nextToken.equals("#")) {
     break;
    }
    vidMap.put(nextToken, g.n());
    String label = fileReader.hasNextLine() ? fileReader.nextLine().trim() : fileReader.next();
    if (VERBOSE) {
     System.out.println("Adding vertex " + g.n() + " (" + nextToken + " = \"" + label + "\")");
    }
    g.addVertex(label);
   }

   // Read edges until EOF
   while (fileReader.hasNext()) {
    // Get src and dest
    String src = fileReader.next();
    String dest = fileReader.next();
    String weightS = fileReader.next();
    int weight = Integer.parseInt(weightS);
    // Discard label if any
//    if (fileReader.hasNextLine()) {
//     String label = fileReader.nextLine().trim();
//     if (!label.isEmpty()) System.out.println("Discarded arc label: \"" + label + "\"");
//    }

    if (VERBOSE) {
     System.out.println(
       "Adding arc "
         + vidMap.get(src)  + " (" + src  + " = \"" + g.getVertex(vidMap.get(src))  + "\") --> "
         + vidMap.get(dest) + " (" + dest + " = \"" + g.getVertex(vidMap.get(dest)) + "\")"
       );
    }
    g.addArc(vidMap.get(src), vidMap.get(dest), weight);
   }
  } catch (RuntimeException e) {
   System.out.println("Error reading TGF");
   throw e;
  } finally {
   fileReader.close();
  }

 }
 
 /**
  * An iterator that iterates over the vertices of an AdjMatGraph.
  */
 private class VerticesIterator implements Iterator<T> {
  private int cursor = 0;
  
  /** Check if the iterator has a next vertex */
  public boolean hasNext() {
   return cursor < n;
  }

  /** Get the next vertex. */
  public T next() {
   if (cursor >= n) {
    throw new NoSuchElementException();
   } else {
    return vertices[cursor++];
   }
  }

  /** Remove is not supported in this iterator. */
  public void remove() {
   throw new UnsupportedOperationException();
  } 
 }
 
 /**
  * Create a new iterator that will iterate over the vertices of the array when asked.
  * @return the new iterator.
  */
 public Iterator<T> iterator() {
  return new VerticesIterator();
 }
 
 /**
  * Check if the graph contains the given vertex.
  */
 public boolean containsVertex(T vertex) {
  return getIndex(vertex) != NOT_FOUND;
 }
 
 
 
 
 /**** FAMILIAR METHODS ********************************************/
 
 
 

 /******************************************************************
    Returns true if the graph is empty and false otherwise. 
  ******************************************************************/
 public boolean isEmpty() {
  return n == 0;
 }

 /******************************************************************
    Returns the number of vertices in the graph.
  ******************************************************************/
 public int n() {
  return n;
 }

 /******************************************************************
    Returns the number of arcs in the graph by counting them.
  ******************************************************************/
 public int m() {
  int total = 0;

  for (int i = 0; i < n; i++) {
   for (int j = 0; j < n; j++) {
    if (arcs[i][j] > 0) {
     total++;
    }
   }
  }
  return total; 
 }

 /******************************************************************
    Returns true iff a directed edge exists from v1 to v2.
  ******************************************************************/
 public boolean isArc(T srcVertex, T destVertex) {
  int src = getIndex(srcVertex);
  int dest = getIndex(destVertex);
  return src != NOT_FOUND && dest != NOT_FOUND && arcs[src][dest]>0;
 }


 /******************************************************************
    Returns true iff an arc exists between two given indices. 
    @throws IllegalArgumentException if either index is invalid.
  ******************************************************************/
 protected boolean isArc(int srcIndex, int destIndex) {
  if (!indexIsValid(srcIndex) || !indexIsValid(destIndex)) {
   throw new IllegalArgumentException("One or more invalid indices: " + srcIndex + ", " + destIndex);
  }
  return arcs[srcIndex][destIndex]>0;
 }


 /******************************************************************
    Returns true iff an edge exists between two given vertices
    which means that two corresponding arcs exist in the graph.
  ******************************************************************/
 public boolean isEdge(T srcVertex, T destVertex) {
  int src = getIndex(srcVertex);
  int dest = getIndex(destVertex);
  return src != NOT_FOUND && dest != NOT_FOUND && isArc(src, dest) && isArc(dest, src);
 }
 
 /******************************************************************
    **********NEW METHOD*********** 
    Returns the integer weight between two vertices (will only 
    call when already establish destVertex is a successor of srcVertex)
    
    @param srcVertex - start vertex 
    @param destVertex - destination vertex
    
    @return the weight of arc 
  ******************************************************************/
 public int getWeight(T srcVertex, T destVertex){
   int src = getIndex(srcVertex);
   int dest = getIndex(destVertex);
   return arcs[src][dest];
 }


 /******************************************************************
    Returns true IFF the graph is undirected, that is, for every 
    pair of nodes i,j for which there is an arc, the opposite arc
    is also present in the graph.  
  ******************************************************************/
 public boolean isUndirected() {
  for (int i = 1; i < n(); i++) {
   // optimize to avoid checking pairs twice.
   for (int j = 0; j < i; j++) {
    if (arcs[i][j] != arcs[j][i]) {
     return false;
    }
   }
  }
  return true;
 }


 /******************************************************************
    Adds a vertex to the graph, expanding the capacity of the graph
    if necessary.  If the vertex already exists, it does not add it again.
  ******************************************************************/
 public void addVertex (T vertex) {
  if (getIndex(vertex) != NOT_FOUND) return;
  if (n == vertices.length) {
   expandCapacity();
  }

  vertices[n] = vertex;
  for (int i = 0; i <= n; i++) {
//   if (arcs[n][i] || arcs[i][n]) throw new RuntimeException("Corrupted AdjacencyMatrix");
   arcs[n][i] = -1;
   arcs[i][n] = -1;
  }      
  n++;
 }

 /******************************************************************
    Helper. Creates new arrays to store the contents of the graph 
    with twice the capacity.
  ******************************************************************/
 @SuppressWarnings("unchecked")
 private void expandCapacity() {
  T[] largerVertices = (T[])(new Object[vertices.length*2]);
  int[][] largerAdjMatrix = 
    new int[vertices.length*2][vertices.length*2];

  for (int i = 0; i < n; i++) {
   for (int j = 0; j < n; j++) {
    largerAdjMatrix[i][j] = arcs[i][j];
   }
   largerVertices[i] = vertices[i];
  }

  vertices = largerVertices;
  arcs = largerAdjMatrix;
 }


 /******************************************************************
    Removes a single vertex with the given value from the graph.  
    Uses equals() for testing equality.
  ******************************************************************/
 public void removeVertex (T vertex) {
  int index = getIndex(vertex);
  if (index != NOT_FOUND) {
   removeVertex(index);
  }
 }

 /******************************************************************
    Helper. Removes a vertex at the given index from the graph.   
    Note that this may affect the index values of other vertices.
    @throws IllegalArgumentException if the index is invalid.
  ******************************************************************/
 protected void removeVertex (int index) {
  if (!indexIsValid(index)) {
   throw new IllegalArgumentException("No such vertex index");
  }
  n--;

  // Remove vertex.
  for (int i = index; i < n; i++) {
   vertices[i] = vertices[i+1];
  }

  // Move rows up.
  for (int i = index; i < n; i++) {
   for (int j = 0; j <= n; j++) {
    arcs[i][j] = arcs[i+1][j];
   }
  }

  // Move columns left
  for (int i = index; i < n; i++) {
   for (int j = 0; j < n; j++) {
    arcs[j][i] = arcs[j][i+1];
   }
  }
  
  // Erase last row and last column
  for (int a = 0; a < n; a++) {
   arcs[n][a] = -1;
   arcs[a][n] = -1;
  }
 }

 /******************************************************************
    Inserts an edge between two vertices of the graph.
    If one or both vertices do not exist, ignores the addition.
  ******************************************************************/
 public void addEdge(T vertex1, T vertex2, int weight) {
  int index1 = getIndex(vertex1);
  int index2 = getIndex(vertex2);
  if (index1 != NOT_FOUND && index2 != NOT_FOUND) {
   addArc(index1, index2, weight);
   addArc(index2, index1, weight);
  }
 }

 /******************************************************************
    Inserts an arc from srcVertex to destVertex.
    If the vertices exist, else does not change the graph. 
  ******************************************************************/
 public void addArc(T srcVertex, T destVertex, int weight) {
  int src = getIndex(srcVertex);
  int dest = getIndex(destVertex);
  if (src != NOT_FOUND && dest != NOT_FOUND) {
   addArc(src, dest, weight);
  }
 }

 /******************************************************************
    Helper. Inserts an edge between two vertices of the graph.
    @throws IllegalArgumentException if either index is invalid.
  ******************************************************************/
 protected void addArc(int srcIndex, int destIndex, int weight) {
  if (!indexIsValid(srcIndex) || !indexIsValid(destIndex)) {
   throw new IllegalArgumentException("One or more invalid indices: " + srcIndex + ", " + destIndex);
  }
  arcs[srcIndex][destIndex] = weight;
 }


 /******************************************************************
    Removes an edge between two vertices of the graph.
    If one or both vertices do not exist, ignores the removal.
  ******************************************************************/
 public void removeEdge(T vertex1, T vertex2) {
  int index1 = getIndex(vertex1);
  int index2 = getIndex(vertex2);
  if (index1 != NOT_FOUND && index2 != NOT_FOUND) {
   removeArc(index1, index2);
   removeArc(index2, index1);
  }
 }


 /******************************************************************
    Removes an arc from vertex src to vertex dest,
    if the vertices exist, else does not change the graph. 
  ******************************************************************/
 public void removeArc(T srcVertex, T destVertex) {
  int src = getIndex(srcVertex);
  int dest = getIndex(destVertex);
  if (src != NOT_FOUND && dest != NOT_FOUND) {
   removeArc(src, dest);
  }
 }

 /******************************************************************
    Helper. Removes an arc from index v1 to index v2.
    @throws IllegalArgumentException if either index is invalid.
  ******************************************************************/
 protected void removeArc(int srcIndex, int destIndex) {
  if (!indexIsValid(srcIndex) || !indexIsValid(destIndex)) {
   throw new IllegalArgumentException("One or more invalid indices: " + srcIndex + ", " + destIndex);
  }
  arcs[srcIndex][destIndex] = -1;
 }



 /******************************************************************
    Returns the index value of the first occurrence of the vertex.
    Returns NOT_FOUND if the key is not found.
  ******************************************************************/
 protected int getIndex(T vertex) {
  for (int i = 0; i < n; i++) {
   if (vertices[i].equals(vertex)) {
    return i;
   }
  }
  return NOT_FOUND;
 }

 /******************************************************************
    Returns the vertex object that is at a certain index
  ******************************************************************/
 protected T getVertex(int v) {
  if (!indexIsValid(v)) {
   throw new IllegalArgumentException("No such vertex index: " + v);
  }
  return vertices[v]; 
 }

 /******************************************************************
     Returns true if the given index is valid. 
  ******************************************************************/
 protected boolean indexIsValid(int index) {
  return index < n && index >= 0;  
 }

 /******************************************************************
    Retrieve from a graph the vertices x pointing to vertex v (x->v)
    and returns them onto a linked list
  ******************************************************************/
 public LinkedList<T> getPredecessors(T vertex) {
  LinkedList<T> neighbors = new LinkedList<T>();

  int v = getIndex(vertex); 

  if (v == NOT_FOUND) return neighbors;
  for (int i = 0; i < n; i++) {
   if (arcs[i][v]>0) {
    neighbors.add(getVertex(i)); // if T then add i to linked list
   }
  }    
  return neighbors;    
 }

 
 public LinkedList<T> getSuccessors(T vertex){
  LinkedList<T> neighbors = new LinkedList<T>();

  int v = getIndex(vertex); 

  if (v == NOT_FOUND) return neighbors;
  for (int i = 0; i < n; i++) {
   if (arcs[v][i]>0) {
    neighbors.add(getVertex(i)); // if T then add i to linked list
   }
  }    
  return neighbors;    
 }
 
 /******************************************************************
  * Retrieve from a graph the vertices x following vertex v (v->x)
    and returns them onto a linked list
  ******************************************************************/
 
  /******************************************************************
              **********NEW METHOD***********
  * Retrieve from a graph the vertices x following vertex v (v->x)
    and returns them onto a linked list if the vertex has not already 
    been visited
    @param vertex - starting vertex
    @param visited - LinkedList of visited verticies 
    
    @return LinkedList of successors 
  ******************************************************************/
 public LinkedList<T> getSuccessors(T vertex, LinkedList<String> visited){
  LinkedList<T> neighbors = new LinkedList<T>();

  int v = getIndex(vertex); 

  if (v == NOT_FOUND) return neighbors;
  for (int i = 0; i < n; i++) {
   if (arcs[v][i]>0 && !visited.contains(getVertex(i))) {
    neighbors.add(getVertex(i)); // if T then add i to linked list
   }
  }    
  return neighbors;    
 }
 
  /******************************************************************
                *************NEW METHOD***************
  * helper method to getClosest: 
  * finds the list of neighbor weights corresponding to getSuccessors
     
    @param:  vertex - staring point 
    @param: visited -linked list of visted vertices 
    
    @return LinkedList of weights of arcs 
  ******************************************************************/
 public LinkedList<Integer> getSuccessorWeights(T vertex, LinkedList<String> visited){
  LinkedList<Integer> neighborWeights = new LinkedList<Integer>();
  LinkedList<T> neighbors = getSuccessors(vertex, visited); // get list of successors that have not yet been visited
  
  Iterator<T> iter = neighbors.iterator();
  while (iter.hasNext()){
    // get the weights and place in neighborWeights
    neighborWeights.add(getWeight(vertex, iter.next()));
  }
    
  return neighborWeights;    
 }

   /******************************************************************
                *************NEW METHOD***************
    Method to get the closest unvisted dining hall from a given vertex
     
    @param: vertex - staring point 
    @param: visited -linked list of visted vertices 
    
    @return name of the closest dining hall 
  ******************************************************************/
 public String getClosest(T vertex,LinkedList<String> visited){
   
   LinkedList<T> neighbors = getSuccessors(vertex, visited); 
   
   // if no more neighbors exist, return null
   if (neighbors.isEmpty()){
     return null;
   }
   
   //LinkedList to store the weights of the neighbors of the given vertex
   LinkedList<Integer> neighborWeights = getSuccessorWeights(vertex, visited);
   
   //keeps track of index of current minimum weight
   int minIndex = 0;
   int min = neighborWeights.get(minIndex);
   
   //loops through neighbors to check for closest dining hall
   for (int i=1; i<neighborWeights.size(); i++){
     if (neighborWeights.get(i) < min){
       min = neighborWeights.get(i);
       minIndex = i;
     }
     
   }
   
   return (String) neighbors.get(minIndex);
 }
 /******************************************************************
    Returns a string representation of the graph. 
  ******************************************************************/
 public String toString() {
  if (n == 0) {
   return "Graph is empty";
  }

  String result = "";

  //result += "\nArcs\n";
  //result += "---------\n";
  result += "\ni ";

  for (int i = 0; i < n; i++) {
   result += "" + getVertex(i);
   if (i < 10) {
    result += " ";
   }
  }
  result += "\n";

  for (int i = 0; i < n; i++) {
   result += "" + getVertex(i) + " ";

   for (int j = 0; j < n; j++) {
    if (arcs[i][j]>0) {
      //fix this so it adds weight instead of 1
     result += arcs[i][j] + " ";
    } else {
     result += "- "; //just empty space
    }
   }
   result += "\n";
  }

  return result;
 }


 /******************************************************************
  * Saves the current graph into a .tgf file.
  * If it cannot save the file, a message is printed. 
  *****************************************************************/
 public void saveTGF(String tgf_file_name) {
  try {
   PrintWriter writer = new PrintWriter(new File(tgf_file_name));

   //prints vertices by iterating through array "vertices"
   for (int i = 0; i < n(); i++) {
    if (vertices[i] == null){
     break;
    } else {
     writer.print((i+1) + " " + vertices[i]);
     writer.println("");
    }
   }
   writer.print("#"); // Prepare to print the edges
   writer.println("");

   //prints arcs by iterating through 2D array
   for (int i = 0; i < n(); i++) {
    for (int j = 0; j < n(); j++) {
     if (arcs[i][j]>0) {
      writer.print((i+1) + " " + (j+1));
      writer.println("");
     }
    }
   }
   writer.close();
  } catch (IOException ex) {
   System.out.println("***(T)ERROR*** The file could nt be written: " + ex);
  }
 }
 
 /** Testing Driver for AdjMatGraph.  This will not help you test AdjMatGraphPlus. */
 public static void main (String args[]) throws FileNotFoundException {
   //testing by reading in the campus map file
   AdjMatGraph<String> g = new AdjMatGraph<String>();
   try {
     loadTGF("CampusMap.tgf", g);
   }
   catch (FileNotFoundException ex) {
    System.out.println(ex); 
   }
   
   System.out.println("Campus Map: \n" + g);
   
   LinkedList<String> visited = new LinkedList<String>();
   visited.add("Tower");
   visited.add("Pom");
   visited.add("Emporium");
   visited.add("Bates");
   visited.add("Leaky");
   visited.add("StoneD");
   
   System.out.println(g.getSuccessors("Lulu", visited));
   System.out.println(g.getSuccessorWeights("Lulu", visited));
   System.out.println(g.getClosest("Lulu", visited));
 }

}
