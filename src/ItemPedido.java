public class ItemPedido {

    private int produtoId;
    private String nomeProduto;
    private double precoProduto;
    private int quantidade;
    private double subtotal;

    public ItemPedido(int produtoId, String nomeProduto, double precoProduto, int quantidade) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.precoProduto = precoProduto;
        this.quantidade = quantidade;
        this.subtotal = precoProduto * quantidade;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public double getPrecoProduto() {
        return precoProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String toString() {
        return quantidade + "x " + nomeProduto + " = R$ " + String.format("%.2f", subtotal);
    }
}