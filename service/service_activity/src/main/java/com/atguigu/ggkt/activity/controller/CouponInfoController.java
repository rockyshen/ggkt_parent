package com.atguigu.ggkt.activity.controller;


import com.atguigu.ggkt.activity.service.CouponInfoService;
import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.activity.CouponUse;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vo.activity.CouponUseQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-11
 */
@Api(tags = "营销管理接口")
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

    //1 优惠劵分页查询
    @ApiOperation("优惠劵分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit
                        ){
        Page<CouponInfo> pageParam = new Page<>(page,limit);
        IPage<CouponInfo> pageModel = couponInfoService.page(pageParam);
        return Result.ok(pageModel);
    }

    //2 根据id获取优惠劵
    @ApiOperation("根据id获取优惠劵")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CouponInfo couponInfo = couponInfoService.getById(id);
        return Result.ok(couponInfo);
    }

    //3 新增一个优惠劵
    @ApiOperation("新增一个优惠劵")
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }

    //4 修改优惠劵
    @ApiOperation("修改一个优惠劵")
    @PostMapping("update")
    public Result updateById(@RequestBody CouponInfo couponInfo) {
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }

    //5 根据id删除一个优惠劵
    @ApiOperation("删除一个优惠劵")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        couponInfoService.removeById(id);
        return Result.ok(null);
    }

    //6 根据id列表删除多个优惠劵
    @ApiOperation("根据id列表批量删除多个优惠劵")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<String> idList) {
        couponInfoService.removeByIds(idList);     // string -> Long ?
        return Result.ok(null);
    }

    //7 获取“已使用优惠劵对象”（coupon_use）的列表，分页查询
    @ApiOperation("获取已使用优惠劵列表")
    @GetMapping("couponUse/{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        CouponUseQueryVo couponUseQueryVo) {
        Page<CouponUse> pageParam = new Page<>(page,limit);
        IPage<CouponUse> pageModel = couponInfoService.selectCouponUsePage(pageParam,couponUseQueryVo);
        return Result.ok(pageModel);
    }
}

