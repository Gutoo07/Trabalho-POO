package dao;

import java.util.List;

import model.Cliente;
import model.CrudException;

public interface ClienteDAO {
    void inserirCliente(Cliente c) throws CrudException;
    void atualizarCliente(Cliente c) throws CrudException;
    void removerCliente(Cliente c) throws CrudException;
    List<Cliente> refreshClientes() throws CrudException;
    List<Cliente> pesquisarClienteNome(String nome) throws CrudException;
    boolean clienteInativo(int idCliente) throws CrudException;
    void adicionarProdutoCarrinho(int idCliente, int idProduto) throws CrudException;
    void excluirProdutoCarrinho(int idCliente, int idProduto) throws CrudException;
    int getQtdProdCarrinho(int clienteId, int produtoId) throws CrudException;
    double getValorTotalCarrinho(int clienteId) throws CrudException;
}
