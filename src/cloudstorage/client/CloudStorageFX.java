package cloudstorage.client;

import cloudstorage.shared.File;
import cloudstorage.shared.Folder;
import cloudstorage.shared.IViewable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class CloudStorageFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Client client;

    private final ListView<IViewable> files = new ListView<>();

    private TextArea text;
    private boolean editMode = false;
    private File openFile = null;

    private boolean viewingFile = false;
    private boolean pushedNotification = false;
    private Timer timer = new Timer();



    @Override
    public void start(Stage stage) {
        client = new Client(this);

        showLogInUI(stage);
    }

    public void updateFileList() {
        Platform.runLater(() -> {
            Folder openFolder = client.getSelectedFolder();
            List<IViewable> viewable = new ArrayList<>();
            viewable.addAll(openFolder.getChildren());
            viewable.addAll(openFolder.getFiles());

            files.getItems().clear();
            for (IViewable view : viewable) {
                files.getItems().add(view);
            }

            //Are you viewing a file?
            if (viewingFile) {
                if (filechanged(openFile, openFolder.getFiles())) {
                    pushedNotification = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("File has been changed");
                    alert.setHeaderText("Someone else has edited the file.");
                    alert.setContentText("Do you want to reload it?");
                    alert.getButtonTypes().add(ButtonType.CANCEL);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            File newFile = null;
                            for (File f : openFolder.getFiles()) {
                                if (f.getId() == openFile.getId()) {
                                    newFile = f;
                                }
                            }

                            if (newFile != null) {
                                text.setText(newFile.getText());
                                openFile = newFile;
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean filechanged(File openFile, List<File> files) {
        for (File f : files) {
            if (f.getId() == openFile.getId()) {
                String text1 = "";
                String text2 = "";
                text1 += f.getText().replace("/r", "").replace("/n", "").replace(" ", "");
                text2 += openFile.getText().replace("/r", "").replace("/n", "").replace(" ", "");

                return (!text1.equals(text2));
            }
        }

        return false;
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
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error logging in");
                alert.setHeaderText("Invalid login");
                alert.setContentText("incorrect username/password");

                alert.showAndWait();
            }
        });
        btnLogIn.setDefaultButton(true);
        grid.add(btnLogIn, 1, 3);

        Button btnRegister = new Button("register");
        btnRegister.setOnAction(event -> {
            showRegisterUI();
            stage.close();
        });
        grid.add(btnRegister, 1, 4);


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
        buttons.getChildren().add(btnBack);

        Button btnNewFile = new Button("New File");
        btnNewFile.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("new file");
            dialog.setTitle("File Name");
            dialog.setHeaderText("File Name");
            dialog.setContentText("Please enter a name for the file:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(name -> client.createFile(name));
            updateFileList();
        });
        buttons.getChildren().add(btnNewFile);

        Button btnShareFile = new Button("Share File");
        btnShareFile.setOnAction(event -> {
            IViewable selectedItem = files.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof File) {
                TextInputDialog dialog = new TextInputDialog("Share File");
                dialog.setTitle("Share File");
                dialog.setHeaderText("With who do you want to share?");
                dialog.setContentText("Please enter their username:");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(name -> client.shareFile((File) selectedItem, result.get()));
            }
        });
        buttons.getChildren().add(btnShareFile);

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

                        if (selectedItem.getName().equals("Shared with You")) {
                            btnCreateFolder.setDisable(true);
                            btnNewFile.setDisable(true);
                            btnShareFile.setDisable(true);
                        } else {
                            btnCreateFolder.setDisable(false);
                            btnNewFile.setDisable(false);
                            btnShareFile.setDisable(false);

                        }
                    } else if (selectedItem instanceof File) {
                        showFileUI((File) selectedItem);
                        stage.close();
                    }
                    updateFileList();
                }
            }
        });

        btnBack.setOnAction(event -> {
            if (client.getSelectedFolder() != client.getRoot()) {
                client.selectFolder(client.getSelectedFolder().getParent());
                updateFileList();
            }
        });

        fileSelection.getChildren().add(files);

        Scene scene = new Scene(root, 350, 400);

        stage.setOnCloseRequest(t -> {
            client.logout();
            Platform.exit();
            System.exit(0);
        });

        stage.setTitle("CloudStorage - GSO3");
        stage.setScene(scene);
        stage.show();
    }

    private void showRegisterUI() {
        Stage stage = new Stage();
        Group root = new Group();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        root.getChildren().add(grid);

        Text sceneTitle = new Text("Welcome to CloudStorage");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label lblUsername = new Label("User Name:");
        grid.add(lblUsername, 0, 1);

        TextField txtUsername = new TextField();
        grid.add(txtUsername, 1, 1);

        Label lblPassword = new Label("Password:");
        grid.add(lblPassword, 0, 2);

        PasswordField pfPassword = new PasswordField();
        grid.add(pfPassword, 1, 2);

        Label lblEmail = new Label("Email:");
        grid.add(lblEmail, 0, 3);

        TextField txtEmail = new TextField();
        grid.add(txtEmail, 1, 3);

        Button btnRegister = new Button("register");
        btnRegister.setOnAction(event -> {
            String username = txtUsername.getText();
            String password = pfPassword.getText();
            String email = txtEmail.getText();
            if (username.length() >= 4 && password.length() >= 6 && email.length() >= 6) {
                if (client.register(username, password, email)) {
                    showCloudStorageUI();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error registering");
                    alert.setHeaderText("Something went wrong with registration");
                    alert.setContentText("This username or email already exists.");

                    alert.showAndWait();
                }

            }
        });
        btnRegister.setDefaultButton(true);
        grid.add(btnRegister, 1, 4);

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("register");
        stage.setScene(scene);
        stage.show();
    }

    private void showFileUI(File file) {
        openFile = file;
        viewingFile = true;

        resetTimer();

        Stage stage = new Stage();
        Group root = new Group();

        VBox vert = new VBox();
        vert.setSpacing(10);
        root.getChildren().add(vert);

        HBox hor = new HBox();
        hor.setAlignment(Pos.CENTER);
        hor.setSpacing(10);
        vert.getChildren().add(hor);

        Label lblEditMode = new Label("Edit mode: off");
        lblEditMode.setAlignment(Pos.BOTTOM_RIGHT);
        hor.getChildren().add(lblEditMode);

        text = new TextArea();
        text.setMaxWidth(480);
        text.setText(file.getText());
        text.setDisable(true);
        vert.getChildren().add(text);

        Button btnEditFile = new Button("Edit File");
        btnEditFile.setOnAction(event -> {
            viewingFile = false;
            if (editMode) {
                return;
            }

            if (client.requestEditFile(file)) {
                editMode = true;
                text.setDisable(false);
                lblEditMode.setText("Edit mode: on");
                stopTimer();
            } else {
                viewingFile = true;

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cannot edit file");
                alert.setHeaderText("You don't have the permission to edit.");
                alert.setContentText("Someone else is editing this file!");

                alert.showAndWait();
            }
        });
        hor.getChildren().add(btnEditFile);

        Button btnSaveFile = new Button("Save File");
        btnSaveFile.setOnAction(event -> {
            String fileText = text.getText();
            if (client.saveFile(file, fileText)) {
                editMode = false;
                text.setDisable(true);
                resetTimer();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateOpenFile();
                viewingFile = true;
                lblEditMode.setText("Edit mode: off");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error saving file");
                alert.setHeaderText("You don't have the permission to save.");
                alert.setContentText("First request to edit the file!");

                alert.showAndWait();
            }
        });
        hor.getChildren().add(btnSaveFile);

        Button btnDownloadFile = new Button("Download File");
        btnDownloadFile.setOnAction(event -> {
            String fileText = text.getText();
            FileDownloader.download("", client.getRoot().getFile(file.getId()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Downloaded file");
            alert.setHeaderText("Succesfully downloaded file!");
            alert.setContentText("It is saved in the application directory");

            alert.showAndWait();
        });
        hor.getChildren().add(btnDownloadFile);

        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(event -> {
            timer.cancel();

            if (editMode) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("You have not saved yet.");
                alert.setHeaderText("Closing this window will lose all your work.");
                alert.setContentText("Are you sure?");
                alert.getButtonTypes().add(ButtonType.CANCEL);

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        client.cancelEditFile(file);
                        showCloudStorageUI();
                    } else {
                        event.consume();
                    }
                });
            } else {
                showCloudStorageUI();
            }
        });

        Scene scene = new Scene(root, 480, 218);
        stage.setTitle("File - " + file.getName());
        stage.setScene(scene);
        stage.show();
    }

    private void updateOpenFile() {
        for (File f : client.pullSelectedFolder().getFiles()) {
            if (openFile.getId() == f.getId()) {
                openFile = f;
                return;
            }
        }
    }

    private void stopTimer() {
        timer.cancel();
        timer = new Timer();
    }

    private void resetTimer() {
        pushedNotification = false;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!pushedNotification) {
                    client.clientPull();
                }
            }
        };

        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 2500);
    }
}
