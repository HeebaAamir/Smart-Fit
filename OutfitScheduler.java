import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

//a calendar for outfits
 
public class OutfitScheduler {

    private HashMap<LocalDate, ArrayList<Outfit>> schedule;

    public OutfitScheduler() {
        schedule = new HashMap<>();
    }

    // Add outfit to a date
    public void addOutfit(LocalDate date, Outfit outfit) {
        schedule.putIfAbsent(date, new ArrayList<>());
        schedule.get(date).add(outfit);

        System.out.println("Outfit scheduled for " + date);
    }

    // View outfits for a specific date
    public void viewOutfits(LocalDate date) {
        if (!schedule.containsKey(date)) {
            System.out.println("No outfits scheduled");
            return;
        }

        System.out.println("Outfits for " + date + ":");

        for (Outfit o : schedule.get(date)) {
            System.out.println(o);
        }
    }

    // View all scheduled outfits
    public void viewAll() {
        for (LocalDate date : schedule.keySet()) {
            System.out.println("Date: " + date);

            for (Outfit o : schedule.get(date)) {
                System.out.println(" * " + o);
            }
        }
    }
}
