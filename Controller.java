import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    private ImageView shirtImageView;
    private ImageView pantImageView;

    private Button shirtButton;
    private Button pantButton;

    public Controller() {

        shirtImageView = createImageView();
        pantImageView = createImageView();

        shirtButton = new Button("Select Shirt PNG");
        pantButton = new Button("Select Pant PNG");

        // Button actions
        shirtButton.setOnAction(e -> loadImage(shirtImageView, "Select Shirt PNG"));
        pantButton.setOnAction(e -> loadImage(pantImageView, "Select Pant PNG"));
    }

    private ImageView createImageView() {
        ImageView iv = new ImageView();
        iv.setFitWidth(200);
        iv.setFitHeight(200);
        iv.setPreserveRatio(true);
        return iv;
    }

    private void loadImage(ImageView imageView, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    public ImageView getShirtImageView() {
        return shirtImageView;
    }

    public ImageView getPantImageView() {
        return pantImageView;
    }

    public Button getShirtButton() {
        return shirtButton;
    }

    public Button getPantButton() {
        return pantButton;
    }
}