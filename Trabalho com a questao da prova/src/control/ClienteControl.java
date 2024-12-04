package control;
import model.Cliente;
import model.CrudException;
import model.Produto;

import java.util.Optional;

import dao.ClienteDAO;
import dao.ClienteDAOimp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class ClienteControl {
    private ObservableList<Cliente> lista = FXCollections.observableArrayList();
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nome = new SimpleStringProperty();
    private StringProperty telefone = new SimpleStringProperty();
    private StringProperty cpf = new SimpleStringProperty();
    public static Cliente staticCliente;

    private ClienteDAO clienteDAO;

    public ClienteControl() throws CrudException {
        id.set(0);
        nome.set("");
        telefone.set("");
        cpf.set("");
        clienteDAO = new ClienteDAOimp();
    }

    public void entityToBoundary(Cliente c) {
        if (c != null) {
            this.id.set(c.getId());
            this.nome.set(c.getNome());
            this.telefone.set(c.getTelefone());
            this.cpf.set(c.getCpf());
        }
    }
    public void adicionar() throws CrudException {
        int idCliente = this.id.get();
        for (Cliente aux : lista) {
            if (aux.getId() == idCliente) {
                Optional<ButtonType> opcao = alert(AlertType.CONFIRMATION,
                "O ID inserido ja existe. Deseja sobrescrever os dados desse Cliente?").showAndWait();
                if (opcao.isPresent() && opcao.get() == ButtonType.OK) {
                    aux.setNome(this.nome.get());
                    aux.setTelefone(this.telefone.get());
                    aux.setCpf(this.cpf.get());
                    clienteDAO.atualizarCliente(aux);
                }
                return;
            }
        }
        Cliente c = new Cliente(idCliente);
        c.setNome(this.nome.get());
        c.setTelefone(this.telefone.get());
        c.setCpf(this.cpf.get());
        lista.add(c);
        clienteDAO.inserirCliente(c);
    }
    public void adicionarProdutoCarrinho(int idCliente, int idProduto) throws CrudException {
        clienteDAO.adicionarProdutoCarrinho(idCliente, idProduto);
    }
    public void excluirProdutoCarrinho(int idCliente, int idProduto) throws CrudException {
        clienteDAO.excluirProdutoCarrinho(idCliente, idProduto);
    }
    public int getQtdProdCarrinho(int idCliente, int idProduto) throws CrudException {
        return(clienteDAO.getQtdProdCarrinho(idCliente, idProduto));
    }
    public double getValorTotalCarrinho(int idCliente) throws CrudException {
        return(clienteDAO.getValorTotalCarrinho(idCliente));
    }
    public void excluir(Cliente c) throws CrudException {
        if (clienteDAO.clienteInativo(c.getId())) {
            lista.remove(c);
            clienteDAO.removerCliente(c);
        } else {
            alert(AlertType.INFORMATION,
            "Este cliente ainda possui produtos em seu carrinho de compras.").show();
        }
    }
    public void refresh() throws CrudException {
        lista.clear();
        lista.addAll(clienteDAO.refreshClientes());
    }
    public void pesquisarClienteNome() throws CrudException {
        lista.clear();
        lista.addAll(clienteDAO.pesquisarClienteNome(this.nome.get()));
    }
    public Alert alert(AlertType tipo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setHeaderText("Aviso");
        alerta.setContentText(texto);
        return alerta;
    }
    public IntegerProperty idProperty() {
        return this.id;
    }
    public StringProperty nomeProperty() {
        return this.nome;
    }
    public StringProperty telefoneProperty() {
        return this.telefone;
    }
    public StringProperty cpfProperty() {
        return this.cpf;
    }
    public ObservableList getLista() {
        return this.lista;
    }
}
