package org.example.libraryapp;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LibraryControllerTest extends ApplicationTest {

    private static LibraryController controller;
    private static TextField titleField;
    private static TextField authorField;
    private static TextField yearField;

    @BeforeAll
    static void setUp() {
        Platform.startup(() -> {});

        FXMLLoader loader = new FXMLLoader(LibraryControllerTest.class.getResource("/org/example/libraryapp/library.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controller = loader.getController();

        titleField = controller.titleField;
        authorField = controller.authorField;
        yearField = controller.yearField;
    }
    @Test
    void testSetAndGetTitle() {
        String testTitle = "The Great Gatsby";
        controller.setTitle(testTitle);
        assertEquals(testTitle, controller.getTitle());
    }

    @Test
    void testSetAndGetAuthor() {
        String testAuthor = "F. Scott Fitzgerald";
        controller.setAuthor(testAuthor);
        assertEquals(testAuthor, controller.getAuthor());
    }

    @Test
    void testSetAndGetYear() {
        String testYear = "1925";
        controller.setYear(testYear);
        assertEquals(testYear, controller.getYear());
    }
}
