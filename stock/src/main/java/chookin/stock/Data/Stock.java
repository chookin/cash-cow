package chookin.stock.Data;

/**
 * Created by chookin on 7/5/14.
 */
public class Stock {
    private String id;

    @Override
    public String toString() {
        return String.format("Stock{id: %s, name: %s, exchange: %s}",this.id, this.name, this.exchange);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    private String name;
    private Exchange exchange;
}
