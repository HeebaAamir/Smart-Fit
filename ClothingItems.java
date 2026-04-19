// Importing the serializable interface to implement file handling using serialization that would allow us to reload the saved files.
import java.io.Serializable;

// Making an abstract class ClothingItems that is to serve as the blueprint for other classes.(Abstraction).
public abstract class ClothingItems implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String size;
    private String brand;
    private int price;
    private String fabric;
    private String season;
    private String colour;
    private int WearCount;
    private String ImagePath = null;

    public ClothingItems(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath){
        this.size = size;
        this.brand = brand;
        this.price = price;
        this.fabric = fabric;
        this.colour = colour;
        this.WearCount = WearCount;
        this.season = season;
        // Introducing a variable ImagePath to store the paths of images of clothing items input by user.
        this.ImagePath = ImagePath;
    }

    public void setSize(String size){
        this.size = size;
    }
    public String getSize(){
        return size;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }
    public String getBrand(){
        return brand;
    }
    public void setPrice(int price){
        this.price = price;
    }
    public int getPrice(){
        return price;
    }
    public void setColour(String colour) { 
        this.colour = colour; 
    }
    public String getColour() { 
        return colour; 
    }
    public void setWearCount(int WearCount) { 
        this.WearCount = WearCount; 
    }
    public int getWearCount() { 
        return WearCount; 
    }
    public void incrementWear() { 
        WearCount++; 
    }
    public void setFabric(String fabric) { 
        this.fabric = fabric; 
    }
    public String getFabric() { 
        return fabric; 
    }
    public void setSeason(String season) { 
        this.season = season; 
    }
    public String getSeason() { 
        return season; 
    } 
    public void setImagePath(String ImagePath){
        this.ImagePath = ImagePath;
    }
    public String getImagePath(){
        return ImagePath;
    }
    //Abstract method that'll will be overriden in subclasses.(Polymorphism)
    public abstract String getItemType();
}

class top extends ClothingItems{
    String style;
    String sleeveType;

    public top(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath, String style, String sleeveType){
        super(size, brand, price, fabric, colour, WearCount, season, ImagePath);
        this.style = style;
        this.sleeveType = sleeveType;
    }
    // The class top is inheriting the getItemType method from the class ClothingItems and using it accordingly.
    // Same will be repeated for other classes as well.
    @Override
    public String getItemType() {
    return "Top";
    }
}

class bottom extends ClothingItems{
    String style;

    public bottom(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath, String style){
        super(size, brand, price, fabric, colour, WearCount,season,ImagePath);
        this.style = style;
    }
    @Override
    public String getItemType() {
    return "Bottom";
    }
}

class shoes extends ClothingItems{
    String style;
    int heelSize;
    boolean openFront;
    
    public shoes(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath, String style, int heelSize, boolean openFront){
        super(size, brand, price, fabric, colour, WearCount,season,ImagePath);
        this.style = style;
        this.heelSize = heelSize;
        this.openFront = openFront;
    }
    @Override
    public String getItemType() {
    return "Shoe";
    }
}

class accessories extends ClothingItems{
    String type;

    public accessories(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath, String type){
        super(size, brand, price, fabric, colour, season, WearCount,ImagePath);
        this.type = type;
    }
    @Override
    public String getItemType() {
    return "Accessory";
    }
}       

class outerwear extends ClothingItems{
    String style;

    public outerwear(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String ImagePath, String style){
        super(size, brand, price, fabric, colour, season, WearCount,ImagePath);
        this.style = style;
        this.style = style;
    }
    @Override
    public String getItemType() {
    return "OuterWear";
    }
}   
