import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Main {

    static Pedido pedidoAtual = null;

    public static void main(String[] args) {

        // Cadastra os produtos padrão se o banco estiver vazio
        if (BancoDeDados.listarProdutos().isEmpty()) {
            BancoDeDados.salvarProduto("Batata Frita", 19.90);
            BancoDeDados.salvarProduto("X-Bacon", 29.90);
            BancoDeDados.salvarProduto("X-Salada", 29.90);
            BancoDeDados.salvarProduto("X-Tudo", 39.90);
            BancoDeDados.salvarProduto("Refrigerante", 9.90);
            BancoDeDados.salvarProduto("Combo X-Tudo", 54.90);
        }

        // Menu principal
        while (true) {
            String[] opcoes = {
                "Novo Pedido",
                "Cadastrar Produto",
                "Ver Pedidos",
                "Sair"
            };

            int escolha = JOptionPane.showOptionDialog(
                null,
                "Bem-vindo à Lanchonete!\nO que deseja fazer?",
                "Menu Principal",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
            );

            if (escolha == 0) {
                novoPedido();
            } else if (escolha == 1) {
                cadastrarProduto();
            } else if (escolha == 2) {
                verPedidos();
            } else {
                // Fechou a janela ou clicou Sair
                JOptionPane.showMessageDialog(null, "Até logo!");
                break;
            }
        }
    }

    // ===================== NOVO PEDIDO =====================
    static void novoPedido() {
        String nomeCliente = JOptionPane.showInputDialog("Digite o nome do cliente:");
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) return;

        pedidoAtual = new Pedido(nomeCliente);

        while (true) {
            // Monta o menu de produtos
            ArrayList<Produto> produtos = BancoDeDados.listarProdutos();
            String[] opcoesProdutos = new String[produtos.size() + 2];

            for (int i = 0; i < produtos.size(); i++) {
                opcoesProdutos[i] = produtos.get(i).toString();
            }
            opcoesProdutos[produtos.size()] = "Remover Item";
            opcoesProdutos[produtos.size() + 1] = "Finalizar Pedido";

            int escolha = JOptionPane.showOptionDialog(
                null,
                "Cliente: " + nomeCliente + "\nTotal atual: R$ " + String.format("%.2f", pedidoAtual.getTotal()) + "\n\nEscolha um produto:",
                "Novo Pedido",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesProdutos,
                opcoesProdutos[0]
            );

            if (escolha == -1 || escolha == produtos.size() + 1) {
                // Finalizar pedido
                finalizarPedido();
                break;
            } else if (escolha == produtos.size()) {
                // Remover item
                removerItem();
            } else {
                // Adicionar produto ao pedido
                Produto produtoEscolhido = produtos.get(escolha);
                String qtdStr = JOptionPane.showInputDialog("Quantidade de " + produtoEscolhido.getNome() + ":");
                if (qtdStr == null || qtdStr.trim().isEmpty()) continue;
                int quantidade = Integer.parseInt(qtdStr);
                ItemPedido item = new ItemPedido(produtoEscolhido.getId(), produtoEscolhido.getNome(), produtoEscolhido.getPreco(), quantidade);
                pedidoAtual.adicionarItem(item);
                JOptionPane.showMessageDialog(null, quantidade + "x " + produtoEscolhido.getNome() + " adicionado!");
            }
        }
    }

    // ===================== FINALIZAR PEDIDO =====================
    static void finalizarPedido() {
        if (pedidoAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum item no pedido!");
            return;
        }

        // Monta o extrato
        StringBuilder extrato = new StringBuilder();
        extrato.append("===== EXTRATO DO PEDIDO =====\n");
        extrato.append("Cliente: ").append(pedidoAtual.getNomeCliente()).append("\n\n");

        for (ItemPedido item : pedidoAtual.getItens()) {
            extrato.append(item.toString()).append("\n");
        }

        double total = pedidoAtual.getTotal();

            // Desconto de 5% para compras acima de R$70
            if (total > 70) {
                double desconto = total * 0.05;
                total -= desconto;
                extrato.append("\nDesconto 5%: -R$ ").append(String.format("%.2f", desconto));
                pedidoAtual.aplicarDesconto(desconto);
        }

        // Taxa de entrega
        String entregaStr = JOptionPane.showInputDialog("Distância para entrega em km (0 para retirada):");
        if (entregaStr != null && !entregaStr.trim().isEmpty()) {
            int km = Integer.parseInt(entregaStr);
            double taxa = 0;
            if (km > 0 && km <= 5) taxa = 5;
            else if (km > 5 && km <= 10) taxa = 10;
            else if (km > 10) taxa = 15;

            if (taxa > 0) {
                total += taxa;
                extrato.append("\nTaxa de entrega: R$ ").append(String.format("%.2f", taxa));
                pedidoAtual.adicionarTaxa(taxa);
            }
        }

        extrato.append("\n\nTOTAL: R$ ").append(String.format("%.2f", total));
        extrato.append("\n=============================");

        JOptionPane.showMessageDialog(null, extrato.toString());

        BancoDeDados.salvarPedido(pedidoAtual);
        JOptionPane.showMessageDialog(null, "Pedido salvo com sucesso!");
    }

    // ===================== REMOVER ITEM =====================
    static void removerItem() {
        if (pedidoAtual.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum item para remover!");
            return;
        }

        ArrayList<ItemPedido> itens = pedidoAtual.getItens();
        String[] opcoesItens = new String[itens.size()];
        for (int i = 0; i < itens.size(); i++) {
            opcoesItens[i] = itens.get(i).toString();
        }

        int escolha = JOptionPane.showOptionDialog(
            null,
            "Qual item deseja remover?",
            "Remover Item",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            opcoesItens,
            opcoesItens[0]
        );

        if (escolha >= 0) {
            pedidoAtual.removerItem(escolha);
            JOptionPane.showMessageDialog(null, "Item removido!");
        }
    }

    // ===================== CADASTRAR PRODUTO =====================
    static void cadastrarProduto() {
        String nome = JOptionPane.showInputDialog("Nome do produto:");
        if (nome == null || nome.trim().isEmpty()) return;

        String precoStr = JOptionPane.showInputDialog("Preço do produto:");
        if (precoStr == null || precoStr.trim().isEmpty()) return;

        double preco = Double.parseDouble(precoStr);
        BancoDeDados.salvarProduto(nome, preco);
        JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
    }

    // ===================== VER PEDIDOS =====================
    static void verPedidos() {
        ArrayList<String> pedidos = BancoDeDados.listarPedidos();
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido encontrado!");
            return;
        }

        StringBuilder lista = new StringBuilder("===== PEDIDOS =====\n\n");
        for (String p : pedidos) {
            lista.append(p).append("\n");
        }

        JOptionPane.showMessageDialog(null, lista.toString());
    }
}
