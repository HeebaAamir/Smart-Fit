public class ClothingItems {
    private String size;
    private String brand;
    private int price;
    String fabric;
    String season;
    String colour;
    int WearCount;

    public ClothingItems(String size, String brand, int price, String fabric, String colour, int WearCount){
        this.size = size;
        this.brand = brand;
        this.price = price;
        this.fabric = fabric;
        this.colour = colour;
        this.WearCount = WearCount;
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
    
}

class top extends ClothingItems{
    String style;
    String sleeveType;

    public top(String size, String brand, int price, String fabric, String colour, int WearCount, String style, String sleeveType){
        super(size, brand, price, fabric, colour, WearCount);
        this.style = style;
        this.sleeveType = sleeveType;
    }
}

class bottom extends ClothingItems{
    String style;

    public bottom(String size, String brand, int price, String fabric, String colour, int WearCount, String style){
        super(size, brand, price, fabric, colour, WearCount);
        this.style = style;
    }
}

class shoes extends ClothingItems{
    String style;
    int heelSize;
    boolean openFront;
    
    public shoes(String size, String brand, int price, String fabric, String colour, int WearCount, String style, int heelSize, boolean openFront){
        super(size, brand, price, fabric, colour, WearCount);
        this.style = style;
        this.heelSize = heelSize;
        this.openFront = openFront;
    }
}

class outerwear extends ClothingItems{
    string style;
}

class accessories extends ClothingItems{
    string style;
}