package shop;

public class ProductStock {
	private Product product;
	private int quantity;

	public ProductStock(Product product, int quantity) {
		super();
		this.product = product;
		this.quantity = quantity;
	}

	public int getQuantity() {

		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {

		return product;
	}

	// What this should do using the 2 string method is it wil try and process
	// this string and it will find the product is  an object of type product and it will
	@Override
	public String toString() {

		return "ProductStock [product=" + product + ", quantity=" + quantity + "]";
	}

}
