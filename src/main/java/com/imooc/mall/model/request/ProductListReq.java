package com.imooc.mall.model.request;


public class ProductListReq {
    private String orderBy;
    private Integer categoryId;
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 10;


    public ProductListReq() {
    }

    @Override
    public String toString() {
        return "SelectProductReq{" +
                "orderBy='" + orderBy + '\'' +
                ", categoryId=" + categoryId +
                ", keyword='" + keyword + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
