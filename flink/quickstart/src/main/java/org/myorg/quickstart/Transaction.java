package org.myorg.quickstart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Transaction {
    private String transactionId;
    private String productId;
    private String productName;
    private String productCategory;
    private Double productPrice;
    private Integer productQuantity;
    private String productBrand;
    private String customerId;
    private Instant transactionDate;
    private String paymentMethod;
    private String storeId;
    private Double totalAmount;

    public Transaction() {
    }

    @JsonCreator
    public Transaction(@JsonProperty("transactionId") String transactionId,
                       @JsonProperty("totalAmount") Double totalAmount,
                       @JsonProperty("storeId") String storeId,
                       @JsonProperty("paymentMethod") String paymentMethod,
                       @JsonProperty("transactionDate") Instant transactionDate,
                       @JsonProperty("customerId") String customerId,
                       @JsonProperty("productBrand") String productBrand,
                       @JsonProperty("productQuantity") Integer productQuantity,
                       @JsonProperty("productPrice") Double productPrice,
                       @JsonProperty("productCategory") String productCategory,
                       @JsonProperty("productName") String productName,
                       @JsonProperty("productId") String productId) {
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
        this.storeId = storeId;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
        this.customerId = customerId;
        this.productBrand = productBrand;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productCategory = productCategory;
        this.productName = productName;
        this.productId = productId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
