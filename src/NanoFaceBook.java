import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 25-10-15.
 */
public class NanoFaceBook extends Application
{
    private Connection conn = null;
    BorderPane borderPane;
    VBox loginVBox;

    VBox newsFeed = new VBox(4);
    ScrollPane newsFeedPane = new ScrollPane(newsFeed);
    private Button logoutButton = new Button("Log out");
    private VBox addFriendVBox = new VBox(5);
    TextField nameTextField = new TextField();
    TextField passwordTextField = new TextField();
    int likes;
    MyLabel numberOfLikes;
    private Map<Integer,Label> map = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    public NanoFaceBook() {
        try {
            String DB_URL = "jdbc:mysql://localhost:8889/REDdb";
            String USER = "root";
            String PASS = "root";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("conn obj created" + conn + " message: ");
        } catch (SQLException e) {
            System.out.println("db error " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        nameTextField.setPromptText("enter username");
        passwordTextField.setPromptText("type password");

        Button createUser = new Button("Sign up");
        TextField fullName = new TextField();
        fullName.setPromptText("Enter your full name for a first time..");

        TextField searcFriendField = new TextField();
        searcFriendField.setPromptText("Add friend...");

        Button searchButton = new Button("Add");
        searchButton.setOnAction(event2 -> {

            try {
                String sql2 = "INSERT INTO Friends(userName, friendsUserName) VALUES (?, ?)";

                PreparedStatement preparedStatement2 = conn.prepareStatement(sql2);
                preparedStatement2.setString(1, nameTextField.getText());
                preparedStatement2.setString(2, searcFriendField.getText());

                int numberOfRows = preparedStatement2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

        TextField newPostField = new TextField();
        newPostField.setPromptText("Post something...");


        Button postButton = new Button("Post");
        postButton.setOnAction(event1 -> {

            try {
                String sql3 = "INSERT INTO Posts(userName, texthjh, numberOfLikes) VALUES (?, ?, NULL)";

                PreparedStatement preparedStatement3 = conn.prepareStatement(sql3);
                preparedStatement3.setString(1, nameTextField.getText());
                preparedStatement3.setString(2, newPostField.getText());

                int numberOfRows = preparedStatement3.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            VBox post = new VBox(3);
            TextField comment = new TextField();
            comment.setStyle("-fx-font-size: 8pt");
            comment.setOnAction(event -> {
                System.out.println("message sent to server " + comment.getText() + " on post nr: ");
            });
            post.getChildren().add(new Text(newPostField.getText()));
            Button likeButton = new Button("Like");
            likeButton.setOnAction(event -> {

                ////////
                System.out.println("you liked the text: Message nr.");
            });
            likeButton.setStyle("-fx-font-size: 6pt");

            // if any replies, the add them here:
            Text text = new Text("Reply: this is outrageous!");
            text.setStyle("-fx-font-size: 8pt");
            numberOfLikes.setStyle("-fx-font-size: 8pt");
            post.getChildren().addAll(numberOfLikes, text);
            post.getChildren().addAll(likeButton, comment);
            newsFeed.getChildren().add(0, post);

            newPostField.clear();
        });

        addFriendVBox.getChildren().addAll(searcFriendField, searchButton, newPostField, postButton);

        borderPane = new BorderPane();
        loginVBox = new VBox(5);

        passwordTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent enterKey) {
                if (enterKey.getCode().equals(KeyCode.ENTER)){
                    login();
                }
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {login();});

        logoutButton.setOnAction(event1 -> {
            logout();
        });

        createUser.setOnAction(event -> {
            try {
                String sql = "INSERT INTO Users (userName, name, password) VALUES ( ?, ?, ?) ";

                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, nameTextField.getText());
                preparedStatement.setString(2, fullName.getText());
                preparedStatement.setString(3, passwordTextField.getText());

                int numberOfRows = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        loginVBox.getChildren().addAll(nameTextField, passwordTextField, loginButton, createUser, fullName);
        loginVBox.setSpacing(10);
        loginVBox.setAlignment(Pos.CENTER);
        borderPane.setTop(loginVBox);
        borderPane.setId("background");

        Scene scene = new Scene(borderPane, 600, 600);
        scene.getStylesheets().addAll(NanoFaceBook.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("NANO FaceBook");
        primaryStage.show();
    }

    public void logout() {System.exit(0);}

    public void login() {

        // fill news

        try {
            /*String sql4 = "SELECT *\n" +
                    "FROM Posts INNER JOIN Friends ON Posts.userName = Friends.friendsUserName \n" +
                    "WHERE Friends.userName = ? ORDER BY postID DESC";*/
            String sql4 = "SELECT * FROM Posts ORDER BY postID DESC";

            PreparedStatement preparedStatement4 = conn.prepareStatement(sql4);
            //preparedStatement4.setString(1, nameTextField.getText());
            //preparedStatement4.setString(2, nameTextField.getText());
            //System.out.println("NAME SHOULD BE: " + nameTextField.getText());
            ResultSet myRs4 = preparedStatement4.executeQuery();

            while (myRs4.next()) {
                VBox post = new VBox(3);
                TextField comment = new TextField();
                comment.setStyle("-fx-font-size: 8pt");
                comment.setOnAction(event -> {
                    System.out.println("message sent to server " + comment.getText() + " on post nr: ");
                });

                post.getChildren().add(new Text(myRs4.getString("userName") + ": " + myRs4.getString("texthjh")));

                MyButton likeButton = new MyButton("Like");
                likeButton.id = myRs4.getInt("postID");
                MyLabel numberOfLikes = new MyLabel(likes + " people like this");

                map.put(likeButton.id, numberOfLikes);

                likeButton.setOnAction(event -> {
                    MyButton button = (MyButton)event.getSource();
                    updateLikes(button.id);
                    showUpdatedLikes(button.id);
                });

                likeButton.setStyle("-fx-font-size: 6pt");
                numberOfLikes.setStyle("-fx-font-size: 8pt");

                post.getChildren().add(numberOfLikes);
                post.getChildren().addAll(likeButton, comment);
                newsFeed.getChildren().add(post);

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            String sql = "SELECT * FROM Users WHERE userName = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, nameTextField.getText());

            ResultSet myRs = preparedStatement.executeQuery();

            if (myRs.next()) {
                String passMatch = myRs.getString("password");

                if (passMatch.equals(passwordTextField.getText())) {
                    HBox hBox = new HBox(5);
                    Text text = new Text("User: Mr. " + myRs.getString("name") + " is logged in.");
                    text.setFill(Color.WHITE);
                    text.setFont(Font.font(null, FontWeight.BOLD, 20));

                    VBox friendsVbox = new VBox();

                    String sql1 = "SELECT friendsUserName FROM Friends WHERE userName = ? ORDER BY friendsUserName";

                    PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
                    preparedStatement1.setString(1, myRs.getString("userName"));

                    ResultSet myRs1 = preparedStatement1.executeQuery();

                    while (myRs1.next()) {
                        friendsVbox.getChildren().add(new Label("     " + myRs1.getString("friendsUserName")));}

                    hBox.getChildren().addAll(text, logoutButton);
                    borderPane.setTop(hBox);
                    borderPane.setCenter(newsFeedPane);
                    Label rightLabel=new Label("    Friends:    ");
                    VBox rightVbox = new VBox();
                    rightVbox.getChildren().addAll(rightLabel, friendsVbox);

                    borderPane.setMargin(addFriendVBox, new Insets(12,12,12, 40));
                    borderPane.setMargin(rightLabel, new Insets(40,12,12, 40));
                    borderPane.setLeft(addFriendVBox);
                    borderPane.setRight(rightVbox);

                } else {
                    System.out.println("not same!!!");
                }
            } else
                System.out.println("no data");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLikes(int id)
    {

        int likes = 0;

        try {

            String sql5 = "SELECT Posts.numberOfLikes FROM Posts WHERE Posts.postID = ?";

            PreparedStatement preparedStatement5 = conn.prepareStatement(sql5);
            preparedStatement5.setInt(1, id);
            System.out.println("post id: " + id);
            ResultSet myRs5 = preparedStatement5.executeQuery();

            while (myRs5.next()) {
                System.out.println(myRs5.getInt("numberOfLikes"));

                likes = myRs5.getInt("numberOfLikes");
                System.out.println("likes: " + likes);
                likes++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("updated likes: " + likes);

        try {
            String sql6 = "UPDATE Posts SET numberOfLikes = ? WHERE postID = ?";

            PreparedStatement preparedStatement6 = conn.prepareStatement(sql6);
            preparedStatement6.setInt(1, likes);
            preparedStatement6.setInt(2, id);

            int numberOfRows = preparedStatement6.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showUpdatedLikes(int id) {
        try {

            String sql5 = "SELECT Posts.numberOfLikes FROM Posts WHERE Posts.postID = ?";

            PreparedStatement preparedStatement5 = conn.prepareStatement(sql5);
            preparedStatement5.setInt(1, id);
            System.out.println("post id: " + id);
            ResultSet myRs5 = preparedStatement5.executeQuery();


                while (myRs5.next()) {
                    System.out.println(myRs5.getInt("numberOfLikes"));
                    // map.get(17).setText(...)
                    likes = myRs5.getInt("numberOfLikes");
                    //numberOfLikes.setText(likes + " people like this");
                    map.get(id).setText(likes + " people like this");

                }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
