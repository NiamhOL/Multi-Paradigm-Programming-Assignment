#include <stdio.h>
#include <string.h>
#include <stdlib.h>



struct Product {
    char* name;
    double price;
};


struct ProductStock {
    struct Product product;
    int quantity;
};


struct Customer {
    char* name;      
    double budget;        
    struct ProductStock shoppingList[10];
    int index;      
};


struct Shop {
    double cash;
    struct ProductStock stock[20]; 
    int index;   
};


void printProduct(struct Product p)
{
    printf("-------------------------\n" );
    printf("Product Name: %s \nProduct Price: %.2f\n", p.name, p.price);
    printf("-------------------------\n" );
}

void printCustomer(struct Customer c)
{
    printf("-------------------------\n" );
    // to restrict it to two decimal places => %.2f
    printf("Customer Name: %s \nCustomer Budget: %.2f\n", c.name, c.budget);
    printf("-------------------------\n" );
   
   
    for(int i=0; i <c.index; i++)
    {
        printProduct(c.shoppingList[i].product);      
        printf("%s Orders %d of above product\n", c.name, c.shoppingList[i].quantity );  
        double cost = c.shoppingList[i].quantity * c.shoppingList[i].product.price;
        printf("The cost to %s will be €%.2f\n", c.name, cost );
    }
}



struct Shop createAndStockShop()
{
    struct Shop shop = {200}; 
    FILE * fp;
    char * line = NULL;
    size_t len = 0;
    ssize_t read;

    fp = fopen("stock.csv", "r");   
    if (fp == NULL)
        exit(EXIT_FAILURE);

    while ((read = getline(&line, &len, fp)) != -1) {
        //printf("Retrieved line of length %zu:\n", read);
        // printf("%s is a line", line);



        char *n = strtok(line, ",");  
        char *p = strtok(NULL, ",");  
        char *q = strtok(NULL, ",");  
        double price = atof(p);
        int quantity = atoi(q);
        char *name = malloc(sizeof(char)*50);   
        strcpy(name, n);                
        struct Product product = {name, price};
        struct ProductStock stockItem = {product, quantity};
        shop.stock[shop.index++]=stockItem;
        //printf("Name of Product %s Price %.2f quantity %d\n", name, price, quantity );
    }
    return shop;

}

void printShop(struct Shop s)
{
    printf("Shop has %.2f in cash\n", s.cash);
    for(int i=0; i < s.index; i++)           
    {
        printProduct(s.stock[i].product);       
        printf("The shop has %d of the above \n", s.stock[i].quantity);
    }

}

int main(void)
{
    struct Customer roisin = {"Roisin", 100}; 
    printCustomer(roisin);

    struct Product coke = {"Can Coke", 1.10};
    struct Product bread = {"Bread", 0.70};
    
    printProduct(coke);

    struct ProductStock cokeStock = {coke, 20};
    
    struct ProductStock breadStock = {bread, 2};
    printProduct(coke):      //call the method and pass in coke
  //printf("The shop has %d of the product %s\n", cokeStock.quantity, cokeStock.product.name)
  

    roisin.shoppingList[roisin.index++]=cokeStock;  
    roisin.shoppingList[roisin.index++]=breadStock;
  //printfCustomer(roisin);

    struct Shop shop = createAndStockShop();
    printShop(shop);                        // pass the shop to the printShop method


    // printf("The shop has %d of the product %s\n", cokestock.quantity, cokestock.product.name);
    //
    return 0;
}