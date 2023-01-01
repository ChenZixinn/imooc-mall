package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Cart;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    @Override
    public List<CartVO> selectByUserId(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectByUserId(userId);
        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOList;
    }

    @Override
    public List<CartVO> addCart(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 不在购物车，新增一个cart
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setSelected(Constant.CHOICE.CHECKED);
            cart.setQuantity(count);
            cartMapper.insertSelective(cart);
        } else {
            count = cart.getQuantity() + count;
            Cart newCart = new Cart();
            newCart.setId(cart.getId());
            newCart.setProductId(cart.getProductId());
            newCart.setUserId(cart.getUserId());
            newCart.setSelected(Constant.CHOICE.CHECKED);
            newCart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.selectByUserId(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        // 判断商品状态是否异常
        if (product == null || !product.getStatus().equals(Constant.SALE_STATUS.SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
        }

        // 判断库存
        if (count > product.getStock()) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
        }
    }

    @Override
    public List<CartVO> updateCart(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart oldCart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (oldCart == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_FOUNT);
        }
        Cart cart = new Cart();
        BeanUtils.copyProperties(oldCart, cart);

        cart.setQuantity(count);
        int i = cartMapper.updateByPrimaryKeySelective(cart);
        if (i == 0)
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        return this.selectByUserId(userId);
    }

    @Override
    public List<CartVO> deleteCart(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_FOUNT);
        }
        int i = cartMapper.deleteByPrimaryKey(cart.getId());
        if (i == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        return this.selectByUserId(userId);
    }

    @Override
    public List<CartVO> selectCart(Integer userId, Integer productId, Integer selected) {
        Cart oldCart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        Cart cart = new Cart();
        BeanUtils.copyProperties(oldCart, cart);
        if (selected == 1) {
            cart.setSelected(Constant.CHOICE.CHECKED);
        } else {
            cart.setSelected(Constant.CHOICE.UN_CHECKED);
        }
        int i = cartMapper.updateByPrimaryKeySelective(cart);
        if (i == 0)
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        return this.selectByUserId(userId);
    }

    @Override
    public List<CartVO> selectAllCart(Integer userId, Integer selected) {
        int i = cartMapper.UpdateSelectAllByUserId(userId, selected);
        if (i == 0) throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);

        return this.selectByUserId(userId);
    }
}
