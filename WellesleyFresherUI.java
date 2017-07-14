/* Dana Fein-Schaffer, Mina Hattori, and Vivian Zhang
 * CS230 Project 
 * 12/15/2016
 * WellesleyFresherUI.java
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.LinkedList;
import java.util.Vector;


 /****************************************************************** 
 * WellesleyFresherUI.java creates the GUI elements drives the WellesleyFresher.java program
 * ******************************************************************/

public class WellesleyFresherUI extends JPanel {
  
  
  WellesleyFresher program; // an instance of the WellesleyFresher.java program
  
  JTextField recipeName; // String recipeName will be taken from user input
  
  // Possible start locations to be used in drop down menu
  String[] locations = {"Lulu", "Emporium", "Tower", "Pom", "Bates", "Leaky", "StoneD", "A-Quad", "SCI", "S.D", "Clapp", "Tower Court", "Quad", "New Dorms"};
  JComboBox startLocation;
  
  JList all; // List of all compiled ingredients available at Wellesley
  JButton addIngredient; // Button to add ingredient from master list to personal shopping list
  
  DefaultListModel model; // List model to hold personal shopping list
  JList yourList; // JList to hold personal shopping list
  JButton deleteIngredient; // Button to delete ingredient from personal shopping list
  JButton deleteAllIngredients; // Button to clear personal shopping list
  
  JButton generatePlan; // Button to generate plan based on personal shopping list
  
  JTextArea shoppingPlan; // Display area for shopping plan
  
  
 /******************************************************************
    Constructor. Creates a WellesleyFresher program. 
  ******************************************************************/
  public WellesleyFresherUI(){
    //create a new WellesleyFresher program
    program = new WellesleyFresher();
    
    //create the layout for the GUI 
    setLayout(new BorderLayout(0, 0));
    
    //make new componenet and add it to designated place in the GUI 
    add(makeLeftPanel(), BorderLayout.CENTER);
    add(makeRightPanel(), BorderLayout.EAST);
    add(makeTopPanel(), BorderLayout.NORTH);
    
  }
  
   /******************************************************************
   * Method to create the top panel for GUI title 
   *  @return top panel of the border layout 
   *******************************************************************/
  private JPanel makeTopPanel(){
    //create a new JPanel and set layout to BoxLayout 
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
    
    //set background color to light blue 
    topPanel.setBackground(new Color(255, 255, 255));
    
    //create the label for the top of the GUI 
    JLabel programName = new JLabel("Wellesley Fresher: A Shopping Plan Generator", JLabel.LEADING);
    
    //add spaces and programName to topPanel 
    topPanel.add(Box.createRigidArea(new Dimension(20,20)));
    topPanel.add(programName);
    topPanel.add(Box.createRigidArea(new Dimension(0,20)));
    
    return topPanel;
    
  }
  
