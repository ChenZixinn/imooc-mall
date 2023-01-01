package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.filter.CustomerFilter;
import com.imooc.mall.model.dao.*;
import com.imooc.mall.model.pojo.Order;
import com.imooc.mall.model.pojo.OrderItem;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.model.vo.OrderItemVO;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.OrderService;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.OrderCodeFactory;
import com.imooc.mall.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 订单Service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Value("${file.upload.ip}")
    String ip;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        // 拿到用户ID
        Integer userId = CustomerFilter.currentUser.getId();

        // 从购物车查找已经勾选的商品
        // 如果购物车勾选为空，报错
        List<CartVO> cartVOList = cartMapper.selectCheckedByUserId(userId);
        if (cartVOList.isEmpty())
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SELECT);
        // 判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);

        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        List<OrderItem> orderItemList = cartVOList2OrderItemList(cartVOList);

        for (OrderItem cartVO : orderItemList) {
            // 扣库存
            Integer productId = cartVO.getProductId();
            Product product = productMapper.selectByPrimaryKey(productId);
            product.setStock(product.getStock() - cartVO.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }

        // 把购物车中的已勾选商品删除
        cartClean(cartVOList);

        // 生成订单
        Order order = new Order();
        // 生成订单号
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(CustomerFilter.currentUser.getId());
        order.setTotalPrice(totalPrice(orderItemList));
        order.setOrderStatus(Constant.ORDER_STATUS_ENUM.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        // 保存到order表
        orderMapper.insertSelective(order);
        // 循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        // 返回结果
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        int sum = 0;
        for (OrderItem orderItem : orderItemList) {
            sum += orderItem.getTotalPrice();
        }
        return sum;
    }

    private void cartClean(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOList2OrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            // 把购物车对象转为订单item对象
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
            orderItemMapper.insertSelective(orderItem);
//            totalPrice += cartVO.getTotalPrice();
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            // 判断商品状态是否异常
            if (product == null || !product.getStatus().equals(Constant.SALE_STATUS.SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }

            // 判断库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo) {
        OrderVO orderVO = new OrderVO();
        Order order = getOrderByOrderNo(orderNo);
        orderVO = getOrderVO(order);
        return orderVO;
    }

    @Override
    public OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        // 相同值赋值
        BeanUtils.copyProperties(order, orderVO);
        // 获取OrderItem
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemListByOrderNo(orderVO.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.ORDER_STATUS_ENUM.codeBy(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(CustomerFilter.currentUser.getId());
        List<OrderVO> orderVOList = orderList2OrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderList2OrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectList();
        ArrayList<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo = new PageInfo(orderVOList);
        return pageInfo;
    }

    @Override
    public void cancel(String orderNo) {
        Order oldOrder = getOrderByOrderNo(orderNo);
        Order order = new Order();
        order.setId(oldOrder.getId());
        order.setOrderStatus(Constant.ORDER_STATUS_ENUM.CANCELED.getCode());
        order.setEndTime(new Date());
        int i = orderMapper.updateByPrimaryKeySelective(order);
        if (i == 0) throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }

    @Override
    public void finish(String orderNo) {
        Order oldOrder = getOrderByOrderNo(orderNo);

        Order order = new Order();
        order.setId(oldOrder.getId());
        order.setOrderStatus(Constant.ORDER_STATUS_ENUM.FINISH.getCode());
        order.setEndTime(new Date());
        int i = orderMapper.updateByPrimaryKeySelective(order);
        if (i == 0)throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }

    @Override
    public void orderDelivered(String orderNo) {
        Order oldOrder = getOrderByOrderNo(orderNo);
        if (!oldOrder.getOrderStatus().equals(Constant.ORDER_STATUS_ENUM.PAID.getCode()))
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_PAID);
        Order order = new Order();
        order.setId(oldOrder.getId());
        order.setDeliveryTime(new Date());
        order.setOrderStatus(Constant.ORDER_STATUS_ENUM.DELIVERED.getCode());
        int i = orderMapper.updateByPrimaryKeySelective(order);
        if (i == 0)throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }

    @Override
    public String qrcode(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        // 如果订单状态不是未付款的话就报错
        if (!Objects.equals(order.getOrderStatus(), Constant.ORDER_STATUS_ENUM.NOT_PAID.getCode()))
            throw new ImoocMallException(ImoocMallExceptionEnum.ORDER_STATUS_ERROR);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String address = "http://" + ip +":"+ request.getLocalPort();

        String path = Constant.FILE_UPLOAD_DIR + orderNo + ".png";
        String content = address + "/pay?orderNo=" + orderNo;
        String imgPath = address + "/static/images/" + orderNo + ".png";

        try {
            QRCodeGenerator.generateQRCodeImage(content, 350,350, path);
        } catch (Exception e) {
            throw new ImoocMallException(ImoocMallExceptionEnum.QRCODE_ENCODE_FAILED);
        }
        return imgPath;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ORDER);
        // 如果订单状态不是未付款的话就报错
        if (!Objects.equals(order.getOrderStatus(), Constant.ORDER_STATUS_ENUM.NOT_PAID.getCode()))
            throw new ImoocMallException(ImoocMallExceptionEnum.ORDER_STATUS_ERROR);
        order.setOrderStatus(Constant.ORDER_STATUS_ENUM.PAID.getCode());
        order.setPayTime(new Date());
        int i = orderMapper.updateByPrimaryKeySelective(order);
        if (i==0) throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
    }

    public Order getOrderByOrderNo(String orderNo){
        Order oldOrder = orderMapper.selectByOrderNo(orderNo);
        // 判断是否有订单
        if (oldOrder == null)
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ORDER);
        // 判断订单是否属于用户
        if (!oldOrder.getUserId().equals(CustomerFilter.currentUser.getId())) {
            User user = userService.getUser(CustomerFilter.currentUser.getId());
            if(!userService.checkAdmin(user))
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_YOUR_ORDER);
        }
        return oldOrder;
    }
}
