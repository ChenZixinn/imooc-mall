package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.ObjectName;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单Controller
 */
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("order/create")
    public ApiRestResponse create(@Valid @RequestBody CreateOrderReq createOrderReq){
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @ApiOperation("订单详情")
    @GetMapping("order/detail")
    public ApiRestResponse detail( @RequestParam String orderNo){
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @ApiOperation("订单列表")
    @GetMapping("order/list")
    public ApiRestResponse list( @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("取消订单")
    @PostMapping("order/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("生成字符二维码")
    @GetMapping("order/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String url = orderService.qrcode(orderNo);
        return ApiRestResponse.success(url);
    }

    @ApiOperation("付款")
    @RequestMapping("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }


    @ApiOperation("后台订单列表")
    @GetMapping("/admin/order/list")
    public ApiRestResponse listForAdmin(Integer pageNum, Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


    @ApiOperation("后台订单发货")
    @PostMapping("/admin/order/delivered")
    public ApiRestResponse orderDelivered(String orderNo){
        orderService.orderDelivered(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 前后台通用接口，40：订单完结
     * @param orderNo 订单号
     * @return 成功信号/失败信息
     */
    @ApiOperation("订单完结")
    @PostMapping("order/finish")
    public ApiRestResponse finish(@RequestParam String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
