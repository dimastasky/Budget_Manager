package budget;

import budget.products.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;

public class Budget_Manager  {

    static Scanner scanner = new Scanner(System.in);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private Format priceFormatter = new DecimalFormat("#0.00");

    private ArrayList<Product> products = new ArrayList<>();
    private double balance;

    private double total;


    public Budget_Manager() {
        this.balance = 0;
        this.total = 0;
    }

    //________METHODS___________

    public void menu() throws IOException {
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose your action:\n" +
                    "1) Add income\n" +
                    "2) Add purchase\n" +
                    "3) Show list of purchases\n" +
                    "4) Balance\n" +
                    "5) Save\n" +
                    "6) Load\n" +
                    "7) Analyze (Sort)\n" +
                    "0) Exit");
            int input = Integer.parseInt(scanner.next());
            System.out.println();
            switch (input) {
                case 0:
                    exit = true;
                    System.out.println("Bye!");
                    break;
                case 1:
                    addIncome();
                    System.out.println();
                    break;
                case 2:
                    while (true) {
                        System.out.println("Choose the type of purchase\n" +
                                "1) Food\n"+
                                "2) Clothes\n" +
                                "3) Entertainment\n" +
                                "4) Other\n" +
                                "5) Back");
                        System.out.println();
                        var productType = scanner.nextInt();
                        if(productType == 5) {
                            break;
                        }
                        else {
                            addPurchase(productType);
                            System.out.println();
                        }
                    }
                    System.out.println();
                    break;
                case 3:
                    while (true) {
                        System.out.println("Choose the type of purchases\n" +
                                "1) Food\n" +
                                "2) Clothes\n" +
                                "3) Entertainment\n" +
                                "4) Other\n" +
                                "5) All\n" +
                                "6) Back");
                        var type = scanner.nextInt();
                        System.out.println();
                        if(type == 6) {
                            break;
                        } else {
                            showPurchases(type);
                        }
                    }
                    System.out.println();
                    break;
                case 4:
                    getBalance();
                    System.out.println();
                    break;
                case 5:
                    savePurchases();
                    System.out.println();
                    break;
                case 6:
                    loadPurchases();
                    System.out.println();
                    break;
                case 7:
                    while (true) {
                        System.out.println("How do you want to sort?\n" +
                                "1) Sort all purchases\n" +
                                "2) Sort by type\n" +
                                "3) Sort certain type\n" +
                                "4) Back");
                        int type = scanner.nextInt();
                        System.out.println();
                        if (type == 4) {
                            break;
                        } else {
                            switch (type) {
                                case 1:
                                    sortAll();
                                    System.out.println();
                                    break;
                                case 2:
                                    sortByTypes();
                                    System.out.println();
                                    break;
                                case 3:
                                    sortCertainType();
                                    System.out.println();
                                    break;
                            }
                        }
                    }
                    System.out.println();
                    break;
            }
        }
    }

    private void savePurchases() {
        try(FileWriter file1 = new FileWriter("purchases.txt")){
            file1.write(priceFormatter.format(balance) + "\n");
            for (var product : products) {
                if (product instanceof Food) {
                    file1.write(product.getName() + "~~~" + product.getPrice() + "~~~" + "FOOD\n");
                } else if (product instanceof Clothes) {
                    file1.write(product.getName() + "~~~" + product.getPrice() + "~~~" + "CLOTHES\n");
                } else if (product instanceof Entertainment) {
                    file1.write(product.getName() + "~~~" + product.getPrice() + "~~~" + "ENTERTAINMENT\n");
                } else if (product instanceof Other) {
                    file1.write(product.getName() + "~~~" + product.getPrice() + "~~~" + "OTHER\n");
                } else {
                    file1.write(product.getName() + "~~~" + product.getPrice() + "~~~" + "PRODUCT\n");
                }
            }
            System.out.println("Purchases were saved!");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error =(!");
        }
    }
    private void loadPurchases() {
        products.clear();
        try(var scanner = new Scanner(new File("purchases.txt"))) {
            if (scanner.hasNextDouble()) {
                this.balance = scanner.nextDouble();
            }
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                var data = scanner.nextLine().split("~~~");
                if (data.length < 3) {
                    break;
                }
                switch (data[2]) {
                    case "FOOD":
                        products.add(new Food(data[0], Double.parseDouble(data[1])));
                        break;
                    case "CLOTHES":
                        products.add(new Clothes(data[0], Double.parseDouble(data[1])));
                        break;
                    case "ENTERTAINMENT":
                        products.add(new Entertainment(data[0], Double.parseDouble(data[1])));
                        break;
                    case "OTHER":
                        products.add(new Other(data[0], Double.parseDouble(data[1])));
                        break;
                    case "PRODUCT":
                        products.add(new Product(data[0], Double.parseDouble(data[1])));
                        break;
                }
            }
            System.out.println("Purchases were loaded!");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error =(!");
        }
    }

    private void showPurchases(int type) {
        var chooseList = new ArrayList<Product>();
        double totalSum = 0.0;
        sortProducts(type, chooseList);
        if (chooseList.size() < 1) {
            System.out.println("The purchase list is empty!");
        }
        switch (type) {
            case 1: System.out.println("Food: "); break;
            case 2: System.out.println("Clothes: "); break;
            case 3: System.out.println("Entertainment: "); break;
            case 4: System.out.println("Other: "); break;
            case 5: System.out.println("All: "); break;
        }
        for (var product : chooseList){
            totalSum += product.getPrice();
            System.out.println(product.getName() + " $" + priceFormatter.format(product.getPrice()));
        }
        System.out.println("Total sum: $" + priceFormatter.format(totalSum));
        System.out.println();
    }
    private void addPurchase(int type) throws IOException {
        System.out.println("Enter purchase name:");
        String name = reader.readLine();
        System.out.println("Enter its price:");
        double price = scanner.nextDouble();
        switch (type) {
            case 1: products.add(new Food(name, price)); break;
            case 2: products.add(new Clothes(name, price)); break;
            case 3: products.add(new Entertainment(name, price)); break;
            case 4: products.add(new Other(name, price)); break;
        }
        balance -= price;
        System.out.println("Purchase was added!");
    }

    private void sortAll() {
        if (products.size() < 1) {

            System.out.println("The purchase list is empty!");
        }
        else {
            products.sort(Comparator.comparing(Product::getPrice).reversed());

            showPurchases(5);
        }
    }

    private void sortByTypes() {
        var mapTypePrice = new HashMap<String,Double>();
        mapTypePrice.put("Food",0.0);
        mapTypePrice.put("Entertainment", 0.0);
        mapTypePrice.put("Clothes", 0.0);
        mapTypePrice.put("Other", 0.0);
        for (var product : products) {
            if (product instanceof Food) {
                mapTypePrice.replace("Food", mapTypePrice.get("Food") + product.getPrice());
            }
            if (product instanceof Entertainment) {
                mapTypePrice.replace("Entertainment", mapTypePrice.get("Entertainment") + product.getPrice());
            }
            if (product instanceof Clothes) {
                mapTypePrice.replace("Clothes", mapTypePrice.get("Clothes") + product.getPrice());
            }
            if (product instanceof Other) {
                mapTypePrice.replace("Other", mapTypePrice.get("Other") + product.getPrice());
            }
        }

        System.out.println("Types:");
        System.out.println("Food - $" + priceFormatter.format(mapTypePrice.get("Food")));
        System.out.println("Entertainment - $" + priceFormatter.format(mapTypePrice.get("Entertainment")));
        System.out.println("Clothes - $" + priceFormatter.format(mapTypePrice.get("Clothes")));
        System.out.println("Other - $" + priceFormatter.format(mapTypePrice.get("Other")));
        System.out.println("Total sum: $" + total);
    }

    private void sortCertainType() {
        System.out.println("Choose the type of purchase\n" +
                "1) Food\n" +
                "2) Clothes\n" +
                "3) Entertainment\n" +
                "4) Other");
        var type = scanner.nextInt();
        var result = sortByType(type);
        if (result.size() < 1){
            System.out.println();
            System.out.println("Purchase list is empty!");
        }
        else {
            result.sort(Comparator.comparing(Product::getPrice).reversed());
            System.out.println();
            showPurchases(type);
        }
    }

    private void sortProducts(int type, ArrayList<Product> result) {
        for (var product : products){
            if (type == 1 && product instanceof Food){
                result.add(product);
            }
            if (type == 2 && product instanceof Clothes){
                result.add(product);
            }
            if (type == 3 && product instanceof Entertainment){
                result.add(product);
            }
            if (type == 4 && product instanceof Other){
                result.add(product);
            }
            if (type == 5){
                result.add(product);
            }
        }
    }

    private ArrayList<Product> sortByType(int type){
        var result = new ArrayList<Product>();
        sortProducts(type, result);
        return result;
    }


    private void getBalance() {
        System.out.println("Balance: $" + balance);
    }

    private void addIncome() {
        System.out.println("Enter income:");
        double income = scanner.nextDouble();
        balance += income;
        System.out.println("Income was added!");
    }

}