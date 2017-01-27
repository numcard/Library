package client.controller;

import client.Client;
import client.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

public class RootLayoutController
{
    private ClientApp clientApp;
    private Client client;
    private BookOverviewController overviewController;
    public void setClientApp(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }

    @FXML
    private void handleOpenConnection()
    {
        client = clientApp.getClient();
        // check if client connected
        if(client.isConnected())
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

            int status = client.createConnection();
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
                alert.setHeaderText(client.readInputLine());
                alert.showAndWait();
                overviewController.getActiveCircle().setFill(Color.GREEN);
                overviewController.getButtonBar().setDisable(false);
            }
        }
    }

    @FXML
    private void handleSaveBooks()
    {
        client.saveBooks(clientApp.getLibraryBooks());
    }

    @FXML
    private void handleCloseConnection()
    {
        client = clientApp.getClient();
        // check if client connected
        if(client.isConnected())
        {
            client.closeConnection();
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