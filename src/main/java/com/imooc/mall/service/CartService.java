package com.imooc.mall.service;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.Cart;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.vo.CartVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CartService {

    List<CartVO> selectByUserId(Integer userId);
    List<CartVO> addCart(Integer userId, Integer productId, Integer count);

    List<CartVO> updateCart(Integer userId, Integer productId, Integer count);

    List<CartVO> deleteCart(Integer userId, Integer productId);

    List<CartVO> selectCart(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllCart(Integer userId, Integer selected);
}
