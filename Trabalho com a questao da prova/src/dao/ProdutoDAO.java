package dao;
import java.util.List;

import model.CrudException;
import model.Produto;

public interface ProdutoDAO {
    void inserirProduto(Produto p) throws CrudException;
    void atualizarProduto(Produto p) throws CrudException;
    void removerProduto(Produto p) throws CrudException;
    boolean produtoNaoUsado(int idProduto) throws CrudException;
    List<Produto> refreshProdutos() throws CrudException;
    List<Produto> pesquisarProdutoNome(String nome) throws CrudException;
    List<Produto> getCarrinho(int idCliente) throws CrudException;
    Produto pesquisarProdutoId(int produtoId) throws CrudException;
    ///// Prova poo
    void limparProdutos() throws CrudException;
}