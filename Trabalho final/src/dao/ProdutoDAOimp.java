package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CrudException;
import model.Produto;

public class ProdutoDAOimp implements ProdutoDAO {

    @Override
    public void inserirProduto(Produto p) throws CrudException {
        try {
            String SQL = """
                    INSERT INTO produto VALUES
                    (?, ?, ?)
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, p.getId());
            stm.setString(2, p.getNome());
            stm.setDouble(3, p.getValor());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }    

    @Override
    public void atualizarProduto(Produto p) throws CrudException {
        try {
            String SQL = """
                    UPDATE produto
                    SET nome = ?,
                        valor = ?
                    WHERE id = ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setString(1, p.getNome());
            stm.setDouble(2, p.getValor());
            stm.setInt(3, p.getId());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }      
    @Override
    public void removerProduto(Produto p) throws CrudException {
        try {
            String SQL = """
                    DELETE FROM produto
                    WHERE id = ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, p.getId());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override 
    public boolean produtoNaoUsado(int idProduto) throws CrudException {
        try {
            String SQL = """
                SELECT p.id
                FROM produto p
                LEFT OUTER JOIN cliente_produto cp
                ON p.id = cp.produtoId
                WHERE cp.produtoId IS NULL AND p.id = ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, idProduto);
            ResultSet rs = stm.executeQuery();
            boolean naoUsado = rs.next();
            con.close();
            return naoUsado;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }   
    @Override
    public List<Produto> refreshProdutos() throws CrudException {
        List<Produto> lista = new ArrayList<>();
        try {
            String SQL = """
                    SELECT * FROM produto
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Produto p = new Produto(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setValor(rs.getDouble("valor"));
                lista.add(p);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
        return lista;
    }

    @Override
    public List<Produto> pesquisarProdutoNome(String nome) throws CrudException {
        List<Produto> lista = new ArrayList<>();
        try {
            String SQL = """
                    SELECT * FROM produto WHERE NOME LIKE ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setString(1, "%" + nome + "%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Produto p = new Produto(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setValor(rs.getDouble("valor"));
                lista.add(p);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
        return lista;
    }
    @Override
    public Produto pesquisarProdutoId(int produtoId) throws CrudException {
        try {
            Connection con = Conexao.getInstance().getConnection();
            String SQL = """
                    SELECT * FROM produto
                    WHERE id = ?
                    """;
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, produtoId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Produto p = new Produto(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setValor(rs.getDouble("valor"));
                con.close();
                return p;
            }
            con.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public List<Produto> getCarrinho(int idCliente) throws CrudException {
        try {
            List<Produto> lista = new ArrayList<>();
            String SQL = """
                SELECT p.id, p.nome, p.valor
                FROM produto p
                INNER JOIN cliente_produto CP
                ON p.id = CP.produtoId
                WHERE CP.clienteId = ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, idCliente);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Produto p = new Produto(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setValor(rs.getDouble("valor"));
                lista.add(p);
            }
            con.close();
            return lista;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

}
