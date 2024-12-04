package view;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.CrudException;

public class TelaInicial extends Application {

    public static Map<String, Tela> telas = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        telas.put("produto", new TelaProduto());
        telas.put("cliente", new TelaCliente());
        // telas.put

        MenuBar menuBar = new MenuBar();
        Menu menuTelas = new Menu("Telas");

        MenuItem menuProduto = new MenuItem("Produto");
        MenuItem menuCliente = new MenuItem("Cliente");

        menuTelas.getItems().addAll(menuProduto, menuCliente);
        
        menuBar.getMenus().add(menuTelas);

        BorderPane bpane = new BorderPane();
        bpane.setTop(menuBar);

        menuProduto.setOnAction(e -> bpane.setCenter(telas.get("produto").render()));
        menuCliente.setOnAction(e -> bpane.setCenter(telas.get("cliente").render()));

        Scene cena = new Scene(bpane, 1200, 800);
        stage.setScene(cena);
        stage.setTitle("Trabalho de POO");
        stage.show();
    }
    public static void main(String[] args) throws CrudException {
        Application.launch(TelaInicial.class, args);
    }
}
