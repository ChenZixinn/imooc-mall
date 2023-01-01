package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;

public interface ProductService {

    void addProduct(AddProductReq addProductReq);

    void updateProduct(Product updateProductReq);

    void deleteProduct(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listProductForAdmin(Integer pageSize, Integer pageNum);

    PageInfo ListProductForCustomer(ProductListReq productListReq);

    Product detail(Integer id);
}
