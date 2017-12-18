package cloudstorage.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CloudStorageFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Client client;

    @Override
    public void start(Stage stage) {
        client = new Client();

        stage.setTitle("login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Welcome to CloudStorage");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btnLogIn = new Button("log in");
        btnLogIn.setOnAction(event -> {
              ShowCloudStorageUI();
              stage.close();
        });
        grid.add(btnLogIn, 1, 3);


        Scene scene = new Scene(grid, 300, 275);
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

    private void ShowCloudStorageUI() {
        Stage stage = new Stage();
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
}
