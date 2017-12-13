package cloudstorage.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CloudStorageFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        GridPane pane = new GridPane();
        pane.setMinSize(350, 400);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(pane);

        VBox fileSelection = new VBox();
        fileSelection.setSpacing(10);
        pane.add(fileSelection, 1, 0);

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        fileSelection.getChildren().add(buttons);

        Button btnBack = new Button("<-");
        buttons.getChildren().add(btnBack);

        Button btnUpload = new Button("Upload File");
        buttons.getChildren().add(btnUpload);

        Button btnDownload = new Button("Download File");
        buttons.getChildren().add(btnDownload);

        Button btnCreateFolder = new Button("Create Folder");
        buttons.getChildren().add(btnCreateFolder);

        final ListView<String> files = new ListView<>();
        files.getItems().addAll(createFileList());
        files.setPrefSize(300, 350);
        files.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        files.getSelectionModel().selectedItemProperty().addListener(this::fileSelected);
        fileSelection.getChildren().add(files);

        Scene scene = new Scene(root, 350, 400);

        stage.setTitle("CloudStorage - GSO3");
        stage.setScene(scene);
        stage.show();
    }

    private ArrayList<String> createFileList() {
        ArrayList<String> items = new ArrayList<>();

        items.add("1");
        items.add("2");

        return items;
    }

    private void fileSelected(ObservableValue<? extends String> observable,String oldValue,String newValue) {

    }
}
