package view;
import control.ClienteControl;
import control.ProdutoControl;
import model.Cliente;
import model.Produto;
import model.CrudException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;


public class TelaCliente implements Tela {
    private TextField txtIdCliente = new TextField("");
    private TextField txtNomeCliente = new TextField("");
    private TextField txtTelefoneCliente = new TextField("");
    private TextField txtCpfCliente = new TextField("");
    private TextField txtPesquisarProduto = new TextField("");

    private Label valorTotalCarrinho = new Label("Valor Total: ");

    private TableView<Cliente> clientes = new TableView<>();
    private TableView<Produto> produtosDisponiveis = new TableView<>();
    private TableView<Produto> carrinho = new TableView<>();

    private ClienteControl controlCliente;
    private ProdutoControl controlProduto;

    @Override
    public Pane render() {
        try {
            controlCliente = new ClienteControl();
            controlProduto = new ProdutoControl();
            controlCliente.refresh();
            controlProduto.refresh();
        } catch (CrudException e) {
            alert(AlertType.ERROR, "Erro ao incializar controller(s)");
        }
        BorderPane bpane = new BorderPane();
        GridPane topo = new GridPane();
        GridPane gpane = new GridPane();
        GridPane gpane2 = new GridPane();
        topo.setHgap(150);
        gpane2.setVgap(40);
        gpane2.setHgap(200);
        clientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        produtosDisponiveis.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        carrinho.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAdd = new Button("Cadastrar Cliente");
        btnAdd.setOnAction(e -> {
            if (txtIdCliente.getText().equals("")
            || txtNomeCliente.getText().equals("")
            || txtTelefoneCliente.getText().equals("")
            || txtCpfCliente.getText().equals("")) {
                alert(AlertType.INFORMATION, "Um ou mais campos estao em branco.").showAndWait();
            } else {
                try {
                    controlCliente.adicionar();
                    clientes.refresh();
                } catch (CrudException erro) {
                    alert(AlertType.ERROR, "Erro ao adicionar Cliente");
                }
            }
        });
        Button btnLimpar = new Button("Limpar Campos");
        btnLimpar.setOnAction(e -> {
            txtIdCliente.setText("");
            txtNomeCliente.setText("");
            txtTelefoneCliente.setText("");
            txtCpfCliente.setText("");
            // try {
            //     controlCliente.refresh();
            // } catch (CrudException erro){
            //     alert(AlertType.ERROR, "Erro ao atualizar lista de clientes");
            // }
        });
        txtPesquisarProduto.setOnKeyTyped(e -> {
            try {
                controlProduto.pesquisarProdutoNome(txtPesquisarProduto.getText());
                produtosDisponiveis.refresh();
            } catch (CrudException erro) {
                alert(AlertType.ERROR, "Erro ao pesquisar produtos");
            }
        });
        gpane.add(new Label("ID"), 1, 1);
        gpane.add(txtIdCliente, 2, 1);
        gpane.add(new Label("Nome"), 1, 2);
        gpane.add(txtNomeCliente, 2, 2);
        gpane.add(new Label("Telefone"), 1, 3);
        gpane.add(txtTelefoneCliente, 2, 3);
        gpane.add(new Label("CPF"), 1, 4);
        gpane.add(txtCpfCliente, 2, 4);
        gpane.add(btnAdd, 1, 5);
        gpane.add(btnLimpar, 2, 5);
        gpane2.add(new Label("Pesquisar Produto "), 0, 1);
        gpane2.add(txtPesquisarProduto, 0, 2);
        gpane2.add(valorTotalCarrinho, 2, 0);
        gpane2.add(new Label("Produtos no Carrinho"), 2, 1);
        topo.add(gpane, 0, 0);
        topo.add(gpane2, 1, 0);

        bpane.setTop(topo);
        bpane.setLeft(clientes);
        bpane.setCenter(produtosDisponiveis);
        bpane.setRight(carrinho);

        gerarColunas();
        gerarBindings();

        return bpane;
    }
    public void gerarColunas() {
        /*------------------------------------------- Tabela Carrinho de Produtos -------------------------------------------*/
        TableColumn<Produto, String> colNomeProdutoCarrinho = new TableColumn<>("Produto");
        colNomeProdutoCarrinho.setCellValueFactory(new PropertyValueFactory<Produto, String>("nome"));
        colNomeProdutoCarrinho.prefWidthProperty().bind(carrinho.widthProperty().multiply(0.45));
        TableColumn<Produto, Double> colValorProdutoCarrinho = new TableColumn<>("Valor");
        colValorProdutoCarrinho.setCellValueFactory(new PropertyValueFactory<Produto, Double>("valor"));
        colValorProdutoCarrinho.prefWidthProperty().bind(carrinho.widthProperty().multiply(0.2));

        Callback<TableColumn<Produto, Void>, TableCell<Produto, Void>> callback = 
        new Callback<>() {
            @Override
            public TableCell<Produto, Void> call(TableColumn<Produto, Void> param) {
                TableCell<Produto, Void> tc = new TableCell<>() {
                    final Button btnExcluir = new Button("Excluir");
                    {
                        btnExcluir.setOnAction(e -> {
                            try {
                                Produto p = carrinho.getItems().get(getIndex());
                                controlCliente.excluirProdutoCarrinho(controlCliente.staticCliente.getId(), p.getId());
                                controlProduto.refreshCarrinho(controlCliente.staticCliente.getId());
                                valorTotalCarrinho.setText("Valor Total: "+controlCliente.getValorTotalCarrinho(controlCliente.staticCliente.getId()));
                            } catch (CrudException erro) {
                                alert(AlertType.ERROR, "Erro ao excluir produto");
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
        TableColumn<Produto, Void> colExcluirProdutoCarrinho = new TableColumn<>();
        colExcluirProdutoCarrinho.setCellFactory(callback);

        TableColumn<Produto, Integer> colQtdProduto = new TableColumn<>("Qtd");
        colQtdProduto.prefWidthProperty().bind(carrinho.widthProperty().multiply(0.15));
        colQtdProduto.setCellValueFactory(cellData -> {
            try {
                return new SimpleIntegerProperty(controlCliente.getQtdProdCarrinho(controlCliente.staticCliente.getId(), cellData.getValue().getId())).asObject();
            } catch (CrudException e) {
                e.printStackTrace();
            }
            return null;
        });

        if (carrinho.getColumns().isEmpty()) {
            carrinho.getColumns().addAll(colNomeProdutoCarrinho, colValorProdutoCarrinho, colQtdProduto, colExcluirProdutoCarrinho );
        }
  
        /*------------------------------------------- Tabela Lista de Clientes ----------------------------------------------*/
        
        TableColumn<Cliente, Integer> colIdCliente = new TableColumn<>("ID");
        colIdCliente.prefWidthProperty().bind(clientes.widthProperty().multiply(0.07));
        colIdCliente.setCellValueFactory(new PropertyValueFactory<Cliente, Integer>("id"));
        TableColumn<Cliente, String> colNomeCliente = new TableColumn<>("Nome");
        colNomeCliente.prefWidthProperty().bind(clientes.widthProperty().multiply((0.33)));
        colNomeCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nome"));
        TableColumn<Cliente, String> colTelefoneCliente = new TableColumn<>("Telefone");
        colTelefoneCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefone"));
        TableColumn<Cliente, String> colCpfCliente = new TableColumn<>("CPF");
        colCpfCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("cpf"));

        Callback<TableColumn<Cliente, Void>, TableCell<Cliente, Void>> callback2 = 
            new Callback<>() {
                @Override
                public TableCell<Cliente, Void> call(TableColumn<Cliente, Void> param) {
                    TableCell<Cliente, Void> tc = new TableCell<>() {
                        final Button btnExcluir = new Button("Excluir");
                        {
                            btnExcluir.setOnAction(e -> {
                                try {
                                    Cliente c = clientes.getItems().get(getIndex());
                                    controlCliente.excluir(c);
                                    clientes.refresh();
                                } catch (CrudException erro) {
                                    alert(AlertType.ERROR, "Erro ao excluir cliente");
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
        TableColumn<Cliente, Void> colExcluirCliente = new TableColumn<>();
        colExcluirCliente.setCellFactory(callback2);

        if (clientes.getColumns().isEmpty()) {
            clientes.getColumns().addAll(colIdCliente, colNomeCliente, colTelefoneCliente, colCpfCliente, colExcluirCliente);
        }
        clientes.setItems(controlCliente.getLista());
        clientes.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            controlCliente.entityToBoundary(novo);
            controlCliente.staticCliente = novo;
            try {
                if (controlCliente.staticCliente != null ) {
                    carrinho.setItems(controlProduto.getCarrinho(controlCliente.staticCliente.getId()));
                    valorTotalCarrinho.setText("Valor Total: "+controlCliente.getValorTotalCarrinho(controlCliente.staticCliente.getId()));
                }
            } catch (CrudException e) {
                e.printStackTrace();
            }
        });
        /*------------------------------------------- Tabela Produtos Disponiveis -------------------------------------------*/
        
        TableColumn<Produto, String> colNomeProduto = new TableColumn<>("Produto");
        colNomeProduto.setCellValueFactory(new PropertyValueFactory<Produto, String>("nome"));
        colNomeProduto.prefWidthProperty().bind(produtosDisponiveis.widthProperty().multiply(0.35));
        TableColumn<Produto, Double> colValorProduto = new TableColumn<>("Valor");
        colValorProduto.prefWidthProperty().bind(produtosDisponiveis.widthProperty().multiply(0.15));
        colValorProduto.setCellValueFactory(new PropertyValueFactory<Produto, Double>("valor"));

        
        Callback<TableColumn<Produto, Void>, TableCell<Produto, Void>> callback3 = 
            new Callback<>() {
                @Override
                public TableCell<Produto, Void> call(TableColumn<Produto, Void> param) {
                    TableCell<Produto, Void> tc = new TableCell<>() {
                        final Button btnAddProduto = new Button("+");
                        {
                            btnAddProduto.setOnAction(e -> {
                                try {
                                    if (controlCliente.staticCliente != null) {
                                        Produto p = produtosDisponiveis.getItems().get(getIndex());
                                        controlCliente.adicionarProdutoCarrinho(controlCliente.staticCliente.getId(), p.getId());
                                        controlProduto.refreshCarrinho(controlCliente.staticCliente.getId());
                                        valorTotalCarrinho.setText("Valor Total: "+controlCliente.getValorTotalCarrinho(controlCliente.staticCliente.getId()));
                                    }
                                } catch (CrudException erro) {
                                    alert(AlertType.ERROR, "Erro ao adicionar produto no carrinho");
                                }
                            });
                        }
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btnAddProduto);
                            }
                        }
                    };
                    return tc;
                }
            };
        TableColumn<Produto, Void> colAddCarrinho = new TableColumn<>("Comprar");
        colAddCarrinho.prefWidthProperty().bind(produtosDisponiveis.widthProperty().multiply(0.15));
        colAddCarrinho.setCellFactory(callback3);

        if (produtosDisponiveis.getColumns().isEmpty()) {
            produtosDisponiveis.getColumns().addAll(colNomeProduto, colValorProduto, colAddCarrinho);
        }
        produtosDisponiveis.setItems(controlProduto.getLista());
    }
    public void gerarBindings() {
        Bindings.bindBidirectional(txtIdCliente.textProperty(), controlCliente.idProperty(), (StringConverter) new IntegerStringConverter());
        Bindings.bindBidirectional(txtNomeCliente.textProperty(), controlCliente.nomeProperty());
        Bindings.bindBidirectional(txtTelefoneCliente.textProperty(), controlCliente.telefoneProperty());
        Bindings.bindBidirectional(txtCpfCliente.textProperty(), controlCliente.cpfProperty());
    }
    public Alert alert(AlertType tipo, String texto) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText("Aviso");
        alert.setContentText(texto);
        return alert;
    }
}
