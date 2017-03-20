package client;

import client.controller.BookEditController;
import client.controller.BookOverviewController;
import client.controller.RootLayoutController;
import client.interfaces.ClientInterface;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lib.model.LibraryBook;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClientApp extends Application
{
    private Stage primaryStage;                                                                     // Сцена приложения
    private BorderPane rootLayout;                                                                  // Корневой макет
    private final ObservableList<LibraryBook> libraryBooks = FXCollections.observableArrayList();   // Данные приложения
    private final ClientInterface client = new Client();                                                     // Модель клиента
    private BookOverviewController bookOverviewController;

    public ObservableList<LibraryBook> getLibraryBooks()
    {
        return libraryBooks;
    }
    public void setLibraryBooks(List<LibraryBook> libraryBooks)
    {
        this.libraryBooks.clear();
        this.libraryBooks.addAll(libraryBooks);
    }
    public ClientInterface getClient()
    {
        return client;
    }
    public BookOverviewController getBookOverviewController()
    {
        return bookOverviewController;
    }

    // Точка входа
    public static void main(String[] args)
    {
        launch(args);
    }

    public void updateLibraryBooks()
    {
        if(client.getConnection().isConnected())
        {
            List<LibraryBook> books = client.getConnection().downloadBooks();
            setLibraryBooks(books);
        }
    }

    @Override
    public void start(Stage primaryStage)
    {
        File iconFile = new File("avdeev/src/client/resource/client-icon.png");
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Электронная библиотека");

        // Устанавливаем иконку приложения.
        this.primaryStage.getIcons().add(new Image("file:" + iconFile));

        initRootLayout();
        showBookOverview();
    }

    @Override
    public void stop()
    {
        if(client.getConnection().isConnected())
        {
            client.saveBooks(libraryBooks);
            client.getConnection().closeConnection();
        }
    }

    private void initRootLayout()
    {
        try
        {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Даём контроллеру доступ к главному приложению.
            RootLayoutController controller = loader.getController();
            controller.setClientApp(this);
            primaryStage.show();
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
    }

    private void showBookOverview()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/BookOverview.fxml"));
            AnchorPane lotOverviewPage = loader.load();

            rootLayout.setCenter(lotOverviewPage);
            bookOverviewController = loader.getController();
            bookOverviewController.setClientApp(this);
            bookOverviewController.setTableBooks(libraryBooks);
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
        }
    }

    public boolean showBookEditDialog(LibraryBook libraryBook, String title, boolean editStatus)
    {
        try
        {
            // check status to edit data on server side
            if(client.getConnection().checkStatus())
            {
                client.getConnection().blockedStatus();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ClientApp.class.getResource("view/BookEditDialog.fxml"));
                AnchorPane bookEditDialogPage = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle(title);
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(bookEditDialogPage);
                dialogStage.setScene(scene);

                BookEditController controller = loader.getController();
                controller.setEditStatus(editStatus);
                controller.setLibraryBook(libraryBook);
                controller.setDialogStage(dialogStage);
                controller.setClient(client);

                dialogStage.showAndWait();

                client.getConnection().freeStatus();
                return controller.isOkClicked();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Внимание");
                alert.setHeaderText("Операция временно недоступна");
                alert.setContentText("Операция занята другим пользователем");
                alert.showAndWait();
                return false;
            }
        }
        catch(IOException e)
        {
            ClientException.Throw(e);
            return false;
        }
    }
}
