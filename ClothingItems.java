public class ClothingItems {
    private String size;
    private String brand;
    private int price;
    private String fabric;
    private String season;
    private String colour;
    private int WearCount;

    public ClothingItems(String size, String brand, int price, String fabric, String colour, int WearCount, String season){
        this.size = size;
        this.brand = brand;
        this.price = price;
        this.fabric = fabric;
        this.colour = colour;
        this.WearCount = WearCount;
        this.season = season;
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
}

class top extends ClothingItems{
    String style;
    String sleeveType;

    public top(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String style, String sleeveType){
        super(size, brand, price, fabric, colour, WearCount, season);
        this.style = style;
        this.sleeveType = sleeveType;
    }
}

class bottom extends ClothingItems{
    String style;

    public bottom(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String style){
        super(size, brand, price, fabric, colour, WearCount,season);
        this.style = style;
    }
}

class shoes extends ClothingItems{
    String style;
    int heelSize;
    boolean openFront;
    
    public shoes(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String style, int heelSize, boolean openFront){
        super(size, brand, price, fabric, colour, WearCount,season);
        this.style = style;
        this.heelSize = heelSize;
        this.openFront = openFront;
    }
}

class accessories extends ClothingItems{
    String type;

    public accessories(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String type){
        super(size, brand, price, fabric, colour, season, WearCount);
        this.type = type;
    }
}       

class outerwear extends ClothingItems{
    String style;

    public outerwear(String size, String brand, int price, String fabric, String colour, int WearCount, String season, String style){
        super(size, brand, price, fabric, colour, season, WearCount);
        this.style = style;
        this.style = style;
    }
}   
