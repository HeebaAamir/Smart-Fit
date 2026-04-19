//importing libraries for file handling
import java.io.*;
import java.nio.file.*;

public class PngFiles {
    
    //utility method
    public static String saveImage(String sourcePath, String category){
        
    try{ 
        //creating file object
        File inputFile = new File(sourcePath);

        //checking if filw aleady exists or not
        if(!inputFile.exists()){
                System.out.println("File not found" +sourcePath);
                return null;
            }
        //creating one main folder for all images
        File mainFolder = new File("images");
        //if folder doesnt exist then creating new folder
        if(!mainFolder.exists()) mainFolder.mkdirs();

        File targetFolder = new File("images/" + category.toLowerCase());
        if(!targetFolder.exists()) targetFolder.mkdirs();

        //counting existing images to name the new files accordingly
        File[] files = targetFolder.listFiles();
        int n=(files == null) ? 0 : files.length;

        //keeping original extension of the file
        String ext = sourcePath.substring(sourcePath.lastIndexOf('.'));

        //building destination path where the image will be copied/saved
        Path destination = Paths.get(
                targetFolder.getPath() + "/img" + (n + 1) + ext
            );

        //copying file
            Files.copy(
                inputFile.toPath(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
            );
        
        System.out.println("Image saved successfully");

        //returnong saved path of image
       return destination.toAbsolutePath().toString();

    }

    catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
           
        }
    
    
}}
