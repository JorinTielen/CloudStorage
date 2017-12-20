package cloudstorage.client;

import cloudstorage.shared.Folder;
import cloudstorage.shared.IViewable;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CloudStorageFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Client client;

    private final ListView<IViewable> files = new ListView<>();


    @Override
    public void start(Stage stage) {
        client = new Client();

        showLogInUI(stage);
    }

    private void updateFileList() {
        Folder openFolder = client.getCurrentFolder();
        List<IViewable> viewable = new ArrayList<>();
        viewable.addAll(openFolder.getChildren());
        viewable.addAll(openFolder.getFiles());

        files.getItems().clear();
        for (IViewable view : viewable) {
            files.getItems().add(view);
        }
    }
    
    private void showLogInUI(Stage stage) {
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
            if (client.login(userTextField.getText(), pwBox.getText())) {
                showCloudStorageUI();
                stage.close();
            }
        });
        grid.add(btnLogIn, 1, 3);


        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        stage.show();
    }

    private void showCloudStorageUI() {
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
        btnBack.setOnAction(event -> {
            if (client.getCurrentFolder() != client.getRoot()) {
                client.selectFolder(client.getCurrentFolder().getParent());
                updateFileList();
            }
        });
        buttons.getChildren().add(btnBack);

        Button btnNewFile = new Button("New File");
        buttons.getChildren().add(btnNewFile);

        Button btnDownloadFile = new Button("Download File");
        buttons.getChildren().add(btnDownloadFile);

        Button btnCreateFolder = new Button("Create Folder");
        btnCreateFolder.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("new folder");
            dialog.setTitle("Folder Name");
            dialog.setHeaderText("Folder Name");
            dialog.setContentText("Please enter a name for the folder:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(name -> client.createFolder(name));
            updateFileList();
        });
        buttons.getChildren().add(btnCreateFolder);

        //Files ListView
        updateFileList();
        files.setPrefSize(300, 350);
        files.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        files.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    IViewable selectedItem = files.getSelectionModel().getSelectedItem();
                    if (selectedItem instanceof Folder) {
                        client.selectFolder((Folder) selectedItem);
                    }
                    updateFileList();
                }
            }
        });
        fileSelection.getChildren().add(files);

        Scene scene = new Scene(root, 350, 400);

        stage.setTitle("CloudStorage - GSO3");
        stage.setScene(scene);
        stage.show();
    }
}
