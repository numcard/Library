package client.controller;

import client.interfaces.ClientInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lib.model.LibraryBook;

public class BookEditController
{
    private LibraryBook libraryBook;
    private boolean okClicked;
    private Stage dialogStage;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField numberOfPagesField;
    @FXML private TextField copyrightField;
    @FXML private TextField inventoryNumberField;
    @FXML private CheckBox freeCheckBox;
    private ClientInterface client;
    private boolean editStatus = false;

    public void setLibraryBook(LibraryBook libraryBook)
    {
        this.libraryBook = libraryBook;
        titleField.setText(libraryBook.getTitle());
        authorField.setText(libraryBook.getAuthor());
        numberOfPagesField.setText(String.valueOf(libraryBook.getNumberOfPages()));
        copyrightField.setText(String.valueOf(libraryBook.getCopyright()));
        inventoryNumberField.setText(String.valueOf(libraryBook.getInventoryNumber()));
        freeCheckBox.setSelected(libraryBook.isBookFree());
        if(editStatus)
            inventoryNumberField.setDisable(true);
        else
            inventoryNumberField.setDisable(false);
    }
    public boolean isOkClicked()
    {
        return okClicked;
    }
    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }
    public void setClient(ClientInterface client)
    {
        this.client = client;
    }
    public void setEditStatus(boolean editStatus)
    {
        this.editStatus = editStatus;
    }

    @FXML
    private void handleOk()
    {
        if(isInputValid())
        {
            libraryBook.setTitle(titleField.getText());
            libraryBook.setAuthor(authorField.getText());
            libraryBook.setNumberOfPages(Integer.parseInt(numberOfPagesField.getText()));
            libraryBook.setCopyright(Integer.parseInt(copyrightField.getText()));
            libraryBook.setInventoryNumber(Integer.parseInt(inventoryNumberField.getText()));
            libraryBook.setBookFree(freeCheckBox.isSelected());
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel()
    {
        dialogStage.close();
    }

    private boolean isInputValid()
    {
        String errorMessage = "";

        if(titleField.getText() == null || titleField.getText().length() == 0)
        {
            errorMessage += "Название книги не указан!\n";
        }
        if(authorField.getText() == null || authorField.getText().length() == 0)
        {
            errorMessage += "Автор книги не указан!\n";
        }
        if(numberOfPagesField.getText() == null || Integer.parseInt(numberOfPagesField.getText()) <= 1)
        {
            errorMessage += "Количество страниц указано неверно!\n";
        }
        if(copyrightField.getText() == null || Integer.parseInt(copyrightField.getText()) <= 0)
        {
            errorMessage += "Год издания указан не верно!\n";
        }
        if(inventoryNumberField.getText() == null)
        {
            errorMessage += "Инвентарный номер указан не верно!\n";
        }
        try
        {
            int number = Integer.parseInt(inventoryNumberField.getText());
            if(number  <= 0)
            {
                errorMessage += "Инвентарный номер указан не верно!\n";
            }
            if(!editStatus)
                switch(client.checkInventoryNumber(number))
                {
                    case -1:
                        errorMessage += "Ошибка сервера...";
                        break;
                    case 1:
                        errorMessage += "Инвентарный номер занят";
                        break;
                }
        }
        catch(NumberFormatException e)
        {
            errorMessage += "Инвентарный номер имеет неверный формат!\n";
        }

        if(errorMessage.length() == 0)
        {
            return true;
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Невалидные поля");
            alert.setHeaderText("Исправьте невалидные поля");
            alert.setContentText(errorMessage);

            alert.showAndWait();
            return false;
        }
    }
}
