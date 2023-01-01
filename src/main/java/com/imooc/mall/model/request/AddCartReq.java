package com.imooc.mall.model.request;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddCartReq {

    @NotNull
    private Integer productId;
    @NotNull
    private Integer userId;

    private Integer quantity = 1;

    private Integer selected = 1;


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "AddCartReq{" +
                "productId=" + productId +
                ", userId=" + userId +
                ", quantity=" + quantity +
                ", selected=" + selected +
                '}';
    }
}