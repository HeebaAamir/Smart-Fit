// using Array list to have variable number of items in the wardrobe that can change upon user's discretion
import java.util.ArrayList;
import java.io.Serializable;

public class Wardrobe {
private static final long serialVersionUID = 1L; 
    
    private ArrayList<ClothingItems>items;

    public Wardrobe() {
        items = new ArrayList<>();
    }
    
    // Creating method to view and get items
    // ClothingItems item indicate that the method will take any object of the type ClotingItems as all subclasses extend from it.
    public void viewItems(){
        for (ClothingItems item : items ){
            System.out.println(item.getItemType());
        }
    }

    public void getItem(){}

    // using built-in functions of Array list to add or remove items (objects)
    public void addItems(ClothingItems item){
        items.add(item);
    }

    public void removeItems(ClothingItems item){
        items.remove(item);
    }

// Creating methods to get specific type of clothing items
    // which will be then displayed in different sections of the GUI
    
    public ArrayList<top> getTops() {
    // Creating a new arraylist that will store tops
    ArrayList<top> result = new ArrayList<>();
    for (ClothingItems item : items) {
    // Using the instanceof operator to check if the item is of type top
    // If it is, we implicitly cast it from type clothing items to type top
    //And add it to the result arraylist that stores only tops
        if (item instanceof top) result.add((top) item);
    }
    return result;
}

//Repeating the same process for other clothing items

public ArrayList<bottom> getBottoms() {
    ArrayList<bottom> result = new ArrayList<>();
    for (ClothingItems item : items) {
        if (item instanceof bottom) result.add((bottom) item);
    }
    return result;
}

public ArrayList<shoes> getShoes() {
    ArrayList<shoes> result = new ArrayList<>();
    for (ClothingItems item : items) {
        if (item instanceof shoes) result.add((shoes) item);
    }
    return result;
}

public ArrayList<outerwear> getOuterwear() {
    ArrayList<outerwear> result = new ArrayList<>();
    for (ClothingItems item : items) {
        if (item instanceof outerwear) result.add((outerwear) item);
    }
    return result;
}

public ArrayList<accessories> getAccessories() {
    ArrayList<accessories> result = new ArrayList<>();
    for (ClothingItems item : items) {
        if (item instanceof accessories) result.add((accessories) item);
    }
    return result;
}
}

