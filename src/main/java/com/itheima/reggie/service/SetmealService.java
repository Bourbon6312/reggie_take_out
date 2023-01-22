package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，保存套餐与菜品的关联信息
    public void saveWithDish(SetmealDto setmealDto);

    //修改套餐
    public void updateWithDish(SetmealDto setmealDto);

    //更改套餐状态
    public void status(int status, String ids);

    //删除套餐，同时删除关联信息
    public void removeWithDish(List<Long> ids);

    //查询套餐信息
    public SetmealDto getSetmeal(Long ids);
}
