package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.controller.EmployeeController;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息
        this.save(setmealDto);

        //SetmealDish中没有setmealId值，需要进行赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐信息
        saveOrUpdate(setmealDto);

        //删除原来的关联信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //SetmealDish中没有setmealId值，需要进行赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void status(int status, String ids) {
        String[] id = ids.split(",");
        for (String setmealId : id) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.parseLong(setmealId));
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }

    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1

        //查询状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);

        //不能删除
        if (count > 0) throw new CustomException("套餐正在售卖中，不能删除！");

        //可以删除
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        for (Long setmealId : ids) {
            setmealDishQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);
            setmealDishService.remove(setmealDishQueryWrapper);
        }

    }

    @Override
    public SetmealDto getSetmeal(Long ids) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(ids);
        BeanUtils.copyProperties(setmeal, setmealDto);

        String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();
        setmealDto.setCategoryName(categoryName);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

}
