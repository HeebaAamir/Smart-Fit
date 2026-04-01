public class Outfit {
   private top Top;
   private bottom Bottom;
   private shoes Shoe;
   private accessories Accessory = null;
   private outerwear OuterWear = null;

   public Outfit (top Top, bottom Bottom, shoes Shoe, accessories Accessory, outerwear OuterWear){
    this.Top = Top;
    this.Bottom = Bottom;
    this.Shoe = Shoe;
    this.Accessory = Accessory;
    this.OuterWear = OuterWear;
   }
    public void setTop(top Top){
        this.Top = Top;
    }
    public top getTop(){
        return Top;
    }
    public void setBottom(bottom Bottom){
        this.Bottom = Bottom;
    }
    public bottom getBottom(){
        return Bottom;
    }
    public void setShoes(shoes Shoe){
        this.Shoe = Shoe;
    }
    public shoes getShoes(){
        return Shoe;
    }
    public void setAccessory(accessories Accessory){
        this.Accessory = Accessory;
    }
    public accessories getAccessories(){
        return Accessory;
    }
    public void setOuterWear(outerwear OuterWear){
        this.OuterWear = OuterWear;
    }
    public outerwear getOuterwear(){
        return OuterWear;
    }

}
