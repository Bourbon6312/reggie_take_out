package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

public interface OrderDetailService extends IService<OrderDetail> {
    //再来一单
    public void again(Orders orders);
}
