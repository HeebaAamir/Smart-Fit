import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class input extends Application {

    @Override
    public void start(Stage stage) {
        Controller controller = new Controller();

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding: 20; -fx-spacing: 15;");

        root.getChildren().addAll(
                controller.getShirtButton(),
                controller.getShirtImageView(),
                controller.getPantButton(),
                controller.getPantImageView()
        );

        Scene scene = new Scene(root, 500, 600);

        stage.setTitle("Outfit Viewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}