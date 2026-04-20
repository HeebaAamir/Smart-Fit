// Main.java
// ─────────────────────────────────────────────────────────────────────────────
// Entry point for SmartFit.
// This class ONLY:
//   1. Creates the model objects (Wardrobe, OutfitManager, OutfitScheduler)
//   2. Passes them to the GUI class
//   3. Launches the JavaFX application

import javafx.application.Application;

public class App {

    public static void main(String[] args) {

        // Load saved data from .ser files (or create fresh objects on first run)
        Wardrobe        wardrobe   = FileManager.loadWardrobe();
        OutfitManager   outfitMgr  = FileManager.loadOutfits();
        OutfitScheduler scheduler  = FileManager.loadSchedule();

        // Pass the objects into the GUI and launch
        // SmartFitGUI stores the references and uses them across all screens
        SmartFitGUI.setModel(wardrobe, outfitMgr, scheduler);

        // This starts the JavaFX application — calls SmartFitGUI.start()
        Application.launch(SmartFitGUI.class, args);
    }
}