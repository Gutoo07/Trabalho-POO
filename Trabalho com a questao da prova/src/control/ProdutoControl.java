package control;
import model.Produto;

import java.util.Optional;

import dao.ProdutoDAO;
import dao.ProdutoDAOimp;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.CrudException;

public class ProdutoControl {
    private ObservableList<Produto> lista = FXCollections.observableArrayList();
    private ObservableList<Produto> carrinho = FXCollections.observableArrayList();
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nome = new SimpleStringProperty();
    private DoubleProperty valor = new SimpleDoubleProperty();
    public static Produto staticProduto;

    private ProdutoDAO produtoDAO;

    public ProdutoControl() throws CrudException {
        id.set(0);
        nome.set("");
        valor.set(0.0);
        produtoDAO = new ProdutoDAOimp();
    }
    public void entityToBoundary(Produto p) {
        if (p != null) {
            this.id.set(p.getId());
            this.nome.set(p.getNome());
            this.valor.set(p.getValor());
        }
    }
    public void refresh() throws CrudException {
        lista.clear();
        lista.addAll(produtoDAO.refreshProdutos());
    }
    public void refreshCarrinho(int idCliente) throws CrudException {
        carrinho.clear();
        carrinho.addAll(produtoDAO.getCarrinho(idCliente));
    }
    public void adicionar() throws CrudException {
        int idProduto = this.id.get();
        Produto p = new Produto(idProduto);
        p.setNome(this.nome.get());
        p.setValor(this.valor.get());

        if (produtoDAO.pesquisarProdutoId(idProduto) != null) {
            Optional<ButtonType> opcao = alert(AlertType.CONFIRMATION,
            "O ID inserido ja existe. Deseja sobrescrever os dados desse Produto?").showAndWait();
            if (opcao.isPresent() && opcao.get() == ButtonType.OK) {
                if (produtoDAO.produtoNaoUsado(idProduto)) {
                    produtoDAO.atualizarProduto(p);
                    refresh();
                } else {
                    alert(AlertType.INFORMATION, "Impossivel sobrescrever: este produto esta no carrinho de compras de alguem.").show();
                }
            }
        } else {
            produtoDAO.inserirProduto(p);
            refresh();
        }
    }
    public void excluir(Produto p) throws CrudException {
        if (produtoDAO.produtoNaoUsado(p.getId())) {
            lista.remove(p);
            produtoDAO.removerProduto(p);
        } else {
            alert(AlertType.INFORMATION, "Este produto esta no carrinho de compras de alguem.").show();;
        }
    }
    public void limparProdutos() throws CrudException {
        produtoDAO.limparProdutos();
        refresh();
    }
    public void pesquisarProdutoNome(String nome) throws CrudException {
        lista.clear();
        lista.addAll(produtoDAO.pesquisarProdutoNome(nome));
    }
    public Alert alert(AlertType tipo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText("Aviso");
        alerta.setContentText(texto);
        return alerta;
    }
    public ObservableList getLista() {
        return this.lista;
    }
    public ObservableList getCarrinho(int idCliente) throws CrudException {
        refreshCarrinho(idCliente);
        return this.carrinho;
    }
    public IntegerProperty idProperty() {
        return this.id;
    }
    public StringProperty nomeProperty() {
        return this.nome;
    }
    public DoubleProperty valorProperty() {
        return this.valor;
    }
}
