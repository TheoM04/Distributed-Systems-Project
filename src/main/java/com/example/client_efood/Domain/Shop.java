package com.example.client_efood.Domain;

import com.example.client_efood.Filters.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Shop implements Rateable, Categorisable, PriceCategory, Locatable, Serializable {

    private static final long serialVersionUID = 9L;

    private static Integer gl_id = 0;

    private Integer id;
    private String name;
    private Location location;
    private String foodCategory;
    private float stars;
    private int noOfVotes;
    private String logoPath;
    private HashMap<Integer, Product> products;

    public Shop(
        String _name,
        double _latitude,
        double _longitude,
        String _foodCategory,
        float _stars,
        int _noOfVotes,
        String _logoPath
    ) {
        id = gl_id++;

        this.name = _name;
        this.location = new Location(_latitude, _longitude);
        this.foodCategory = _foodCategory;
        this.stars = _stars;
        this.noOfVotes = _noOfVotes;
        this.logoPath = _logoPath;
        this.products = new HashMap<>();
    }

    public Shop(Shop otherShop) {

        this.id = otherShop.getId();
        this.name = otherShop.getName();
        this.foodCategory = otherShop.getCategory();
        this.stars = otherShop.getStars();
        this.noOfVotes = otherShop.getNoOfVotes();
        this.logoPath = otherShop.getLogoPath();

        if (otherShop.location != null) {
            this.location = new Location(otherShop.location.getLatitude(), otherShop.location.getLongitude());
        } else {
            this.location = null;
        }

        if (otherShop.products != null) {
            this.products = new HashMap<>();
            for (Map.Entry<Integer, Product> entry : otherShop.products.entrySet())
                this.products.put(entry.getKey(), new Product(entry.getValue()));

        } else {
            this.products = new HashMap<>();
        }
    }

    public void addProduct(Product _product) {
        if (this.products.containsKey(_product.getId())) return;
        this.products.put(_product.getId(), _product);
    }

    public void removeProduct(Product _product) {
        if (!this.products.containsKey(_product.getId())) return;
        this.products.remove(_product.getId());
    }

    public void updateRating(float newRating) {
        if ((newRating >= 0) && (newRating <= 5)) {
            if (noOfVotes == 0) {
                stars = newRating;
                noOfVotes = 1;
            } else {
                float sumOfPreviousRatings = stars * noOfVotes;

                noOfVotes++;
                stars = (sumOfPreviousRatings + newRating) / noOfVotes;
            }
        }
    }

    public float getAverageOverallCost() {
        return (float) this.products.values().stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
    }

    @Override
    public PriceCategoryEnum getPriceCategory() {
        float prices = getAverageOverallCost();
        if (prices <= 5) {
            return PriceCategoryEnum.LOW;
        } else if (prices <= 15) {
            return PriceCategoryEnum.MEDIUM;
        } else {
            return PriceCategoryEnum.HIGH;
        }
    }

    public Integer getTotalSales(){
        return products.values().stream()
                .map(Product::getSold)
                .reduce(0, Integer::sum);
    }

    public Integer getTotalSalesForProductType(String product_type){
        return products.values().stream()
                .filter(product -> product.getType().equals(product_type))
                .map(Product::getSold)
                .reduce(0, Integer::sum);
    }

    @Override
    public float getRating() {
        return this.stars;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getCategory() {
        return this.foodCategory;
    }


    public String getName() {
        return this.name;
    }
    public float getStars() {
        return this.stars;
    }
    public int getNoOfVotes() {
        return this.noOfVotes;
    }
    public String getLogoPath() {
        return this.logoPath;
    }
    public Product getProductById(Integer _id) {
        return this.products.get(_id);
    }
    public HashMap<Integer, Product> getProducts() {
        return this.products;
    }

    public Integer getId() {
        return id;
    }

    public void showProducts(){
        StringBuilder builder = new StringBuilder();
        builder.append("Products:\n");
        for (Product product : products.values()) {
            if (!product.is_removed())
                builder.append(product);
        }
        System.out.println(builder);
    }

    public ArrayList<Product> getNonRemovedProducts(){
        ArrayList<Product> nonRemovedProducts = new ArrayList<>();
        for (Product product : products.values()) {
            if (!product.is_removed())
                nonRemovedProducts.add(product);
        }
        return nonRemovedProducts;
    }

    public void showManagerProducts(){
        StringBuilder builder = new StringBuilder();
        builder.append("Products:\n");
        for (Product product : products.values()) {
            builder.append(product).append("\tStatus: ").append(((product.is_removed()) ? "Removed\n": "Not removed\n"));
        }
        System.out.println(builder);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Shop: ").append(name).append("\n");
        builder.append("ID: ").append(id).append("\n");
        builder.append("  Location: ").append(location).append("\n");
        builder.append("  Category: ").append(foodCategory).append("\n");
        builder.append("  Rating: ").append(String.format("%.2f", getRating()))
                .append(" (").append(noOfVotes).append(" reviews)\n");
        builder.append("  Price Category: ").append(getPriceCategory()).append("\n");
        builder.append("  Logo Path: ").append(logoPath).append("\n");

        return builder.toString();
    }
}