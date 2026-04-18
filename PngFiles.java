//importing libraries for file handling and scanner for taking pngs as input
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

//main class
public class PngFiles {
    public static void main(String[] args) throws Exception{
        //creating scanner object
        Scanner input = new Scanner(System.in);

        //choice menu
        while(true){
            System.out.println("\n1. Top");
            System.out.println("2. Bottom");
            System.out.println("Enter 0 to exit");
            System.out.print("Enter choice: ");

            //taking input and converting to integer
            int choice = Integer.parseInt(input.nextLine());

            //exiting condition and breaking the loop
            if(choice == 0){
                System.out.println("Program ended");
                break;
            }

            //taking file path as input
            System.out.println("Enter png path: ");
            String path = input.nextLine().replace("\"","");

            //creating file object
            File inputFile = new File(path);

            //checking if files exists
            if(!inputFile.exists()){
                System.out.println("File not found");
                continue;
            }

            //creating one main folder for both tops and bottoms
            File mainFolder = new File("images");
            //if folder doesnt exist then making a folder/directory
            if(!mainFolder.exists()) mainFolder.mkdir();

            //findind the appropriate folder accoding to the choice
            File targetFolder;

            if(choice == 1){
                targetFolder = new File("images/top");
            } 
            else if(choice == 2){
                targetFolder = new File("images/bottom");
            } 
            else{
                System.out.println("Invalid choice");
                continue;
            }

            //creating subfolders if doesnt exist
            if(!targetFolder.exists()) targetFolder.mkdir();

            //counting existing files and then asigning the file name accordingly
            File[] files = targetFolder.listFiles();
            int n =(files==null)?0 : files.length;

            //saving png bu making a copy of it in the folder
            Files.copy(
                inputFile.toPath(),
                Paths.get(targetFolder.getPath() + "/img" +(n + 1)+ ".png"),
                StandardCopyOption.REPLACE_EXISTING
                      );

            System.out.println("Image saved successfully");




        }
    }
    
}
