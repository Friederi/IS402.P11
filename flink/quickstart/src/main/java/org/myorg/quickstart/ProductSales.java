package org.myorg.quickstart;

public class ProductSales {
    private String productId;
    private String productName;
    private Integer productQuantity;
    private Double revenue;

    public ProductSales() {
    }

    public ProductSales(String productId, String productName, Integer productQuantity, Double revenue) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.revenue = revenue;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
