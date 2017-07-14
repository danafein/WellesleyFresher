/* Dana Fein-Schaffer, Mina Hattori, and Vivian Zhang
 * CS230 Project 
 * 12/15/2016
 * WellesleyFresher.java
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Hashtable;
import java.util.Vector;

 /****************************************************************** 
 * WellesleyFresher.java creates the program which traverses the campus
 * map to generate a shopping plan 
 * ******************************************************************/

public class WellesleyFresher {

  private AdjMatGraph<String> campus; // weighted graph represents campus locations and connections
  private Hashtable<String, Hashtable> diningHalls; // hashtable contains dining halls and their ingredients
  private Hashtable<String, Vector<String>> allIngredients; // hashtable aggregated ingredients by category
  
 /******************************************************************
   * Constructor. Creates a WellesleyFresher program that creates a 
   * new campus map and instantiates hashtable that holds all of the 
   * ingredients. 
   * 
   * @throws FileNotFoundException
  ******************************************************************/
  public WellesleyFresher(){
    
    // create campus map from file
     campus = new AdjMatGraph<String>();
     try {
       campus.loadTGF("CampusMap.tgf", campus);
     }
     catch (FileNotFoundException ex) {
       System.out.println(ex); 
     }
     
     // make hashtable for each dining hall
     diningHalls = new Hashtable<String, Hashtable>();
     diningHalls.put("Lulu", makeDiningHall("lulu.txt"));
     diningHalls.put("Emporium", makeDiningHall("emporium.txt"));
     diningHalls.put("Tower", makeDiningHall("tower.txt"));
     diningHalls.put("Pom", makeDiningHall("pom.txt"));
     diningHalls.put("Bates", makeDiningHall("bates.txt"));
     diningHalls.put("Leaky", makeDiningHall("leaky.txt"));
     diningHalls.put("StoneD", makeDiningHall("stoned.txt"));
     
     
     allIngredients = new Hashtable<String, Vector<String>>();
     allIngredients.put("All", makeCategory("all.txt"));

  }
  
  
 /******************************************************************
   * Method to get ingredients from given category (currently all ingredients
   * are in one category, but in the future expand the hashtable to orgnaize
   * ingredients into multiple categories)
   * 
   *  @param category - String of the category to get ingredients from
   *  @return  Vector of Strings of all of the ingredients in given category
  *******************************************************************/
  public Vector<String> getAllIngredients(String category){
    Vector<String> output = allIngredients.get(category);
    
    return output;
  }
  
   /******************************************************************
   * Method to read ingredients of a dining hall from .txt file and 
   * return hashtable of ingredients
   * 
   *  @param inFile - String of the file name to get ingredients from 
   *  @return Hashtable of ingredients 
   *  @throws IOException
  *******************************************************************/
  public static Hashtable<String, String> makeDiningHall(String inFile) {
    Hashtable<String, String> diningHall = new Hashtable<String, String>();
    
    try {
      Scanner reader = new Scanner(new File(inFile));
      
      while (reader.hasNextLine()){
        String ingredient = reader.nextLine().trim();
        diningHall.put(ingredient, ingredient);
      }
      reader.close();
      
    } catch (IOException e){
      System.out.println(e);
    }
    
    return diningHall;     
  }
  
    /******************************************************************
   * Method to read ingredients of a category from .txt file and 
   * return Vector of categories. Currently all our ingredients are
   * held in a single category, but can later be organized into seperate 
   * categories.
   * 
   *  @param inFile - String of the file name to get ingredients from 
   *  @return Vector of ingredients 
   *  @throws IOException
  *******************************************************************/
  public static Vector<String> makeCategory(String inFile) {
    Vector<String> ingredientCategory = new Vector<String>();
    
    try {
      Scanner reader = new Scanner(new File(inFile));
      
      while (reader.hasNextLine()){
        String ingredient = reader.nextLine().trim();
        ingredientCategory.add(ingredient);
      }
      reader.close();
      
    } catch (IOException e){
      System.out.println(e);
    }
    
    return ingredientCategory;     
  }
  
  /******************************************************************
   * Method to make the shopping plan from user inputs 
   * 
   *  @params ingredientsInput - LinkedList of ingredients from user, 
   *          startLocation  - String of the start location 
   *          recipeName - String of the recipe name 
   *  @return String of the shopping plan  
  *******************************************************************/
  public String makeShoppingPlan(LinkedList<String> ingredientsInput, String startLocation, String recipeName){
  
    // string to be outputted as shopping plan
    String shoppingPlan = "\n********************************\n";
      shoppingPlan += "SHOPPING PLAN for " + recipeName.toUpperCase() +"\n";
      shoppingPlan +="********************************\n\n";
    
    // keep track of visited dining halls
    LinkedList<String> visited = new LinkedList(); 
    
    // keep track of total time taken
    int totalTime = 0;
   
    // while all ingredients have not yet been found
    while (!ingredientsInput.isEmpty()){
      
      // find next closest dining hall and add it to visited
      String next = (String) campus.getClosest(startLocation, visited);
      visited.add(next);
   
      ListIterator<String> iter = ingredientsInput.listIterator(0);
      Hashtable<String, String> currentDiningHall = diningHalls.get(next);
    
      String foundInCurrentDiningHall = "";
      
      while (iter.hasNext()) {
        String currentIngredient = iter.next();
        
        if (currentDiningHall.contains(currentIngredient)){
          foundInCurrentDiningHall += currentIngredient + "\n";
          iter.remove();
        }
      }
      
      // if ingredients are found in that dining hall, add to shopping plan 
      if (foundInCurrentDiningHall != ""){
        totalTime += campus.getWeight(startLocation, next); // increment time counter
        
        // add to output string
        shoppingPlan += "- " + startLocation.toUpperCase() + " to " + next.toUpperCase() + " (" +campus.getWeight(startLocation, next) +  " min) -\n";
        shoppingPlan += foundInCurrentDiningHall;
        shoppingPlan += "\n";
        
        // reassign startLocation and continue searching for ingredients
        startLocation = next;
      }
      
      /* if no ingredients are found in the closest dining hall, the while loop will automatically search the second closest dining hall (and so on) 
       * since the closest dining hall has now been added to "visited" and startLocation is not updated. this continues until all ingredients are found
       */
      
    }
    
    shoppingPlan += "- TOTAL TIME -\n" + totalTime + " min\n\nHappy shopping!";
    
    return shoppingPlan;
  }
  
  
  /* Main method for testing*/
  public static void main(String[] args){
    
  WellesleyFresher test = new WellesleyFresher();
  
   LinkedList<String> visited = new LinkedList<String>();
   visited.add("Tower");
   visited.add("Pom");
   visited.add("Emporium");
   visited.add("Bates");
   visited.add("Leaky");
   visited.add("StoneD");
   
   LinkedList<String> ingredientsInput = new LinkedList<String>();
   ingredientsInput.add("Raw Eggs");
   ingredientsInput.add("Spinach");
   ingredientsInput.add("Mushrooms");
   ingredientsInput.add("Red Pepper Flakes");
   ingredientsInput.add("Dill");
   ingredientsInput.add("Zaatar");
   ingredientsInput.add("Cumin");
   ingredientsInput.add("Cracked Black Pepper");
   ingredientsInput.add("Greek Yogurt");
   ingredientsInput.add("Hershey Kisses");
   
   System.out.println(test.makeShoppingPlan(ingredientsInput, "Lulu", "Chocolatey Omelette"));
   
  }

}