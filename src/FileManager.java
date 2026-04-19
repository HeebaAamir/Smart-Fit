import java.io.*;

public class FileManager {

    // These .ser files will appear in project folder after the first time the app is closed
   
    private static final String WARDROBE_FILE  = "wardrobe.ser";
    private static final String OUTFITS_FILE   = "outfits.ser";
    private static final String SCHEDULE_FILE  = "schedule.ser";
    private static final String IMGMAP_FILE    = "imgmap.ser";

   //save methods
    public static void saveWardrobe(Wardrobe w) {
        saveObject(w, WARDROBE_FILE);
    }

    public static void saveOutfits(OutfitManager om) {
        saveObject(om, OUTFITS_FILE);
    }

    public static void saveSchedule(OutfitScheduler os) {
        saveObject(os, SCHEDULE_FILE);
    }

    public static void saveImgMap(java.util.Map<ClothingItems, String> map) {
        saveObject(new java.util.HashMap<>(map), IMGMAP_FILE);
    }

    
    public static void saveAll(Wardrobe w, OutfitManager om,
                               OutfitScheduler os,
                               java.util.Map<ClothingItems, String> imgMap) {
        saveWardrobe(w);
        saveOutfits(om);
        saveSchedule(os);
        saveImgMap(imgMap);
        System.out.println("All data saved successfully.");
    }

    
    // Load methods:
   
    // Each load method will return a fresh object if no file exist at that time

    public static Wardrobe loadWardrobe() {
        Wardrobe loaded = (Wardrobe) loadObject(WARDROBE_FILE);
        return loaded != null ? loaded : new Wardrobe();
    }

    public static OutfitManager loadOutfits() {
        OutfitManager loaded = (OutfitManager) loadObject(OUTFITS_FILE);
        return loaded != null ? loaded : new OutfitManager();
    }

    public static OutfitScheduler loadSchedule() {
        OutfitScheduler loaded = (OutfitScheduler) loadObject(SCHEDULE_FILE);
        return loaded != null ? loaded : new OutfitScheduler();
    }

    @SuppressWarnings("unchecked")
    public static java.util.Map<ClothingItems, String> loadImgMap() {
        java.util.Map<ClothingItems, String> loaded =
            (java.util.Map<ClothingItems, String>) loadObject(IMGMAP_FILE);
        return loaded != null ? loaded : new java.util.HashMap<>();
    }

    // One method to load everything
    public static void loadAll(Wardrobe w, OutfitManager om,
                               OutfitScheduler os,
                               java.util.Map<ClothingItems, String> imgMap) {
       
        // Copy wardrobe items
        Wardrobe loadedW = loadWardrobe();
        for (ClothingItems item : loadedW.getAllItems()) {
            w.addItems(item);
        }

        // Copy outfits
        OutfitManager loadedOM = loadOutfits();
        for (Outfit o : loadedOM.getAllOutfits()) {
            om.saveOutfit(o);
        }

       
        OutfitScheduler loadedOS = loadSchedule();
        for (java.util.Map.Entry<java.time.LocalDate,
                java.util.ArrayList<Outfit>> entry
                : loadedOS.getSchedule().entrySet()) {
            for (Outfit o : entry.getValue()) {
                os.addOutfit(entry.getKey(), o);
            }
        }

        // Copy image map
        imgMap.putAll(loadImgMap());

        System.out.println("All data loaded successfully.");
    }

   //private helpers

    private static void saveObject(Object obj, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            out.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Save error (" + filename + "): "
                    + e.getMessage());
        }
    }

    private static Object loadObject(String filename) {
        File file = new File(filename);
        if (!file.exists()) return null; // first launch, no file yet
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(filename))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load error (" + filename + "): "
                    + e.getMessage());
            return null;
        }
    }
}