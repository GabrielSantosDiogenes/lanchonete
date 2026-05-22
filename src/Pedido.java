import java.util.ArrayList;

public class Pedido {

    private int id;
    private String nomeCliente;
    private double total;
    private ArrayList<ItemPedido> itens;

    public Pedido(String nomeCliente) {
        this.nomeCliente = nomeCliente;
        this.total = 0;
        this.itens = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<ItemPedido> getItens() {
        return itens;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        total += item.getSubtotal();
    }

    public void removerItem(int indice) {
        total -= itens.get(indice).getSubtotal();
        itens.remove(indice);
    }

    public void aplicarDesconto(double desconto) {
        total -= desconto;
    }

    public void adicionarTaxa(double taxa) {
        total += taxa;
    }
}
