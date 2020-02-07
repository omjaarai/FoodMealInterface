/**
 * Filename:   MyPQ.java
 * Project:    Food Query and Meal Analyze
 * Version:    1.0
 * Date:       Dec 12, 2018
 * Authors:    Sulong Zhou - szhou78@wisc.edu
 *             Zhennan Wu - zwu347@wisc.edu
 *             Yimiao Cao - cao223@wisc.edu
 *             Omjaa Rai - orai@wisc.edu
 *             Ethan Root - eroot2@wisc.edu
 *
 * Semester:   Fall 2018
 * Course:     CS400
 * Instructor: Deppeler (deppeler@cs.wisc.edu)
 * Credits:    
 * Bugs:       no known bugs
 *
 */
package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;

public class Main extends Application {
    
    // private field for food data and any other UI objects 
    private File file;
    private Label foodCounter = new Label();
    private TextField foodName = new TextField();
    private FoodData data = new FoodData();
    private ObservableList<FoodItem> foodData;
    private ObservableList<FoodItem> foodData2;
    private ObservableList<FoodItem> mealData;
    private FilteredList<FoodItem> filteredData;
    private SortedList<FoodItem> sortedData;
    private FileChooser fileChooser = new FileChooser();
    private TableView foodTable = new TableView();
    private TableView mealTable = new TableView();
    private VBox vbox6;
    
    // Initial total nutrients
    double totalFat = 0;
    double totalCarbohydrates = 0;
    double totalProtein = 0;
    double totalCalories = 0;
    double totalFiber = 0;
    // Initial nutrient
    double fat = 0;
    double Carbohydrate = 0;
    double Fiber = 0;
    double Protein = 0;
    double totalWeight = 0;
    
    private String[] tempRules = new String[15];
    private int filterNumber = 0;
    
