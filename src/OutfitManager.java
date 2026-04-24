import java.io.Serializable;
import java.util.ArrayList;

public class OutfitManager implements Serializable {
    private static final long serialVersionUID = 1L; 
    private ArrayList<Outfit> outfits;

    public OutfitManager() {
        outfits = new ArrayList<>();
    }

    public void saveOutfit(Outfit outfit)   { 
        outfits.add(outfit);    
    }
    public void deleteOutfit(Outfit outfit) { 
        outfits.remove(outfit); 
    }

    public ArrayList<Outfit> getAllOutfits() { 
    // Returning the whole arraylist of outfits
        return outfits; 
    }
// method to get the total number of outfits created and saved by the user
    public int getTotalOutfits() { 
        return outfits.size(); 
    }

    // Find outfits that contain a specific clothing item
    public ArrayList<Outfit> findOutfitsWith(ClothingItems item) {
        // Creating a new arraylist that will store outfits that contains the particular clothing item
        ArrayList<Outfit> result = new ArrayList<>();
        // enhanced for loop to loop through the outfits arraylist and check if any of the clothing items in the outfit matches the item we are looking for, if it does we add it to the result arraylist
        for (Outfit o : outfits) {
            if (o.getTop().equals(item) || o.getBottom().equals(item) || o.getShoes().equals(item)
                    || (o.getAccessories() != null && o.getAccessories().equals(item))
                    || (o.getOuterwear()   != null && o.getOuterwear().equals(item))) {
        // if the the outfit with particular clothing item is found, adding it to the result arraylist  
                result.add(o);
            }
        }
        // returning the arraylist of outfits that contains the particular clothing item
        return result;
    }

    // Rate an outfit (Made by AI)
    public void rateOutfit(Outfit outfit, int stars) {
        outfit.setRating(stars);
    }
}
