# SmartFit
# Title and Team Information
**Project Title:** SmartFit

**Team Members:**
- **Heeba Amir**    
- **Umayrah Masood Malik**   
- **Urooj Fatima**
---
# Overview
“SmartFit”, a system designed specially to simplify the process of organizing wardrobe and planning outfits. This application provides an interactive platform where users can manage their clothing items, design outfit combinations, and explore new styling ideas. The system aims to help users make better use of their existing wardrobe while reducing the time spent deciding what to wear.   

---
# Features
- Create outfits using templates
- Digitalize your Wardrobe
- Organize your Wardrobe with respect to brand, material, etc.
- Save outfits in digital closet
- Plan outfits on calendar
- Search outfits by image
- Rate outfits
---
# OOP Concepts :  
## OOP Concepts: Application

| Concept                         | Implementation |
|---------------------------------|---------------|
| **Encapsulation**               | Clothing items have private attributes as properties, with public getters and setters. Other classes follow the same principle as per requirements. |
| **Abstraction**                 | `ClothingItems` is an abstract class that hides implementation details and exposes only essential attributes like color, size, and brand. |
| **Polymorphism**                | Each subclass overrides `getItemType()` to return its specific type, so the same method call produces different results at runtime. |
| **Inheritance**                 | `Top`, `Bottom`, `Shoes`, `Accessories`, and `Outerwear` extend `ClothingItems`, inheriting common attributes and methods while adding their own. |
| **Interface**                   | The built-in Java interface `Serializable` is used for data persistence. |
| **Composition**                 | `Outfit` is composed of `Top`, `Bottom`, and `Shoes`, meaning these are core parts directly owned by it. |
| **Aggregation**                 | `OutfitManager` aggregates `Outfit` objects and `Wardrobe` aggregates `ClothingItems`, meaning they hold references but objects can exist independently. |
| **Single Responsibility Principle** | Each class has one clearly defined responsibility. |
| **Utility Classes**             | `FileManager`, `PngFiles`, and `ImageUploader` provide static helper methods without maintaining state. |
| **Collections & Generics**      | Uses `HashMap`, `ArrayList`, and `List` for data handling. | 
---
# Technical Requirements
- **Java 17** - Programming Language
- **VS Code** - IDE
-  **Git/GitHub** - Version Control
- **JavaFX** - for the GUI
- **Groq API** - Image Processing & Suggestions
- **ImgBB API** - Creating public URLs of our local images. 
- **Serp API** - Fetching the results from the Web
---
# How To Run :
**Requirements :**  
1. Java 17+ and JavaFX sdk should be installed.
2. Generate API keys from SerpAPI, ImageBB and GroqAPI websites and use them in the relevant spaces.
3. Run these comands to compile and run :
```bash
# Compile    
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp "src/gson-2.10.1.jar" *.java    
# Run   
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp "bin;src/gson-2.10.1.jar" App
```
---
# Data Persistence :   
SmartFit uses Java Serialization to ensure no data is lost between sessions.
1. Objects that are saved: Wardrobe, OutfitManager and OutfitScheduler all implement the Serializable interface
2. Data is stored locally as .ser files (wardrobe.ser, outfits.ser and schedule.ser)
3. FileManager utility class handles all saving and loading operations
4. Data is automatically saved every time the user adds a clothing item, saves an outfit or schedules an outfit
5. On relaunch, FileManager deserializes the .ser files and fully restores the application to its last saved state
---
# Current Limitations : 
1. Our SerpAPI is unable to fetch price of items from websites.
2. There is no input validation implemented. Users can input a bottom at top section and the program will take it.
3. When assigning outfits to a date, user can only see the name of outfit rather the whole fit.
4. User can currently select one accessory per outfit.
