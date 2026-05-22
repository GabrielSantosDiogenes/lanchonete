import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BancoDeDados {

    // Dados de conexão com o MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/lanchonete";
    private static final String USUARIO = "root";
    private static final String SENHA = "tinCTrom"; 

    // Abre a conexão com o banco
    public static Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conn;
        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }

    // ===================== PRODUTOS =====================

    // Salva um produto novo no banco
    public static void salvarProduto(String nome, double preco) {
        try {
            Connection conn = conectar();
            String sql = "INSERT INTO produtos (nome, preco) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setDouble(2, preco);
            ps.executeUpdate();
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    // Busca todos os produtos do banco
    public static ArrayList<Produto> listarProdutos() {
        ArrayList<Produto> lista = new ArrayList<>();
        try {
            Connection conn = conectar();
            String sql = "SELECT * FROM produtos";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                double preco = rs.getDouble("preco");
                lista.add(new Produto(id, nome, preco));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    // ===================== PEDIDOS =====================

    // Salva um pedido e seus itens no banco
    public static void salvarPedido(Pedido pedido) {
        try {
            Connection conn = conectar();

            // Salva o pedido
            String sql = "INSERT INTO pedidos (nome_cliente, total) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getNomeCliente());
            ps.setDouble(2, pedido.getTotal());
            ps.executeUpdate();

            // Pega o id gerado automaticamente para o pedido
            ResultSet rs = ps.getGeneratedKeys();
            int pedidoId = 0;
            if (rs.next()) {
                pedidoId = rs.getInt(1);
            }

            // Salva cada item do pedido
            for (ItemPedido item : pedido.getItens()) {
                String sqlItem = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)";
                PreparedStatement psItem = conn.prepareStatement(sqlItem);
                psItem.setInt(1, pedidoId);
                psItem.setInt(2, item.getProdutoId());
                psItem.setInt(3, item.getQuantidade());
                psItem.setDouble(4, item.getSubtotal());
                psItem.executeUpdate();
            }

            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    // Busca todos os pedidos do banco
    public static ArrayList<String> listarPedidos() {
        ArrayList<String> lista = new ArrayList<>();
        try {
            Connection conn = conectar();
            String sql = "SELECT * FROM pedidos";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome_cliente");
                double total = rs.getDouble("total");
                lista.add("Pedido #" + id + " - " + nome + " - Total: R$ " + String.format("%.2f", total));
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }
        return lista;
    }
}