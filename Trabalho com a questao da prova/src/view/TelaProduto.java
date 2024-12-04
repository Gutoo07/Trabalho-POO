package view;
import control.ProdutoControl;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.CrudException;
import model.Produto;

public class TelaProduto implements Tela {
    private TextField txtId = new TextField("");
    private TextField txtNome = new TextField("");
    private TextField txtValor = new TextField();

    TableView<Produto> produtos = new TableView<>();

    ProdutoControl controlProduto;

    @Override
    public Pane render() {

        try {
            controlProduto = new ProdutoControl();
            controlProduto.refresh();
        } catch (CrudException e) {
            alert(AlertType.ERROR, "Erro ao inicializar ProdutoControl");
        }
        BorderPane bpane = new BorderPane();
        GridPane gpane = new GridPane();

        txtNome.setOnKeyTyped(e -> {
            try {
                controlProduto.pesquisarProdutoNome(txtNome.getText());
            } catch (CrudException erro) {
                alert(AlertType.ERROR, "Erro ao pesquisar produtos");
            }
            produtos.refresh();
        });
        Button btnAdd = new Button("Cadastrar Produto");
        btnAdd.setOnAction(e -> {
            if (this.txtId.getText().equals("")
            || this.txtNome.getText().equals("")
            || this.txtValor.getText().equals("")) {
                alert(AlertType.INFORMATION, "Um ou mais campos estao em branco.").showAndWait();
            } else {
                try {
                    controlProduto.adicionar();
                    produtos.refresh();
                } catch (CrudException erro) {
                    alert(AlertType.ERROR, "Erro ao adicionar Produto");
                }
            }
        });
        Button btnLimpar = new Button("Limpar Campos");
        btnLimpar.setOnAction(e -> {
            txtId.setText("");
            txtNome.setText("");
            txtValor.setText("");
            try {
                controlProduto.refresh();
            } catch (CrudException erro) {
                alert(AlertType.ERROR, "Erro ao atualizar lista de produtos");
            }
        });
        Button btnLimparProdutos = new Button("Limpar Produtos");
        btnLimparProdutos.setOnAction(e -> {
            try {
                controlProduto.limparProdutos();
                produtos.refresh();
            } catch (CrudException e1) {
                e1.printStackTrace();
            }
        });

        gpane.setHgap(10);
        gpane.setVgap(10);
        gpane.add(new Label("ID"), 1, 1);
        gpane.add(txtId, 2, 1);
        gpane.add(new Label("Nome"), 1, 2);
        gpane.add(txtNome, 2, 2);
        gpane.add(new Label("Valor"), 1, 3);
        gpane.add(txtValor, 2, 3);
        gpane.add(btnAdd, 1, 4);
        gpane.add(btnLimpar, 2, 4);
        gpane.add(btnLimparProdutos, 3, 4);
        bpane.setTop(gpane);
        bpane.setCenter(produtos);

        gerarColunas();
        gerarBindings();

        try {
            controlProduto.refresh();
        } catch (CrudException e) {
            alert(AlertType.ERROR, "Erro ao atualizar produtos");
        }

        return bpane;
    }
    public void gerarColunas() {
        TableColumn<Produto, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<Produto, Integer>("id"));
        TableColumn<Produto, String> colNome = new TableColumn<>("Nome");
        colNome.prefWidthProperty().bind(produtos.widthProperty().multiply(0.22));
        colNome.setCellValueFactory(new PropertyValueFactory<Produto, String>("nome"));
        TableColumn<Produto, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<Produto, Double>("valor"));

        Callback<TableColumn<Produto, Void>, TableCell<Produto, Void>> callback =
            new Callback<>() {
                @Override
                public TableCell<Produto, Void> call(TableColumn<Produto, Void> param) {
                    TableCell<Produto, Void> tc = new TableCell<>() {
                        final Button btnExcluir = new Button("Excluir");
                        {
                            btnExcluir.setOnAction(e -> {
                                try {
                                    Produto p = produtos.getItems().get(getIndex());
                                    controlProduto.excluir(p);
                                    produtos.refresh();
                                } catch (CrudException erro) {
                                    alert(AlertType.ERROR, "Erro ao excluir Produto");
                                }
                            });
                        }
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btnExcluir);
                            }
                        }
                    };
                    return tc;
                }
            };
        
        TableColumn<Produto, Void> colExcluir = new TableColumn<>("");
        colExcluir.setCellFactory(callback);

        
        Callback<TableColumn<Produto, Void>, TableCell<Produto, Void>> callback2 =
            new Callback<>() {
                @Override
                public TableCell<Produto, Void> call(TableColumn<Produto, Void> param) {
                    TableCell<Produto, Void> tc = new TableCell<>() {
                        final Button btnExcluir = new Button("Prova - Excluir Produto");
                        {
                            btnExcluir.setOnAction(e -> {
                                try {
                                    Produto p = produtos.getItems().get(getIndex());
                                    controlProduto.excluir(p);
                                    produtos.refresh();
                                } catch (CrudException erro) {
                                    alert(AlertType.ERROR, "Erro ao excluir Produto");
                                }
                            });
                        }
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btnExcluir);
                            }
                        }
                    };
                    return tc;
                }
            };
        
        TableColumn<Produto, Void> colExcluir2 = new TableColumn<>("Prova Excluir");
        colExcluir2.prefWidthProperty().bind(produtos.widthProperty().multiply((0.2)));
        colExcluir2.setCellFactory(callback2);

        if (produtos.getColumns().isEmpty()) {
            produtos.getColumns().addAll(colId, colNome, colValor, colExcluir2);
        }
        produtos.setItems(controlProduto.getLista());

        produtos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            controlProduto.entityToBoundary(novo);
        });
    }
    
    public void gerarBindings() {
        Bindings.bindBidirectional(txtId.textProperty(), controlProduto.idProperty(), (StringConverter) new IntegerStringConverter());
        Bindings.bindBidirectional(txtNome.textProperty(), controlProduto.nomeProperty());
        Bindings.bindBidirectional(txtValor.textProperty(), controlProduto.valorProperty(), (StringConverter) new DoubleStringConverter());
    }
    public Alert alert(AlertType tipo, String texto) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText("Aviso");
        alert.setContentText(texto);
        return alert;
    }
}
