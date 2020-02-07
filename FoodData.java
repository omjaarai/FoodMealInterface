package application;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;

/**
 * This class represents the backend for managing all the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 * @author Leo
 * @author Sulong
 */
public class FoodData implements FoodDataADT<FoodItem> {

    // List of all the food items.
    private List<FoodItem> foodItemList;
    

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    // The list of BPTree, to get the code more efficient
    
    private List<BPTree<Double, FoodItem>> nutriTree;
    
    
    // Five Nutrition filter tree
    
    private BPTree<Double, FoodItem> calorieTree;
    private BPTree<Double, FoodItem> fatTree;
    private BPTree<Double, FoodItem> carbohyTree;
    private BPTree<Double, FoodItem> fiberTree;
    private BPTree<Double, FoodItem> proteinTree;
    
    // Nutrition name
    private String[] nutrition = new String[5];
    
    // File storage
    private File file;
    
    // Load file scanner
    private Scanner scnr;
    
    // Iterate filter string 
    private Iterator<String> itr;
    
    // Record for filter numbers
    private int filterCount;
    
    //branch factor for b+trees
    private int bFactor;
    

    /**
     * Public constructor
     */
    public FoodData() {
    	
        this.foodItemList = new ArrayList<>();
        this.indexes = new HashMap<>();
        
        this.bFactor = 3;
        
        // initiate tree list
        
        this.nutriTree = new ArrayList<>();
        
        
        	
        //initiate nutrition tree
        
        this.calorieTree = new BPTree<Double, FoodItem>(bFactor);
        this.fatTree = new BPTree<Double, FoodItem>(bFactor);
        this.carbohyTree = new BPTree<Double, FoodItem>(bFactor);
        this.fiberTree = new BPTree<Double, FoodItem>(bFactor);
        this.proteinTree = new BPTree<Double, FoodItem>(bFactor);
        
        
        // add nutrition tree to tree list
        
        nutriTree.add(0, calorieTree);
        nutriTree.add(1, fatTree);
        nutriTree.add(2, carbohyTree);
        nutriTree.add(3, fiberTree);
        nutriTree.add(4, proteinTree);
        
        
        // initiate nutrition name
        nutrition[0] = "calories";
    	nutrition[1] = "fat";
    	nutrition[2] = "carbohydrate";
    	nutrition[3] = "fiber";
    	nutrition[4] = "protein";
    	
    	int i = 0;
    	Iterator<BPTree<Double, FoodItem>> treeItr = nutriTree.iterator();
    	while(treeItr.hasNext()) {
    		indexes.put(nutrition[i], treeItr.next());
    		i ++;
    	}
    	

    }

    /**
     * Check the validation of file path, and then
     * read the file and convert the item to Food class
     * add the new instance to data list
     * @param filePath
     * 
     */
    @Override
    public void loadFoodItems(String filePath) {
        file = new File(filePath);
        
        // Check the file existence
        try {
            scnr = new Scanner(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.out.println("no such file");
        }
        
        // Read the file and save the food info to food class
        try {
            while (scnr.hasNextLine()) {
            	
                // read line by line
                String text = scnr.nextLine().trim();
                
                // if line is not empty
                
                // load the file to memory
                if (!text.isEmpty()) {
                	
                    String[] objects = text.split(",");
                    
                    //skip the data with wrong format
                    if(objects.length != 12) continue;
                    String id = objects[0];
                    String name = objects[1];
                    FoodItem food = new FoodItem(id, name);
                    for(int i = 0; i < 5; i++) {
                    	food.addNutrient(nutrition[i], Double.parseDouble(objects[2*(i+1)+1]));
                    }
                    foodItemList.add(food);
          
                }
            }
                
                // add the food item to nutrition BPTree
                
                Iterator<FoodItem> foodItr = foodItemList.iterator();
                while(foodItr.hasNext()) {
                	Iterator<BPTree<Double, FoodItem>> treeItr = nutriTree.iterator();
                	FoodItem tempFood = foodItr.next();
                	int i = 0;
                    while(treeItr.hasNext()) {
                    	treeItr.next().insert(tempFood.getNutrientValue(nutrition[i]), tempFood);
                    	i ++;
                    }
                }
                
            
            scnr.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error in food data processing");
        }
    }

    
    /*	DID NOT FINISH
     * (non-Javadoc)
     * 
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        if (foodItemList == null || foodItemList.size() == 0)
            return foodItemList;
        List<FoodItem> foodCandidate = new ArrayList<>();
        	for (FoodItem food : foodItemList) {
                if (food.getName().equals(substring)) {
                    foodCandidate.add(food);
                }
        	}
            return foodCandidate;
       
    }

    /*
     * (non-Javadoc)
     * 
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
    	try {
    		itr = rules.iterator();
    		filterCount = rules.size();
    		Iterator<FoodItem> itr2 = foodItemList.iterator();
    		
    		// the final list of nutrition filter
    		List<FoodItem> qualifiedFood = new ArrayList<>();
    		
    		// the temporary storage list
    		List<FoodItem> tempFoodList= new ArrayList<>();
    		
    		while(itr2.hasNext()) {
    			qualifiedFood.add(itr2.next());
    		}
    		
    		while(itr.hasNext()) {
    			
    			// seperate the filter string to the proper format
    			String[] filter = itr.next().split("\\s+");
    			
    			//Double.parseDouble(filter[2])
    			// search the corresponding food for the nutrition filter
    			
    			tempFoodList = indexes.get(filter[0]).rangeSearch(Double.parseDouble(filter[2]), filter[1]);
    			
    			// choose the intersection between new food list and existing food list
    			//qualifiedFood.retainAll(tempFoodList);
    			qualifiedFood.retainAll(tempFoodList);

    			//renew the filter index to prepare for next filter
//    			filterIndex ++;
    		};

    		return qualifiedFood;
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
        this.foodItemList.add(foodItem);
    	
        	Iterator<BPTree<Double, FoodItem>> treeItr = nutriTree.iterator();
        
        	int i = 0;
            while(treeItr.hasNext()) {
            	treeItr.next().insert(foodItem.getNutrientValue(nutrition[i]), foodItem);
            	i ++;
            }
        
        
    	

    }

    /*
     * (non-Javadoc)
     * 
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
        return this.foodItemList;

    }
    
    
    /**
     *  Output food to file
     */
    public void saveFoodItems(String filename) {
        File fileout = new File(filename);
        try {
            FileWriter output = new FileWriter(fileout);
            for (FoodItem food : foodItemList) {
                output.write(food.getName());
                output.write(",calories," + food.getNutrientValue("calories"));
                output.write(",carbohydrate," + food.getNutrientValue("carbohydrate"));
                output.write(",fat," + food.getNutrientValue("fat"));
                output.write(",fiber," + food.getNutrientValue("fiber"));
                output.write(",protein," + food.getNutrientValue("protein") + "\n");
            }
            output.close();
        } catch (IOException e) {
            System.out.println("Error in saving data to file");
        }
    }

}
