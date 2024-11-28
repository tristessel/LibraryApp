package org.example.libraryapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class LibraryController {

    @FXML
    private TableView<Book> table;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    @FXML
    TextField titleField;
    @FXML
    TextField authorField;
    @FXML
    TextField yearField;

    ObservableList<Book> books;

    @FXML
    public void initialize() {
        books = FXCollections.observableArrayList();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        loadBooksFromDatabase();
    }

    @FXML
    public void onAdd() {
        String title = titleField.getText();
        String author = authorField.getText();
        int year;

        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Year", "Year must be a valid integer.");
            return;
        }

        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, year);
            stmt.executeUpdate();

            books.add(new Book(title, author, year));
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add book. Please try again.");
        }
    }

    @FXML
    public void onUpdate() {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a book to update.");
            return;
        }

        String title = titleField.getText();
        String author = authorField.getText();
        int year;

        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Year", "Year must be a valid integer.");
            return;
        }

        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setInt(3, year);
            stmt.setInt(4, getBookId(selected));
            stmt.executeUpdate();

            selected.setTitle(title);
            selected.setAuthor(author);
            selected.setYear(year);
            table.refresh();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update book. Please try again.");
        }
    }

    @FXML
    public void onDelete() {
        Book selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a book to delete.");
            return;
        }

        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, getBookId(selected));
            stmt.executeUpdate();

            books.remove(selected);
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete book. Please try again.");
        }
    }

    void loadBooksFromDatabase() {
        books.clear();
        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year")
                ));
            }

            table.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load books from database.");
        }
    }

    private int getBookId(Book book) {
        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "SELECT id FROM books WHERE title = ? AND author = ? AND year = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        yearField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setTitle(String title) {
        titleField.setText(title);
    }

    public String getTitle() {
        return titleField.getText();
    }

    public void setAuthor(String author) {
        authorField.setText(author);
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public void setYear(String year) {
        yearField.setText(year);
    }

    public String getYear() {
        return yearField.getText();
    }
}
