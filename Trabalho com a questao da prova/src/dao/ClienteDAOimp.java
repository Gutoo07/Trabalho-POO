package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Cliente;
import model.CrudException;

public class ClienteDAOimp implements ClienteDAO {

    @Override
    public void inserirCliente(Cliente c) throws CrudException {
        try {
            String SQL = """
                INSERT INTO cliente VALUES  
                (?, ?, ?, ?)
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, c.getId());
            stm.setString(2, c.getNome());
            stm.setString(3, c.getTelefone());
            stm.setString(4, c.getCpf());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public void adicionarProdutoCarrinho(int idCliente, int idProduto) throws CrudException {
        try {
            String SQL = """
                IF EXISTS (SELECT 1 FROM cliente_produto WHERE clienteId = ? AND produtoId = ?)
                BEGIN
                UPDATE cliente_produto
                    SET qtd = qtd + 1
                    WHERE clienteId = ? AND produtoId = ?
                END
                ELSE
                BEGIN
                    INSERT INTO cliente_produto (clienteId, produtoId, qtd) VALUES
                    (?, ?, 1)
                END
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, idCliente);
            stm.setInt(2, idProduto);
            stm.setInt(3, idCliente);
            stm.setInt(4, idProduto);
            stm.setInt(5, idCliente);
            stm.setInt(6, idProduto);
            stm.executeUpdate();
            con.close();
        } catch (SQLException e){
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public void excluirProdutoCarrinho(int idCliente, int idProduto) throws CrudException {
        try {
            String SQL = """
                IF (SELECT qtd FROM cliente_produto WHERE clienteId = ? AND produtoId = ?) > 1
                BEGIN
                    UPDATE cliente_produto
                    SET	qtd = qtd - 1
                    WHERE clienteId = ? AND produtoId = ?
                END
                ELSE
                BEGIN
                    DELETE FROM cliente_produto
                    WHERE clienteId = ? AND produtoId = ?
                END
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, idCliente);
            stm.setInt(2, idProduto);
            stm.setInt(3, idCliente);
            stm.setInt(4, idProduto);
            stm.setInt(5, idCliente);
            stm.setInt(6, idProduto);
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public int getQtdProdCarrinho(int clienteId, int produtoId) throws CrudException {
        try {
            int qtd = 0;
            String SQL = """
                    SELECT qtd FROM cliente_produto
                    WHERE clienteId = ? AND produtoId = ?
                    """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, clienteId);
            stm.setInt(2, produtoId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                qtd = rs.getInt("qtd");
            }
            con.close();
            return qtd;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override 
    public double getValorTotalCarrinho(int clienteId) throws CrudException {
        try {
            Connection con = Conexao.getInstance().getConnection();
            Double valorTotal = 0.0;
            String SQL = """
                    SELECT sum(cp.qtd * p.valor) AS valor_carrinho
                    FROM cliente c
                    INNER JOIN cliente_produto cp
                    ON c.id = cp.clienteId
                    INNER JOIN produto p
                    ON cp.produtoId = p.id
                    WHERE c.id = ?
                    """;
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, clienteId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                valorTotal = rs.getDouble("valor_carrinho");
            }
            con.close();
            return valorTotal;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public void atualizarCliente(Cliente c) throws CrudException {
        try {
            String SQL = """
                UPDATE cliente
                    SET nome = ?,
                        telefone = ?,
                        cpf = ?
                WHERE id = ?
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setString(1, c.getNome());
            stm.setString(2, c.getTelefone());
            stm.setString(3, c.getCpf());
            stm.setInt(4, c.getId());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    @Override
    public void removerCliente(Cliente c) throws CrudException {
        try {
            String SQL = """
                DELETE FROM cliente
                WHERE id = ?
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, c.getId());
            stm.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    @Override
    public List<Cliente> refreshClientes() throws CrudException {
        try {
            List<Cliente> lista = new ArrayList<>();
            String SQL = """
                SELECT * FROM cliente
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                c.setCpf(rs.getString("cpf"));
                lista.add(c);
            }
            con.close();
            return lista;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public List<Cliente> pesquisarClienteNome(String nome) throws CrudException {
        try {
            List<Cliente> lista = new ArrayList<>();
            String SQL = """
                SELECT * FROM cliente
                WHERE nome LIKE ?
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setString(1, "%"+nome+"%");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                c.setCpf(rs.getString("cpf"));
                lista.add(c);
            }
            con.close();
            return lista;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    @Override
    public boolean clienteInativo(int idCliente) throws CrudException{ 
        try {
            String SQL = """
                SELECT c.id
                FROM cliente c
                LEFT OUTER JOIN cliente_produto cp
                ON c.id = cp.clienteId
                WHERE cp.clienteId IS NULL AND c.id = ?
                """;
            Connection con = Conexao.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement(SQL);
            stm.setInt(1, idCliente);
            ResultSet rs = stm.executeQuery();
            boolean inativo = rs.next();
            con.close();
            return inativo;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

}