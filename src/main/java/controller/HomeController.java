package controller;

import Objects.Document;
import dao.BookDAO;
import dao.BorrowRecordDAO;
import dao.RequestDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController implements Initializable {
    @FXML
    private GridPane bookContainer;

    @FXML
    private GridPane bookContainer1;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestionBox;

    @FXML
    private Button profileButton;

    @FXML
    private VBox suggestionContent;

    private List<Document> recommended;
    private List<Document> availableBooks;
    public static int userId;
    private RequestDAO requestDAO = new RequestDAO();
    private BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();
    private ExecutorService executor = Executors.newFixedThreadPool(2); // ExecutorService để quản lý thread pool

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSearchSuggestions();

        // Sử dụng Task và ExecutorService để tải dữ liệu trong nền
        Task<Void> loadRecommendedTask = new Task<>() {
            @Override
            protected Void call() {
                loadRecommendedBooks();
                return null;
            }
        };

        Task<Void> loadAvailableBooksTask = new Task<>() {
            @Override
            protected Void call() {
                loadAvailableBooks();
                return null;
            }
        };

        executor.submit(loadRecommendedTask);
        executor.submit(loadAvailableBooksTask);
    }

    private void initializeSearchSuggestions() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                showSuggestions(newValue.trim());
            } else {
                hideSuggestions();
            }
        });

        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hideSuggestions();
            }
        });

        searchButton.setOnAction(event -> onSearchButtonClick());
    }

    private void loadAvailableBooks() {
        try {
            BookDAO bookDAO = new BookDAO();
            availableBooks = bookDAO.getAllDocuments();
            System.out.println("Loaded available books: " + availableBooks.size());
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load available books.", Alert.AlertType.ERROR));
        }
    }

    private void showSuggestions(String query) {
        List<Document> filteredBooks = availableBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()))
                .limit(5)
                .toList();

        if (filteredBooks.isEmpty()) {
            hideSuggestions();
            return;
        }

        Platform.runLater(() -> {
            try {
                suggestionContent.getChildren().clear();
                for (Document book : filteredBooks) {
                    HBox suggestionBoxItem = createSuggestionBoxItem(book);
                    suggestionContent.getChildren().add(suggestionBoxItem);
                }
                suggestionBox.setVisible(true);
                suggestionBox.setManaged(true);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to show suggestions.", Alert.AlertType.ERROR);
            }
        });
    }

    // Tạo HBox cho mỗi gợi ý sách
    private HBox createSuggestionBoxItem(Document book) {
        HBox suggestionBoxItem = new HBox(10);
        suggestionBoxItem.setPadding(new Insets(5));
        suggestionBoxItem.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");
        suggestionBoxItem.setPrefHeight(40);

        ImageView bookImageView = new ImageView();
        bookImageView.setFitHeight(30);
        bookImageView.setFitWidth(30);
        bookImageView.setPreserveRatio(true);
        bookImageView.setSmooth(true);
        bookImageView.setCache(true);

        String thumbnailUrl = book.getThumbnailLink();
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            // Sử dụng Task để tải hình ảnh trong nền
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(thumbnailUrl.replace("http://", "https://"), true);
                }
            };

            loadImageTask.setOnSucceeded(e -> bookImageView.setImage(loadImageTask.getValue()));
            loadImageTask.setOnFailed(e -> bookImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png"))));
            executor.submit(loadImageTask);
        } else {
            bookImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_book.png")));
        }

        Label bookTitleLabel = new Label(book.getTitle());
        bookTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        bookTitleLabel.setWrapText(true);
        bookTitleLabel.setMaxWidth(450);

        suggestionBoxItem.getChildren().addAll(bookImageView, bookTitleLabel);

        suggestionBoxItem.setOnMousePressed(event -> {
            searchField.setText(book.getTitle());
            hideSuggestions();
            openBookDetail(book);
            event.consume();
        });

        return suggestionBoxItem;
    }

    private void onSearchButtonClick() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            showSuggestions(query);
            // Thêm logic tìm kiếm ở đây nếu cần
        }
    }

    private void hideSuggestions() {
        Platform.runLater(() -> {
            if (suggestionBox != null) {
                suggestionBox.setVisible(false);
                suggestionBox.setManaged(false);
            }
        });
    }

    private void loadSuggestBooks() {
        try {
            UserDAO userDAO = new UserDAO();
            String favoriteCategory = userDAO.getUserFavoriteCategory(userId);

            BookDAO bookDAO = new BookDAO();
            List<Document> suggestedBooks = bookDAO.getByCategories(favoriteCategory);
            System.out.println("Suggested books loaded: " + suggestedBooks.size());

            int column = 0;
            int row = 1;

            if (bookContainer1 != null) {
                Platform.runLater(() -> bookContainer1.getChildren().clear()); // clear() trên JavaFX Application Thread
                for (Document document : suggestedBooks) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/small_book.fxml"));
                    VBox bookBox = fxmlLoader.load();
                    bookBox.getStylesheets().add(getClass().getResource("/css/cardStyling.css").toExternalForm());

                    SmallBookController smallBookController = fxmlLoader.getController();
                    smallBookController.loadBookInfo(document);

                    bookBox.setOnMouseClicked(event -> openBookDetail(document));

                    if (column == 8) {
                        column = 0;
                        ++row;
                    }

                    int finalColumn = column++;
                    int finalRow = row;
                    Platform.runLater(() -> {
                        bookContainer1.add(bookBox, finalColumn, finalRow);
                        GridPane.setMargin(bookBox, new Insets(15));
                    });
                }
            } else {
                System.err.println("bookContainer1 is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load suggested books.", Alert.AlertType.ERROR));
        }
    }

    private void loadRecommendedBooks() {
        try {
            recommended = documents();
            System.out.println("Recommended books loaded: " + recommended.size());
            int column = 0;
            int row = 1;

            if (bookContainer != null) {
                Platform.runLater(() -> bookContainer.getChildren().clear()); // clear() trên JavaFX Application Thread
                for (Document document : recommended) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/small_book.fxml"));
                    VBox bookBox = fxmlLoader.load();
                    bookBox.getStylesheets().add(getClass().getResource("/css/cardStyling.css").toExternalForm());

                    SmallBookController smallBookController = fxmlLoader.getController();
                    smallBookController.loadBookInfo(document);

                    bookBox.setOnMouseClicked(event -> openBookDetail(document));

                    if (column == 8) {
                        column = 0;
                        ++row;
                    }

                    int finalColumn = column++;
                    int finalRow = row;
                    Platform.runLater(() -> {
                        bookContainer.add(bookBox, finalColumn, finalRow);
                        GridPane.setMargin(bookBox, new Insets(15));
                    });
                }
            } else {
                System.err.println("bookContainer is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load recommended books.", Alert.AlertType.ERROR));
        }
    }

    private List<Document> documents() {
        List<Document> ls = new ArrayList<>();
        try {
            BookDAO bookDAO = new BookDAO();
            ls = bookDAO.getAllDocuments();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to load documents.", Alert.AlertType.ERROR));
        }
        return ls;
    }

    @FXML
    private void search(ActionEvent event) {
        // Có thể thêm logic tìm kiếm ở đây
    }

    @FXML
    public void switchToBorrowed(ActionEvent event) {
        try {
            // Lấy userId từ UserSession
            int userId = Session.getUserId();


            // Tải FXML của giao diện thứ hai
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/borrowedDocuments.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToMore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserMore.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Profile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Profile window.", Alert.AlertType.ERROR);
        }
    }

    private void openBookDetail(Document document) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/bookDetail.fxml"));
            Parent bookDetailPage = fxmlLoader.load();

            BookDetailsController bookDetailsController = fxmlLoader.getController();
            bookDetailsController.loadBookDetails(document, userId, requestDAO, borrowRecordDAO);

            Stage stage = new Stage();
            Scene scene = new Scene(bookDetailPage);
            stage.setScene(scene);
            stage.setTitle(document.getTitle());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void setUserId(int userId) {
        this.userId = userId;
        // Sử dụng Task và ExecutorService để tải dữ liệu trong nền
        Task<Void> loadSuggestBooksTask = new Task<>() {
            @Override
            protected Void call() {
                loadSuggestBooks();
                return null;
            }
        };
        executor.submit(loadSuggestBooksTask);
    }

    public int getUserId() {
        return userId;
    }
}