    /*
     * (non-Javadoc) initialize a stage and run the program 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            
            // load from and save to a default csv if user dosen't specify 
            data.loadFoodItems("foodItems.csv");
            data.saveFoodItems("savedFood.csv");
            
            // food, meal and filtered data
            foodData = FXCollections.observableArrayList(data.getAllFoodItems());
            mealData = FXCollections.observableArrayList(new ArrayList<FoodItem>());
            filteredData = new FilteredList<>(foodData, p -> true);
            sortedData = new SortedList<>(filteredData);

            // set title of app
            primaryStage.setTitle("Food & Meal");
            
            // layout 
            BorderPane border = new BorderPane();
            border.setTop(addFood());
            border.setLeft(addFoodList(primaryStage, foodName));
            border.setCenter(addSlider());
            border.setRight(addMealList());
            // scene
            Scene scene = new Scene(border, 1000, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Filter for calories 
     * @return a HBox contains calories filter
     */
    private HBox addCaloriesRange() {
        // min textfield for calories 
        TextField minCalories = new TextField();
        minCalories.setPromptText("min");
        minCalories.setPrefWidth(58);
        // when user inputs min value
        minCalories.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "calories";
                String comparator = ">=";
                String value = minCalories.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[0] = ruleBuilder;
                filterNumber++;
            }
        });
        
        // max textfield for calories 
        TextField maxCalories = new TextField();
        maxCalories.setPromptText("max");
        maxCalories.setPrefWidth(58);
        // when user inputs max value
        maxCalories.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "calories";
                String comparator = "<=";
                String value = maxCalories.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[1] = ruleBuilder;
                filterNumber++;
            }
        });
        
        // equal textfield for calories 
        TextField equalCalories = new TextField();
        equalCalories.setPromptText("equal");
        equalCalories.setPrefWidth(58);
        
        equalCalories.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "calories";
                String comparator = "==";
                String value = equalCalories.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[2] = ruleBuilder;
                filterNumber++;
            }
        });
           
        Label notEqual = new Label("<=");
        notEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label isEqual = new Label("==");
        isEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.getChildren().addAll(minCalories, notEqual, maxCalories, isEqual, equalCalories);
        return hbox;
    }
    
    /**
     * Filter for fat 
     * @return a HBox contains fat filter
     */
    private HBox addFatRange() {

        TextField minFat = new TextField();
        minFat.setPromptText("min");
        minFat.setPrefWidth(58);

        minFat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fat";
                String comparator = ">=";
                String value = minFat.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[3] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField maxFat = new TextField();
        maxFat.setPromptText("max");
        maxFat.setPrefWidth(58);

        maxFat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fat";
                String comparator = "<=";
                String value = maxFat.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[4] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField equalFat = new TextField();
        equalFat.setPromptText("equal");
        equalFat.setPrefWidth(58);

        equalFat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fat";
                String comparator = "==";
                String value = equalFat.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[5] = ruleBuilder;
                filterNumber++;
            }
        });

        Label notEqual = new Label("<=");
        notEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label isEqual = new Label("==");
        isEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.getChildren().addAll(minFat, notEqual, maxFat, isEqual, equalFat);
        return hbox;
    }
    
    /**
     * Filter for carboHydrate 
     * @return a HBox contains fat filter
     */
    private HBox addCarbohyRange() {

        TextField minCarbohy = new TextField();
        minCarbohy.setPromptText("min");
        minCarbohy.setPrefWidth(58);

        minCarbohy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "carbohydrate";
                String comparator = ">=";
                String value = minCarbohy.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[6] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField maxCarbohy = new TextField();
        maxCarbohy.setPromptText("max");
        maxCarbohy.setPrefWidth(58);

        maxCarbohy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "carbohydrate";
                String comparator = "<=";
                String value = maxCarbohy.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[7] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField equalCarbohy = new TextField();
        equalCarbohy.setPromptText("equal");
        equalCarbohy.setPrefWidth(58);

        equalCarbohy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "carbohydrate";
                String comparator = "==";
                String value = equalCarbohy.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[8] = ruleBuilder;
                filterNumber++;
            }
        });

        Label notEqual = new Label("<=");
        notEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label isEqual = new Label("==");
        isEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.getChildren().addAll(minCarbohy, notEqual, maxCarbohy, isEqual, equalCarbohy);
        return hbox;
    }

    /**
     * Filter for fiber
     * @return a HBox contains fiber filter
     */
    private HBox addFiberRange() {

        TextField minFiber = new TextField();
        minFiber.setPromptText("min");
        minFiber.setPrefWidth(58);

        minFiber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fiber";
                String comparator = ">=";
                String value = minFiber.getText();
                if (value != "") {
                    String ruleBuilder = Stream.of(nutriName, comparator, value)
                                    .collect(Collectors.joining(" "));
                    tempRules[9] = ruleBuilder;
                    filterNumber++;
                }
            }
        });

        TextField maxFiber = new TextField();
        maxFiber.setPromptText("max");
        maxFiber.setPrefWidth(58);

        maxFiber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fiber";
                String comparator = "<=";
                String value = maxFiber.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[10] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField equalFiber = new TextField();
        equalFiber.setPromptText("equal");
        equalFiber.setPrefWidth(58);

        equalFiber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "fiber";
                String comparator = "==";
                String value = equalFiber.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[11] = ruleBuilder;
                filterNumber++;
            }
        });

        Label notEqual = new Label("<=");
        notEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label isEqual = new Label("==");
        isEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.getChildren().addAll(minFiber, notEqual, maxFiber, isEqual, equalFiber);
        return hbox;
    }

    /**
     * Protein for carboHydrate 
     * @return a HBox contains protein filter
     */
     
    private HBox addProteinRange() {

        TextField minProtein = new TextField();
        minProtein.setPromptText("min");
        minProtein.setPrefWidth(58);

        minProtein.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "protein";
                String comparator = ">=";
                String value = minProtein.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[12] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField maxProtein = new TextField();
        maxProtein.setPromptText("max");
        maxProtein.setPrefWidth(58);

        maxProtein.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "protein";
                String comparator = "<=";
                String value = maxProtein.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[13] = ruleBuilder;
                filterNumber++;
            }
        });

        TextField equalProtein = new TextField();
        equalProtein.setPromptText("equal");
        equalProtein.setPrefWidth(58);

        equalProtein.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent enter) {
                String nutriName = "Protein";
                String comparator = "==";
                String value = equalProtein.getText();
                String ruleBuilder = Stream.of(nutriName, comparator, value)
                                .collect(Collectors.joining(" "));
                tempRules[14] = ruleBuilder;
                filterNumber++;
            }
        });

        Label notEqual = new Label("<=");
        notEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label isEqual = new Label("==");
        isEqual.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.getChildren().addAll(minProtein, notEqual, maxProtein, isEqual, equalProtein);
        return hbox;
    }
    
    /**
     * returns a vBox containing all filter elements  
     * @return
     */
    private VBox addSlider() {
        foodName.setPromptText("search by name");
        search(foodName);

        Label nameLabel = new Label("Name");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label calLabel = new Label("Calories ");
        calLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label fatLabel = new Label("Fat ");
        fatLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label hdrLabel = new Label("CarbonHydrate ");
        hdrLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label fbrLabel = new Label("Fiber ");
        fbrLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label prtLabel = new Label("Protein ");
        prtLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        HBox hbox0 = new HBox();
        hbox0.setSpacing(5);
        hbox0.setPadding(new Insets(5, 5, 0, 5));
        hbox0.getChildren().addAll(nameLabel, foodName);
        hbox0.setAlignment(Pos.CENTER);
        HBox hbox1 = new HBox();
        hbox1.setSpacing(5);
        hbox1.setPadding(new Insets(5, 5, 0, 5));
        hbox1.getChildren().addAll(calLabel, addCaloriesRange());
        hbox1.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox();
        hbox2.setSpacing(5);
        hbox2.setPadding(new Insets(5, 5, 0, 5));
        hbox2.getChildren().addAll(fatLabel, addFatRange());
        hbox2.setAlignment(Pos.CENTER);
        HBox hbox3 = new HBox();
        hbox3.setSpacing(5);
        hbox3.setPadding(new Insets(5, 5, 0, 5));
        hbox3.getChildren().addAll(hdrLabel, addCarbohyRange());
        hbox3.setAlignment(Pos.CENTER);
        HBox hbox4 = new HBox();
        hbox4.setSpacing(5);
        hbox4.setPadding(new Insets(5, 5, 0, 5));
        hbox4.getChildren().addAll(fbrLabel, addFiberRange());
        hbox4.setAlignment(Pos.CENTER);
        HBox hbox5 = new HBox();
        hbox5.setSpacing(5);
        hbox5.setPadding(new Insets(5, 5, 0, 5));
        hbox5.getChildren().addAll(prtLabel, addProteinRange());
        hbox5.setAlignment(Pos.CENTER);
        HBox hbox7 = new HBox();
        Button button8 = new Button("Apply Filter");
        Button button9 = new Button("Clear Filter");
        hbox7.setSpacing(5);
        hbox7.setPadding(new Insets(5, 5, 0, 5));
        hbox7.getChildren().addAll(button8, button9);
        hbox7.setAlignment(Pos.CENTER);

        Label instLabel =
                        new Label("Add food ONLY after Clear Filter; Only input positive numbers");
        Label instLabel2 =
                        new Label("Press ENTER each time to save value; Then press Apply Filter");
        instLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        instLabel2.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        VBox vbox8 = new VBox();
        vbox8.setSpacing(1);
        vbox8.setPadding(new Insets(5, 5, 0, 5));
        vbox8.getChildren().addAll(instLabel, instLabel2);
        vbox8.setAlignment(Pos.CENTER);

        button8.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent click) {

                List<String> finalRules = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    if (tempRules[i] != null) {
                        finalRules.add(tempRules[i]);
                    }
                }
                List<FoodItem> list1 = new ArrayList<>();
                list1 = data.filterByNutrients(finalRules);
                foodData2 = FXCollections.observableArrayList(data.filterByNutrients(finalRules));
                System.out.println(list1);
                filteredData = new FilteredList<>(foodData2, p -> true);
                sortedData = new SortedList<>(filteredData);
                foodCounter.textProperty()
                                .bind(Bindings.size(filteredData).asString("Record count: %s"));

                search(foodName);
            }
        });

        vbox6 = new VBox();
        vbox6.setSpacing(5);
        vbox6.setPadding(new Insets(5, 5, 5, 5));

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5, 5, 5, 5));

        vbox.getChildren().addAll(hbox0, vbox8, hbox1, hbox2, hbox3, hbox4, hbox5, hbox7, vbox6);

        button9.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent click) {
                for (int i = 0; i < 15; i++) {
                    tempRules[i] = null;
                }
                for (int i = 0; i < 5; i = i + 2) {
                    addCaloriesRange().getChildren().set(i, new TextField());
                    addFatRange().getChildren().set(i, new TextField());
                    addCarbohyRange().getChildren().set(i, new TextField());
                    addFiberRange().getChildren().set(i, new TextField());
                    addProteinRange().getChildren().set(i, new TextField());
                }
                TextField name1 = new TextField();
                hbox0.getChildren().set(1, name1);
                hbox1.getChildren().setAll(calLabel, addCaloriesRange());
                hbox2.getChildren().setAll(fatLabel, addFatRange());
                hbox3.getChildren().setAll(hdrLabel, addCarbohyRange());
                hbox4.getChildren().setAll(fbrLabel, addFiberRange());
                hbox5.getChildren().setAll(prtLabel, addProteinRange());
                vbox.getChildren().setAll(hbox0, vbox8, hbox1, hbox2, hbox3, hbox4, hbox5, hbox7,
                                vbox6);

                filteredData = new FilteredList<>(foodData, p -> true);

                sortedData = new SortedList<>(filteredData);

                search(name1);

                foodCounter.textProperty()
                                .bind(Bindings.size(filteredData).asString("Record count: %s"));
            }
        });

        return vbox;
    }

    /**
     * returns Hbox for top of gui that handles adding food
     * @return
     */
    private HBox addFood() {
        TextField ID = new TextField();
        ID.setPromptText("ID");

        TextField name = new TextField();
        name.setPromptText("name");

        TextField calorie = new TextField();
        calorie.setPromptText("calorie");

        TextField fat = new TextField();
        fat.setPromptText("fat");

        TextField carboHydr = new TextField();

        carboHydr.setPromptText("carbonhydrate");

        TextField fiber = new TextField();
        fiber.setPromptText("fiber");

        TextField protein = new TextField();
        protein.setPromptText("protein");

        Button addFood = new Button("Add Food");
        addFood.setPrefSize(100, 20);
        calorie.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
                if (!newValue.matches("\\d*")) {
                    calorie.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        carboHydr.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
                if (!newValue.matches("\\d*")) {
                    carboHydr.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        fat.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
                if (!newValue.matches("\\d*")) {
                    fat.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        fiber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
                if (!newValue.matches("\\d*")) {
                    fiber.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        protein.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                            String newValue) {
                if (!newValue.matches("\\d*")) {
                    protein.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        addFood.setOnAction(e -> {
            if (!(ID.getText() == null || ID.getText().isEmpty())) {
                FoodItem food = new FoodItem(ID.getText(), name.getText());
                if (isDouble(calorie.getText())) {
                    food.addNutrient("calories", Double.parseDouble(calorie.getText()));
                }
                if (isDouble(fat.getText())) {
                    food.addNutrient("fat", Double.parseDouble(fat.getText()));
                }
                if (isDouble(carboHydr.getText())) {
                    food.addNutrient("carbohydrate", Double.parseDouble(carboHydr.getText()));
                }
                if (isDouble(fiber.getText())) {
                    food.addNutrient("fiber", Double.parseDouble(fiber.getText()));
                }
                if (isDouble(protein.getText())) {
                    food.addNutrient("protein", Double.parseDouble(protein.getText()));
                }

                data.addFoodItem(food);
                foodData.add(food);
                foodData = FXCollections.observableArrayList(data.getAllFoodItems());
            }
        });


        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.getChildren().addAll(ID, name, calorie, fat, carboHydr, fiber, protein, addFood);
        return hbox;

    }

    /**
     * helper method to check proper inputs
     * @param str
     * @return
     */
    private static boolean isDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        int i = 0;
        for (; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9' || c == '.') {
                return false;
            }
        }
        return true;
    }

    /**
     * returns analysis chart
     * @return
     */
    private DoughnutChart addPie() {
        ObservableList<PieChart.Data> pieChartData = createData();
        DoughnutChart chart = new DoughnutChart(pieChartData);
        return chart;
    }

    /**
     * returns main food list
     * @param primaryStage
     * @param foodName
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private VBox addFoodList(Stage primaryStage, TextField foodName) {

        // create a menu
        Menu m = new Menu("File");

        // create menu items
        MenuItem loadFile = new MenuItem("Load File");
                
        loadFile.setOnAction(e -> {
            // Extension filter
            data = new FoodData();
            foodData.clear();
            mealData.clear();
            filteredData.clear();
            sortedData.clear();
            foodCounter.textProperty()
            .bind(Bindings.size(filteredData).asString("Record count: %s"));
            FileChooser.ExtensionFilter extensionFilter =
                            new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setSelectedExtensionFilter(extensionFilter);
            file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                data.loadFoodItems(file.getName());
                foodData = FXCollections.observableArrayList(data.getAllFoodItems());
                mealData = FXCollections.observableArrayList(new ArrayList<FoodItem>());
                filteredData = new FilteredList<>(foodData, p -> true);
                sortedData = new SortedList<>(filteredData);
                search(foodName);
                foodCounter.textProperty()
                                .bind(Bindings.size(filteredData).asString("Record count: %s"));
            }
        });

        MenuItem saveFile = new MenuItem("Save File");
        saveFile.setOnAction(new EventHandler<ActionEvent>(){          
            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fileChooser = new FileChooser();
                //Extension filter
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setSelectedExtensionFilter(extensionFilter);
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file !=null) {
                    data.saveFoodItems(file.getName());
                    
                }
            }
        }); 

        // add menu items to menu
        m.getItems().add(loadFile);
        m.getItems().add(saveFile);

        // create a menu bar
        MenuBar mb = new MenuBar();
        // add menu to menu bar
        mb.getMenus().add(m);

        HBox hbox = new HBox();
        hbox.setSpacing(15);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10, 10, 5, 10));

        Label label = new Label("Food List");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hbox.getChildren().addAll(label, mb);

        foodCounter.textProperty().bind(Bindings.size(filteredData).asString("Record count: %s"));


        foodTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FoodItem, String> nameCol = new TableColumn("Name");

        TableColumn<FoodItem, CheckBox> selectCol = new TableColumn("Add");
        selectCol.setMaxWidth(300);

        foodTable.getColumns().addAll(nameCol, selectCol);
        foodTable.setItems(foodData);

        foodTable.setOnMouseClicked(e -> {

            Label details = new Label("Name & Nutrients");
            details.setFont(Font.font("Arial", FontWeight.BOLD, 12));

            ListView<String> list = new ListView<String>();
            VBox infor = new VBox();
            infor.setAlignment(Pos.CENTER);
            infor.setSpacing(5);
            infor.setPadding(new Insets(10, 10, 10, 10));
            infor.getChildren().addAll(details, list);
            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().addAll(infor);

            Scene secondScene = new Scene(secondaryLayout, 300, 200);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Food Information");
            newWindow.setScene(secondScene);

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);
            for (int i = 0; i < foodTable.getItems().size(); i++) {
                if (foodTable.getSelectionModel().isSelected(i)) {
                    newWindow.show();
                    TablePosition pos = (TablePosition) foodTable.getSelectionModel()
                                    .getSelectedCells().get(0);

                    // Item here is the table view type:
                    Object item = foodTable.getItems().get(pos.getRow());

                    TableColumn col = pos.getTableColumn();

                    // this gives the value in the selected cell:
                    String name = (String) col.getCellObservableValue(item).getValue();
                    String[] nutrients = new String[5];
                    for (FoodItem food : foodData) {
                        if (food.getName().equals(name)) {
                            nutrients[0] = String.valueOf(food.getNutrientValue("calories"));
                            nutrients[1] = String.valueOf(food.getNutrientValue("fat"));
                            nutrients[2] = String.valueOf(food.getNutrientValue("carbohydrate"));
                            nutrients[3] = String.valueOf(food.getNutrientValue("fiber"));
                            nutrients[4] = String.valueOf(food.getNutrientValue("protein"));
                        }
                    }
                    ObservableList<String> menu = FXCollections.observableArrayList(name,
                                    "Calories: " + nutrients[0], "Fat: " + nutrients[1],
                                    "Carbohydrate: " + nutrients[2], "Fiber: " + nutrients[3],
                                    "Protein: " + nutrients[4]);
                    list.setItems(menu);
                }
            }


        });

        nameCol.setCellValueFactory(new PropertyValueFactory<FoodItem, String>("name"));

        selectCol.setMinWidth(20);
        selectCol.setCellValueFactory(new PropertyValueFactory<FoodItem, CheckBox>("selection"));

        Button foodButton = new Button("Add Selected Food to Meal List");

        foodButton.setOnAction(e -> addFoodToMeal());

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 5, 10));
        vbox.getChildren().addAll(hbox, foodCounter, foodTable, foodButton);
        return vbox;
    }
    
    /**
     * returns meal list table for right of screen
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private VBox addMealList() {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10, 10, 5, 10));
        Label label = new Label("Meal List");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));


        Button button = new Button("Analyze Meal");

        button.setOnAction(e -> mealAnalysis());

        hbox.getChildren().addAll(label, button);
        hbox.setSpacing(15);

        Label counter = new Label();
        counter.textProperty().bind(Bindings.size(mealData).asString("Record count: %s"));

        mealTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FoodItem, String> nameCol = new TableColumn("Name");
        TableColumn<FoodItem, CheckBox> selectCol = new TableColumn("Check");

        mealTable.getColumns().addAll(nameCol, selectCol);

        nameCol.setCellValueFactory(new PropertyValueFactory<FoodItem, String>("name"));

        selectCol.setCellValueFactory(new PropertyValueFactory<FoodItem, CheckBox>("selection"));

        Button mealButton = new Button("Remove Selected Food from Meal List");
        mealButton.setOnAction(e -> deleteSelectedRow());

        Button clearMealButton = new Button("Clear List");
        clearMealButton.setOnAction(e -> clearAllMealRows());

        // buttons
        HBox hbox2 = new HBox();
        hbox2.setSpacing(5);
        hbox2.setPadding(new Insets(0, 10, 0, 10));
        hbox2.getChildren().addAll(mealButton, clearMealButton);
        hbox2.setAlignment(Pos.CENTER);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10, 10, 5, 10));

        vbox.getChildren().addAll(hbox, counter, mealTable, hbox2);

        return vbox;
    }
    
    /**
     * adds selected food item from food list to meal list
     */
    @SuppressWarnings("unchecked")
    private void addFoodToMeal() {

        for (int i = 0; i < foodTable.getItems().size(); i++) {

            if (filteredData.get(i).getSelection().isSelected()) {
                mealData.add(new FoodItem(filteredData.get(i).getID(),
                                filteredData.get(i).getName()));
            }
        }
        mealTable.setItems(mealData);
    }
    
    /**
     * deletes all selected food items from meal list
     */
    private void deleteSelectedRow() {
        for (int i = mealData.size() - 1; i >= 0; i--) {
            if (mealData.get(i).getSelection().isSelected()) {
                mealData.remove(i);
            }
        }
        mealTable.setItems(mealData);
    }

    /**
     * clears and restart meal stats
     */
    private void clear() {
        totalCalories = 0;
        totalFat = 0;
        totalCarbohydrates = 0;
        totalFiber = 0;
        totalProtein = 0;
    }

    /**
     * analyzes current meal
     */
    private void mealAnalysis() {
        clear();
        if (mealData.size() == 0)
            return;
        for (int i = mealData.size() - 1; i >= 0; i--) {
            if (mealData.get(i).getSelection().isSelected()) {
                totalCalories += foodData.get(i).getNutrientValue("calories");
                totalFat += foodData.get(i).getNutrientValue("fat");
                totalCarbohydrates += foodData.get(i).getNutrientValue("carbohydrate");
                totalFiber += foodData.get(i).getNutrientValue("fiber");
                totalProtein += foodData.get(i).getNutrientValue("protein");
                totalWeight = totalFat + totalCarbohydrates + totalFiber + totalProtein;
                fat = Math.round(totalFat / totalWeight * 100);
                Carbohydrate = Math.round(totalCarbohydrates / totalWeight * 100);
                Fiber = Math.round(totalFiber / totalWeight * 100);
                Protein = Math.round(totalProtein / totalWeight * 100);
            }
        }

        Label label2 = new Label("Total Calories : " + totalCalories + " C");
        vbox6.getChildren().clear();
        vbox6.getChildren().addAll(label2, addPie());
        vbox6.setAlignment(Pos.TOP_CENTER);

    }

    /**
     * create the data to be used by our pi chart
     * @return
     */
    private ObservableList<PieChart.Data> createData() {
        return FXCollections.observableArrayList(
                        new PieChart.Data("Fat" + " " + fat + "%" + "(" + totalFat + "g)", fat),
                        new PieChart.Data("Carbonhydrate" + " " + Carbohydrate + "%" + "("
                                        + totalCarbohydrates + "g)", Carbohydrate),
                        new PieChart.Data("Fiber" + " " + Fiber + "%" + "(" + totalFiber + "g)",
                                        Fiber),
                        new PieChart.Data(
                                        "Protein" + " " + Protein + "%" + "(" + totalProtein + "g)",
                                        Protein));


    }

    /**
     * filters food items by a user input string
     * @param name
     */
    private void search(TextField name) {

        // Set the filter Predicate whenever the filter changes.
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(food -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (food.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches by name.
                }
                return false; // Does not match.
            });
        });

        // Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(foodTable.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        foodTable.setItems(sortedData);
    }

    /**
     * clears meal list
     */
    private void clearAllMealRows() {
        for (int i = mealData.size() - 1; i >= 0; i--) {
            mealData.remove(i);
        }
        mealTable.setItems(mealData);
    }


    /**
     *  Main method to run the program  
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}
