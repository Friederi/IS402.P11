package org.myorg.quickstart;

public class StoreSales {
    private String storeId;
    private Integer productQuantity;
    private Double revenue;

    public StoreSales() {
    }

    public StoreSales(String storeId, Integer productQuantity, Double revenue) {
        this.storeId = storeId;
        this.productQuantity = productQuantity;
        this.revenue = revenue;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
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
