package client.controller;

import client.ClientApp;
import client.interfaces.ClientInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.IOException;

public class RootLayoutController
{
    private ClientApp clientApp;
    private BookOverviewController overviewController;
    public void setClientApp(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }

    @FXML
    private void handleOpenConnection() throws IOException
    {
        ClientInterface client = clientApp.getClient();
        // check if client connected
        if(client.getConnection().isConnected())
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Соединение с сервером");
            alert.setHeaderText("Внимание");
            alert.setContentText("Соединение уже установлено!");
            alert.showAndWait();
        }
        else
        {
            overviewController = clientApp.getBookOverviewController();
            overviewController.getActiveCircle().setFill(Color.RED);    // default value

            int status = client.getConnection().createConnection();
            if(status == -1)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Соединение с сервером");
                alert.setHeaderText("Сервер недоступен");
                alert.setContentText("Попробуйте переподключиться!");
                alert.showAndWait();
            }
            else if(status == 1)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Соединение с сервером");
                alert.setHeaderText("Неизвестная ошибка");
                alert.setContentText("Возникла неизвестная ошибка при подключении к серверу!");
                alert.showAndWait();
            }
            else if(status == 0)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Сервер");
                alert.setHeaderText(client.getConnection().readInputLine());
                alert.showAndWait();
                // change indicator color
                overviewController.getActiveCircle().setFill(Color.GREEN);
                overviewController.getButtonBar().setDisable(false);
                // download books from server
                if(client.getConnection().checkBookIsExisted())
                    clientApp.setLibraryBooks(client.getConnection().downloadBooks());
            }
        }
    }

    @FXML
    private void handleUpdateData()
    {
        clientApp.updateLibraryBooks();
    }

    @FXML
    private void handleSaveBooks()
    {
        clientApp.getClient().saveBooks(clientApp.getLibraryBooks());
    }

    @FXML
    private void handleCloseConnection()
    {
        ClientInterface client = clientApp.getClient();
        // check if client connected
        if(client.getConnection().isConnected())
        {
            client.getConnection().closeConnection();
            overviewController = clientApp.getBookOverviewController();
            overviewController.getActiveCircle().setFill(Color.RED);
            overviewController.getButtonBar().setDisable(true);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Соединение с сервером");
            alert.setHeaderText("Внимание");
            alert.setContentText("Вы не подключены к серверу!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAbout()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Информация");
        alert.setContentText("Автор: Авдеев А.О.");
        alert.showAndWait();
    }
}