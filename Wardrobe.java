// using Array list to have variable number of items in the wardrobe that can change upon user's discretion
import java.util.ArrayList;

public class Wardrobe {
    private ArrayList<ClothingItems>items;

    public Wardrobe() {
        items = new ArrayList<>();
    }
    
    // Creating method to view and get items
    // ClothingItems item indicate that the method will take any object of the type ClotingItems as all subclasses extend from it.
    public void viewItems(){
        for (ClothingItems item : items ){
            System.out.println(items);
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
}
