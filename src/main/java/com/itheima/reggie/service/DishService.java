package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入口味
    public void saveWithFlavor(DishDto dishDto);

    //查询菜品
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品
    public void removeWithFlavor(String ids);

    //更改菜品状态
    public void status(int status, String ids);
}