   /******************************************************************
   * Method to create the left panel for user options 
   *  @return left panel of the border layout 
   *******************************************************************/
  private JPanel makeLeftPanel(){
    
    //create a new JPanel, leftPanel, and set the layout to BoxLayout
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.LINE_AXIS));
    
    //create a new JPanel, wrapper, and set the layout to BoxLayout
    JPanel wrapper = new JPanel();
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
    
    //set the background color of the leftPanel and wrapper to light blue 
    leftPanel.setBackground(new Color(204, 255, 255));
    wrapper.setBackground(new Color(204, 255, 255));
    
    // Recipe name text label and text field
    JLabel recipeNameLabel = new JLabel("Your Recipe Name");
    
    recipeName = new JTextField();
    recipeName.setMaximumSize(new Dimension(10000, 100));
    
    // location dropdown menu
    JLabel startLocationLabel = new JLabel("Where are you?");
    
    startLocation = new JComboBox(locations);
    startLocation.setMaximumSize(new Dimension(10000, 100));
    
    // ingredients text field
    //label for ingredients list 
    JLabel ingredientsLabel = new JLabel("Choose Ingredients");
    
    //JPanel to hold the ingredients list 
    JPanel ingredientsHolder =  new JPanel();
    ingredientsHolder.setBackground(new Color(204, 255, 255));
    
    //create String Vector of ingredients and add it to new JList 
    //set the selction mode to single selection 
    Vector<String> allIngredients = program.getAllIngredients("All");
    all = new JList(allIngredients);
    all.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    //create a scrollpane that holds the JList, ingredientsList, and set the size
    JScrollPane scroller = new JScrollPane(all);
    scroller.setPreferredSize(new Dimension(300, 250));
    
    //add scroller to the JPanel 
    ingredientsHolder.add(scroller);
    
    // Create JButton to add selected ingredients to yourList
    addIngredient = new JButton("+ Add to your list");
    //Add ActionListener for addIngredient button and add button to JPanel
    addIngredient.addActionListener(new addToYourList());
    ingredientsHolder.add(addIngredient);
    
    // Create JLabel and JPanel for ingredients user selected  
    JLabel yourIngredientsLabel = new JLabel("Your Ingredients");
    JPanel yourIngredientsHolder =  new JPanel();
    yourIngredientsHolder.setBackground(new Color(204, 255, 255));
    
    //Creates new list to contain selected ingredients from the user 
    model = new DefaultListModel();
    yourList = new JList(model);
    yourList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    //Create JScrollPane for yourList and add to JPanel 
    JScrollPane yourScroller = new JScrollPane(yourList);
    yourScroller.setPreferredSize(new Dimension(300, 200));
    
    yourIngredientsHolder.add(yourScroller);
    
    //Create a new button for the user to delete ingredient from yourList 
    deleteIngredient = new JButton("- Delete from your list");
    //Add ActionListener for deleteIngredient button 
    deleteIngredient.addActionListener(new subtractFromYourList());
    yourIngredientsHolder.add(deleteIngredient);
    
    //Create a new button for the user to delete all ingredients from yourList
    deleteAllIngredients = new JButton("Clear list");
    //Add ActionListener for deleteIngredient button 
    deleteAllIngredients.addActionListener(new clearYourList());
    yourIngredientsHolder.add(deleteAllIngredients);
    
    //Create a button to make the shopping plan
    generatePlan = new JButton("Make Shopping Plan");
    //Add ActionListener for the MakeShoppingPlan button 
    generatePlan.addActionListener(new makeShoppingPlan());

    //Setting up the layout of leftPanel and wrapper 
    //Adds all of the components to the GUI 
    leftPanel.add(Box.createRigidArea(new Dimension(50,50)));
    leftPanel.add(wrapper);
    
    wrapper.add(Box.createRigidArea(new Dimension(20,20)));
    
    wrapper.add(recipeNameLabel);
    wrapper.add(recipeName);
    
    wrapper.add(Box.createRigidArea(new Dimension(0,10)));
    
    wrapper.add(startLocationLabel);
    wrapper.add(startLocation);
    
    wrapper.add(Box.createRigidArea(new Dimension(0,10)));
    
    wrapper.add(ingredientsLabel);
    wrapper.add(ingredientsHolder);
    
    wrapper.add(Box.createRigidArea(new Dimension(0,10)));
    
    wrapper.add(yourIngredientsLabel);
    wrapper.add(yourIngredientsHolder);
    
    wrapper.add(Box.createRigidArea(new Dimension(0,10)));
    wrapper.add(generatePlan);
    
    wrapper.add(Box.createRigidArea(new Dimension(20,20)));
    leftPanel.add(Box.createRigidArea(new Dimension(50,50)));
    
    return leftPanel;
  }
  
