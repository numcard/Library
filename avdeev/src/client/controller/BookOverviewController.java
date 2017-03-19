package client.controller;

import client.ClientApp;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lib.model.LibraryBook;

public class BookOverviewController
{
    @FXML private TableView<LibraryBook> bookTable;
    @FXML private TableColumn<LibraryBook, String> titleColumn;
    @FXML private TableColumn<LibraryBook, String> authorColumn;
    @FXML private TableColumn<LibraryBook, Integer> copyrightColumn;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label copyrightLabel;
    @FXML private Label inventoryLabel;
    @FXML private CheckBox freeCheckBox;
    @FXML private HBox buttonBar;
    @FXML private Circle activeCircle;

    private ClientApp clientApp;

    public void setClientApp(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }
    public void setTableBooks(ObservableList<LibraryBook> tableBooks)
    {
        bookTable.setItems(tableBooks);
    }
    Circle getActiveCircle()
    {
        return activeCircle;
    }
    HBox getButtonBar()
    {
        return buttonBar;
    }

    @FXML
    private void initialize()
    {
        buttonBar.setDisable(true);
        activeCircle.setFill(Color.RED);
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        copyrightColumn.setCellValueFactory(cellData -> cellData.getValue().copyrightProperty().asObject());
        showBookDetails(null);

        bookTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showBookDetails(newValue));
    }

    private void showBookDetails(LibraryBook book)
    {
        if(book == null)
        {
            titleLabel.setText("");
            authorLabel.setText("");
            copyrightLabel.setText("");
            inventoryLabel.setText("");
            freeCheckBox.setVisible(false);
            freeCheckBox.setSelected(false);
        }
        else
        {
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
            copyrightLabel.setText(String.valueOf(book.getCopyright()));
            inventoryLabel.setText(String.valueOf(book.getInventoryNumber()));
            freeCheckBox.setVisible(true);
            freeCheckBox.setSelected(book.isBookFree());
        }
    }

    @FXML
    private void handleNewBook()
    {
        LibraryBook newBook = new LibraryBook();
        boolean okClicked = clientApp.showBookEditDialog(newBook, "Создание книги");
        if(okClicked)
            clientApp.getLibraryBooks().add(newBook);
        clientApp.getClient().saveBooks(clientApp.getLibraryBooks());
    }

    @FXML
    private void handleEditBook()
    {
        int selectedIndex = bookTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1)
        {
            showAlertNoChoose();
        }
        else
        {
            LibraryBook selectedBook = bookTable.getSelectionModel().getSelectedItem();
            boolean okClicked = clientApp.showBookEditDialog(selectedBook, "Редактирование книги");
            if(okClicked)
            {
                showBookDetails(selectedBook);
            }
        }
        clientApp.getClient().saveBooks(clientApp.getLibraryBooks());
    }

    @FXML
    private void handleDeleteBook()
    {
        int selectedIndex = bookTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1)
        {
            showAlertNoChoose();
        }
        else
        {
            bookTable.getItems().remove(selectedIndex);
            clientApp.getLibraryBooks().clear();
            clientApp.getLibraryBooks().addAll(bookTable.getItems());
        }
        clientApp.getClient().saveBooks(clientApp.getLibraryBooks());
    }

    private void showAlertNoChoose()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText("Книга не выбрана");
        alert.setContentText("Выберите книгу в таблице.");

        alert.showAndWait();
    }
}
