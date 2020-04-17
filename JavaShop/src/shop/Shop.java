package shop;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Shop {

	private static double cash;
	// ProductStock is not represented as array but arraylist which is more advantageous as it means we don't have to be so low level
	// Inside the triangular brackets <> we put what type is going into the ArrayList - Note there is no int index (seen in C) as ArrayList does that for us
	private static ArrayList<ProductStock> stock;

	// Here we construct the shop using a string - we will take in the name of the file that we want to work with
	public Shop(String fileName) {

		stock = new ArrayList<>();
		List<String> lines = Collections.emptyList();

		try {
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

			String[] shopCash = lines.get(0).split(",");
			cash = Double.parseDouble(shopCash[0]);
			// i am removing at index 0 as it is the only one treated differently
			lines.remove(0);

			for (String line : lines) {
				String[] arr = line.split(",");
				String name = arr[0];

				double price = Double.parseDouble(arr[1]);
				int quantity = Integer.parseInt(arr[2].trim());
				Product p = new Product(name, price);
				ProductStock s = new ProductStock(p, quantity);
				stock.add(s);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// This method will get cash value from shop (shop float)
	public static double getCash() {
		return cash;
	}

	// This method will adjust the cash value from the shop (shop float)
	public static void setCash(double cash) {
		Shop.cash = cash;
	}

	// This method wil get product stock from the shop
	public static ArrayList<ProductStock> getStock() {
		return stock;
	}


	// a method to export shop details to a string
	@Override
	public String toString() {
		return "Shop [cash=" + cash + ", stock=" + stock + "]";
	}


	// Adapted from: https://www.baeldung.com/find-list-element-java
	// Method to find the price of the product from the shop stock based on its name
	public double findPrice(String name) {
		// for loop which gets a product name and passes it through the shop stock
		// take in a product name and iterate through the shop stock
		for (ProductStock productStock : stock) {
			// if the item is in the stock shop return the shop price for the item
			if (productStock.getProduct().getName().contentEquals(name)) {
				return productStock.getProduct().getPrice();
			}
		}
		// otherwise return 0
		return 0.0;
	}


	//Method to find product name from the shop stock based on name
	public static String findProduct(String product) {
		for (ProductStock productStock : stock) {
			// if statement to see if the product is in stock and returns the name if it is
			if (productStock.getProduct().getName().equals(product)) {
				return productStock.getProduct().getName();
			}
		}
		// if not return null
		return "NULL";
	}

	// Prints out the customer order that they wish to order
	public static void printOrder(Customer c) {

		// Print the customer budget which is stored in the csv file using a getter
		System.out.printf("\n\nCUSTOMER NAME     : " + c.getName() + "\n");
		System.out.printf("CUSTOMER BUDGET   : " + "€" + c.getBudget() + "\n");

		System.out.println("\n********************************************************");
		System.out.println("\n***                 CUSTOMER ORDER                   ***");
		System.out.println("\n********************************************************");
		System.out.println("\n| PRODUCT             |  QTY | UNIT PRICE | TOTAL COST |");
		System.out.println("\n********************************************************");

		double charge = 0.0;

		// For loop that passing each product in the customer shopping list - prints name, cost and quantity
		for (ProductStock productStock : c.getShoppingList()) {

			Product p = productStock.getProduct();
			double price = productStock.getProduct().getPrice();
			int q = productStock.getQuantity();
			String n = productStock.getProduct().getName();

			System.out.printf("| %-20.20s| %5d| €%10.2f| €%10.2f| \n", n, q, price, (q * price));
			System.out.println("********************************************************\n");
			charge += (q * price);
		}
		// Prints the following statements. Note this is before we review the order so may not be the final cost
		System.out.printf("%s's PROVISIONAL TOTAL COST OF ITEMS :     €   %.2f\n", c.getName(), charge);
		System.out.printf("%s's PROVISIONAL UPDATED BUDGET      :     € %.2f\n", c.getName(), (c.getBudget() - charge));
		System.out.println("\n********************************************************\n");
	}

	// Prints shop contents - name, price and quantity of products currently in the shop

	public static void printShop(Shop s) {
		// Print the current cash balance for the shop which is stored in csv file using a getter
		System.out.printf("\n\nSHOP BALANCE    :          " + "€" + s.getCash() + "\n");
		System.out.println("\n*******************************************\n");
		System.out.println("***            SHOP INVENTORY           ***\n");
		System.out.println("*******************************************\n");
		System.out.println("| PRODUCT             |  QTY  |   PRICE   |\n");
		System.out.println("*******************************************");

		// For loop that passing each product in the shop and prints out its name and quantity
		for (ProductStock productStock : s.getStock()) {

			Product p = productStock.getProduct();
			double price = productStock.getProduct().getPrice();
			int q = productStock.getQuantity();
			String n = productStock.getProduct().getName();

			// Print the current amount of stock available and price for each product in the shop
			System.out.printf("| %-20.20s| %5d| €%10.2f|\n", n, q, price);
			System.out.println("*******************************************");

		}
	}

	// Review the customer order
	public void reviewOrder(Customer c) {

		// Create the following variables:

		double orderCost = 0;             // Variable to  calculate the amount due for products stocked in shop
		double shortAmount = 0;           // Variable to calculate amount the customer is short when paying for items
		int shortStock = 0;            // Variable to calculate the shortage in stock in the shop vs what the customer ordered

		System.out.println("\n******************************************************\n");
		System.out.println("**            REVIEW AND PROCESS ORDER            **\n");
		System.out.println("******************************************************\n");

		//  Calculate the cost of the customer order which will be stored in the totalCost variable
		for (ProductStock productStock : c.getShoppingList()) {
			orderCost += (productStock.getProduct().getPrice() * productStock.getQuantity());
		}

		// Dilemma 1: Product customer ordered is not stocked in the shop.
		for (ProductStock productStock : c.getShoppingList()) {
			if (findProduct(productStock.getProduct().getName()).equals("NULL")) {
				System.out.printf("SORRY %s %s IS NOT STOCKED BY OUR SHOP AT THIS TIME!\n",
						c.getName(), productStock.getProduct().getName());
				return;
			}

		}

		// Dilemma 2: Customer does not have enough money - customer budget is less than total cost of the products.
		// Transaction will be cancelled as a result

		// if statement to see if the customer budget is greater than 1 cent - proceed to next if statement
		if (c.getBudget() >= 0.01) {

			// if statement to see if total cost of the order is greater than the customer budget
			if (orderCost > c.getBudget()) {

				// Find the amount the customer is short by deducting the customer budget from the total order cost
				shortAmount = (orderCost - c.getBudget());

				System.out.println("SORRY YOUR BUDGET DOES NOT COVER THE COST OF THE PRODUCTS YOU WISH TO PURCHASE!");
				System.out.printf("YOU ARE SHORT BY €%.2f \n", shortAmount);
				return;
			}

		}
		// Dilemma 3: Shop stock is less than the quantity the customer wishes to order

		for (ProductStock productStock : c.getShoppingList()) {
			for (ProductStock shopStock : stock) {

				// Find if the product in the shopping list is in the shop  - this checks if the strings are the same
				if (productStock.getProduct().getName().equals(shopStock.getProduct().getName())) {

					// if the shop stock quantity is less than the customer order quantity
					if (productStock.getQuantity() > shopStock.getQuantity()) {

						// Calculate the shortage of stock - how much is missing from  order.
						shortStock = (productStock.getQuantity() - shopStock.getQuantity());


						System.out.printf("\nSORRY %s OUR STORE ONLY HAS %d %s!\n", c.getName(), shopStock.getQuantity(), productStock.getProduct().getName());
						System.out.printf("THERE ARE %d %s MISSING FROM YOUR ORDER\n", shortStock, productStock.getProduct().getName());
						System.out.printf("%s WILL NOT BE ADDED TO YOUR ORDER\n", productStock.getProduct().getName());
						System.out.printf("PLEASE ADJUST ORDER ON FILE FOR %s AND REORDER\n", productStock.getProduct().getName());
						System.out.printf("ALL OTHER PRODUCTS LISTED ABOVE WILL BE ADDED AND PROCESSED\n");
						return;
					}

					// Deduct the customer quantity ordered from the shop stock
					shopStock.setQuantity(shopStock.getQuantity() - productStock.getQuantity());


					System.out.println("SUCCESSFULLY ORDERED     :     " + productStock.getProduct().getName() + "\n");
					System.out.println("UPDATED STOCK            :     " + shopStock.getQuantity() + "\n");
					System.out.printf("\n****************************************************\n");

				}

			}

		}

		// Update the customer balance to reflect the purchase of products
		c.setBudget(c.getBudget() - orderCost);
		System.out.printf("UPDATED CUSTOMER BUDGET : " + "€" + c.getBudget() + "\n");


		// Update the shop balance to reflect the customer purchase
		setCash(getCash() + orderCost);
		System.out.printf("UPDATED SHOP BALANCE      : " + "€" + getCash() + "\n");
	}


	public static void LiveCustomer() {

		// Create scanner to take in user input
		Scanner scan = new Scanner(System.in);

		System.out.println("\n\n******************************************************\n");
		System.out.println("**             WELCOME TO THE LIVE SHOP             **\n");
		System.out.println("******************************************************\n");


		// Prompts the user to enter name
		System.out.print("PLEASE ENTER YOUR NAME: ");
		String name = scan.nextLine();

		// Prompts the user to enter product that want
		System.out.printf("PLEASE ENTER PRODUCT TO PURCHASE: ");
		String productName = scan.nextLine();

			// Prompts the user to enter how many of products they want
			System.out.println("QUANTITY OF " + productName + " YOU WANT TO ORDER: ");
			int amount = scan.nextInt();

			double charge = 0.0;

			System.out.println("\n********************************************************");
			System.out.println("\n***                 CUSTOMER ORDER                   ***");
			System.out.println("\n********************************************************");
			System.out.println("\n| PRODUCT             |  QTY | UNIT PRICE | TOTAL COST |");
			System.out.println("\n********************************************************");

			for (ProductStock productStock : Shop.getStock()) {
				if (productName.equals(productStock.getProduct().getName())) {

					Product p = productStock.getProduct();
					double price = productStock.getProduct().getPrice();
					int q = amount;
					String n = productStock.getProduct().getName();

					System.out.printf("| %-20.20s| %5d| €%10.2f| €%10.2f| \n", n, q, price, (q * price));
					System.out.println("********************************************************\n");
					charge += (q * price);
				}
			}

			System.out.printf("COST OF PRODUCTS    :  €%.2f\n", charge, "\n");

			// Prompts the user to enter budget
			System.out.printf("PLEASE ENTER YOUR BUDGET: ");
			double budget = scan.nextDouble();

			if (budget - charge > 0.0) {

				System.out.printf("SUFFICIENT BUDGET TO PROCEED WITH ORDER\n");

				// Update the shop balance to reflect the customer purchase
				setCash(getCash() + charge);
				System.out.printf("UPDATED SHOP BALANCE      : " + "€" + getCash() + "\n");
			} else {
				// Find the amount the customer is short by deducting the customer budget from the total order cost
				System.out.printf("\n\n*****************************************************************************\n");
				System.out.printf("\nSORRY YOUR BUDGET DOES NOT COVER THE COST OF THE PRODUCTS YOU WISH TO PURCHASE!");
				System.out.printf("\nYOU ARE SHORT BY €%.2f \n", charge - budget);   // should be budget -charge but for readability I switched it around to remove the minus
				System.out.printf("\nORDER WILL NOT BE PROCESSED AS A RESULT");
				System.out.printf("\n*******************************************************************************\n");
			}

			System.out.printf("YOU HAVE ORDERED " + amount + " " + productName + " AND YOUR BUDGET IS " + "€" + budget + ".\n");

		}


		// Adapted from:
		// https://ubuntuforums.org/showthread.php?p=7938987
		//  https://stackoverflow.com/questions/5032356/using-scanner-nextline
		// https://stackoverflow.com/questions/8059259/creating-a-console-menu-for-user-to-make-a-selection
		// https://www.programcreek.com/java-api-examples/java.util.Scanner
		// https://stackoverflow.com/questions/36996450/return-to-main-menu-from-sub-menu-in-java

	public static void displayMenu(Shop s) {
		int option = 0;
		String fileName;
		String rerun;

		// create the scanner to take in user input
		Scanner scan = new Scanner(System.in);

		System.out.println("\n\n***************************************************************************");
		System.out.println("**                           WELCOME TO THE SHOP                         **");
		System.out.println("***************************************************************************");
		System.out.println("**                                                                       **");
		System.out.println("**            PLEASE SELECT FROM THE MENU OPTIONS BELOW :                **");
		System.out.println("**                                                                       **");
		System.out.println("**               ENTER  1 ==> VIEW SHOP CONTENTS                         **");
		System.out.println("**               ENTER  2 ==> CUSTOMER ORDER FROM CSV FILE               **");
		System.out.println("**               ENTER  3 ==> CUSTOMER ORDER VIA LIVE MODE               **");
		System.out.println("**               ENTER  4 ==> EXIT SHOP                                  **");
		System.out.println("**                                                                       **");
		System.out.println("***************************************************************************");
		System.out.println("***************************************************************************");
		System.out.println("\n\nENTER YOUR CHOICE : ");
//
		// Exception was added which changes the input to the proper format
		// Convert the string to an integer using Integer.parseInt(String) method.
		// If the user was to enter a letter i.e. g it would prompt them that they need to enter an integer
		// Adapted from: https://intellipaat.com/community/21669/scanner-is-skipping-nextline-after-using-next-or-nextfoo
		try {
			option = Integer.parseInt(scan.nextLine());
		}
		catch (NumberFormatException e) {
			System.out.printf("\n**************************************************************************\n");
			System.out.println("**       INVALID  INPUT - PLEASE ENTER A NUMBER FROM THE MENU ABOVE     **");
			System.out.printf("***************************************************************************\n");
			displayMenu(s);
		}

		if (option == 1) {
			printShop(s);
			System.out.printf("PRESS ANY KEY TO RETURN TO THE SHOP MENU.\n");
			rerun = scan.nextLine();
			displayMenu(s);
		}

		else if (option == 2) {
			System.out.printf("ENTER CUSTOMER FILE YOU WISH TO PROCESS: \n");
			fileName = scan.nextLine();
			System.out.println(fileName);

			// Create a new customer
			Customer newCustomer = new Customer("src/shop/" + fileName, s);
			printOrder(newCustomer);			// Print the customer order
			s.reviewOrder(newCustomer);			// Review the customer order

			System.out.printf("PRESS ANY KEY TO RETURN TO THE SHOP MENU.\n");
			rerun = scan.nextLine();
			displayMenu(s);
		}
		else if (option == 3) {

			LiveCustomer();
			System.out.printf("PRESS ANY KEY TO RETURN TO THE SHOP MENU.\n");
			rerun = scan.nextLine();
			displayMenu(s);

		}

		else if (option ==4) {

			System.out.printf("\n***************************************************************************\n");
			System.out.printf("**           GOODBYE AND THANK YOU FOR SHOPPING WITH US!                 **\n");
			System.out.printf("***************************************************************************\n");
			System.exit(0);

		}
		//  Try catch above catches if a user inputs a letter and prompts them to enter an integer on their next attempt
		// else statement will inform the user if the number they entered was not from the menu above and again will return the menu so they can try again
		else {
			System.out.printf("\n**************************************************************************************\n");
			System.out.printf("**                     INCORRECT SELECTION - PLEASE TRY AGAIN                         **\n");
			System.out.printf("**************************************************************************************\n");
			displayMenu(s);
		}

	}

	public static void main(String[] args) {
		Shop shop = new Shop("src/Shop/stock.csv");
		displayMenu(shop);

	}

}