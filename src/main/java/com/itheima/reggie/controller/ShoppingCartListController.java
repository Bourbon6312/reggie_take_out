package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartListController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //用户id
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        return R.success(shoppingCartService.list(queryWrapper));
    }

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //添加菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart res = shoppingCartService.getOne(queryWrapper);

        //判断是否已存在
        if (res != null) {
            Integer number = res.getNumber();
            res.setNumber(number + 1);
            shoppingCartService.updateById(res);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            res = shoppingCart;
        }

        return R.success(res);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        //用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //删除菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //删除套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart res = shoppingCartService.getOne(queryWrapper);
        Integer number = res.getNumber();

        //判断是否为最后一件
        if (number != 1) {
            res.setNumber(number - 1);
            shoppingCartService.updateById(res);
        } else {
            shoppingCartService.removeById(res.getId());
            res = null;
        }
        return R.success(res);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        //用户id
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("清空成功！");
    }
}