/******************************************************************
   * Method to create the right panel to display the shopping plan
   *  @return right panel of the border layout 
   *******************************************************************/
  private JPanel makeRightPanel(){
    //make right panel and set layout to BoxLayout 
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.LINE_AXIS));
    
    //create a wrapper panel and set layout to BoxLayout 
    JPanel wrapper = new JPanel();
    wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
    
    //Label for the shopping plan 
    JLabel shoppingPlanLabel = new JLabel("Your Shopping Plan");
    
    //Create a JTextArea for the generated shopping plan 
    shoppingPlan = new JTextArea("Nothing here yet!", 10,10);
    
    //Add components to rightPanel
    //Add to GUI 
    rightPanel.add(Box.createRigidArea(new Dimension(50,50)));
    rightPanel.add(wrapper);
    
    wrapper.add(Box.createRigidArea(new Dimension(0,20)));
    wrapper.add(shoppingPlanLabel);
    wrapper.add(shoppingPlan); 
    wrapper.add(Box.createRigidArea(new Dimension(0,20)));
    
    rightPanel.add(Box.createRigidArea(new Dimension(50,50)));
    rightPanel.setPreferredSize(new Dimension(400, 800));
    
    return rightPanel; 
  }
  
  /******************************************************************
   * Method to set the size of the program of the GUI window 
   *  @return dimension of the window  
   *******************************************************************/
  public Dimension getPreferredSize() {
    return new Dimension(900, 900);
  }
  
   /* Class addToYourList Implements ActionListener
   */ 
  public class addToYourList implements ActionListener{
    /******************************************************************
      * Method (event handler) to add ingredients from given ingredients list to user selected list
      * @param e - button click 
   *******************************************************************/
    public void actionPerformed(ActionEvent e){
      //Get user selected ingredient 
      String selected = (String) all.getSelectedValue();
      
      //if the ingredient is not already in the list, add it to the user's list 
      if (!model.contains(selected)){
        model.addElement(selected);
      }
    }
  }
  
  /* Class subtractFromYourList Implements ActionListener
   */ 
  public class subtractFromYourList implements ActionListener{
    /******************************************************************
      * Method (event handler) to delete ingredients from user selected list
      * @param e - button click 
    *******************************************************************/
    public void actionPerformed(ActionEvent e){
      //Get user selected ingredient and remove 
      String selected = (String) yourList.getSelectedValue();
      model.removeElement(selected);
    }
  }
  
  
  /* Class clearYourList Implements ActionListener
   */ 
  public class clearYourList implements ActionListener{
    /******************************************************************
      * Method (event handler) to clear user selected list
      * @param e - button click 
    *******************************************************************/
    public void actionPerformed(ActionEvent e){
      //Get user selected ingredient and remove 
      model.removeAllElements();
    }
  }
  
  /* Class makeShoppingPlan Implements ActionListener
   */ 
  public class makeShoppingPlan implements ActionListener{
     /******************************************************************
      * Method (event handler) to generate the shopping plan 
      * @param e - button click 
    *******************************************************************/
    public void actionPerformed(ActionEvent e){
      
      //Create Object[] from the model and get the recipe name 
      Object[] yourIngredients = model.toArray();
      String name = recipeName.getText();
      
      //Create a LinkedList for ingredients
      LinkedList<String> yourIngredientsInput = new LinkedList<String>();
      
      //Add ingredients from the array to the LinkedList 
      for (int i=0; i<yourIngredients.length; i++){
        yourIngredientsInput.add((String)yourIngredients[i]);
      }
      
      //Generate the shopping plan from program (WellesleyFresher) method (makeShoppingPlan) using the user's input 
      String shoppingPlanOutput = program.makeShoppingPlan(yourIngredientsInput, 
                                                           (String) startLocation.getSelectedItem(), name);
      
      //Display the text in the GUI 
      shoppingPlan.setText(shoppingPlanOutput);
      
    }
  }
  
   /******************************************************************
      * Driver method to create instantiate and run a new WellesleyFresherUI
    *******************************************************************/
  public static void main (String[] args) {
    // creates and shows a Frame 
    JFrame frame = new JFrame("Wellesley Fresher");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    //create a panel, and add it to the frame
    WellesleyFresherUI ui = new WellesleyFresherUI();
    frame.getContentPane().add(ui);
    
    frame.pack();
    frame.setVisible(true);
    
  }
  
}