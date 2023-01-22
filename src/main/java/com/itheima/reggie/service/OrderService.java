package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);

    //用户订单
    public Page<OrdersDto> userPage(int page, int pageSize);
}
