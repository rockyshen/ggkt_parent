package com.atguigu.ggkt.order.service.impl;

import com.atguigu.ggkt.client.activity.CouponInfoFeignClient;
import com.atguigu.ggkt.client.course.CourseFeignClient;
import com.atguigu.ggkt.client.user.UserInfoFeignClient;
import com.atguigu.ggkt.utils.OrderNoUtils;
import com.atguigu.ggkt.exception.GgktException;
import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.order.OrderDetail;
import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.order.mapper.OrderInfoMapper;
import com.atguigu.ggkt.order.service.OrderDetailService;
import com.atguigu.ggkt.order.service.OrderInfoService;
import com.atguigu.ggkt.utils.AuthContextHolder;
import com.atguigu.ggkt.vo.order.OrderFormVo;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private CouponInfoFeignClient couponInfoFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    // 订单列表
    @Override
    public Map<String, Object> selectOrderInfoPage(Page<OrderInfo> pageParam,OrderInfoQueryVo orderInfoQueryVo) {
        // 通过orderInfoQueryVo获取查询条件
        Long userId = orderInfoQueryVo.getUserId();
        String outTradeNo = orderInfoQueryVo.getOutTradeNo();
        String phone = orderInfoQueryVo.getPhone();
        String createTimeEnd = orderInfoQueryVo.getCreateTimeEnd();
        String createTimeBegin = orderInfoQueryVo.getCreateTimeBegin();
        Integer orderStatus = orderInfoQueryVo.getOrderStatus();

        // 判断条件值是否为空，不为空，进行封装
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(outTradeNo)) {
            wrapper.eq("out_trade_no",outTradeNo);
        }
        if(!StringUtils.isEmpty(phone)) {
            wrapper.eq("phone",phone);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }

        //调用Mapper方法实现分页查询
        Page<OrderInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);
        long totalCount = pageModel.getTotal();
        long pageCount = pageModel.getPages();
        List<OrderInfo> records = pageModel.getRecords();

        //根据订单id查询出详情，再封装
        records.stream().forEach(item -> {
            this.getOrderDetail(item);

        });
        //所有需要的数据都封装到Map中
        Map<String, Object> map = new HashMap<>();
        map.put("total",totalCount);
        map.put("pageCount",pageCount);
        map.put("records",records);
        return map;
    }

    //api,微信公众号调用此方法，生成订单，OrderInfo
    @Override
    public Long submitOrder(OrderFormVo orderFormVo) {
        //1 从orderFormVO中取值
        Long courseId = orderFormVo.getCourseId();
        Long couponId = orderFormVo.getCouponId();
        Long userId = AuthContextHolder.getUserId();    // 类实现在common/utils中

        //2 判断当前用户，针对当前课程，是否已经生成了该课程订单（参数1:课程id,参数2:用户id) order_detail
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        wrapper.eq("user_id",userId);
        OrderDetail orderDetailExist = orderDetailService.getOne(wrapper);
        if(orderDetailExist != null) {
            return orderDetailExist.getId();  //订单存在，直接返回订单id
        }

        //3 根据课程id，查询课程信息（OpenFeign）  courseId -> courseInfo
        Course course = courseFeignClient.getById(courseId);
        if(course == null){
            throw new GgktException(20001,"课程不存在");
        }

        //4 根据课程id,查询用户信息 courseId -> userInfo
        UserInfo userInfo = userInfoFeignClient.getById(userId);
        if(userInfo == null){
            throw new GgktException(20001,"用户不存在");
        }

        //5 根据优惠劵id，查询优惠劵信息
        BigDecimal couponReduce = new BigDecimal(0);
        if(couponId != null){
            CouponInfo couponInfo = couponInfoFeignClient.getById(couponId);
            couponReduce = couponInfo.getAmount();
        }

        //6 封装订单生成需要的数据到OrderInfo，完成订单
        //6.1 封装数据到order_info，添加到order_info表中
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setUserId(userId);
        orderInfo.setNickName(userInfo.getNickName());
        orderInfo.setPhone(userInfo.getPhone());
        orderInfo.setProvince(userInfo.getProvince());
        orderInfo.setOriginAmount(course.getPrice());
        orderInfo.setCouponReduce(couponReduce);
        orderInfo.setFinalAmount(orderInfo.getOriginAmount().subtract(orderInfo.getCouponReduce()));
        orderInfo.setOutTradeNo(OrderNoUtils.getOrderNo());
        orderInfo.setTradeBody(course.getTitle());
        orderInfo.setOrderStatus("0");
        baseMapper.insert(orderInfo);

        //6.2 封装数据到order_detail，添加到order_detail表中
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        orderDetail.setUserId(userId);
        orderDetail.setCourseId(courseId);
        orderDetail.setCourseName(course.getTitle());
        orderDetail.setCover(course.getCover());
        orderDetail.setOriginAmount(course.getPrice());
        orderDetail.setCouponReduce(new BigDecimal(0));
        orderDetail.setFinalAmount(orderDetail.getOriginAmount().subtract(orderDetail.getCouponReduce()));
        orderDetailService.save(orderDetail);

        //7 更新优惠劵状态（已使用）
        if(orderFormVo.getCouponId() != null){
            couponInfoFeignClient.updateCouponInfoUseStatus(orderFormVo.getCouponUseId(),orderInfo.getId());
        }

        //8 返回订单id
        return orderInfo.getId();
    }

    //传入Order_info对象，查询Order_detail
    private OrderInfo getOrderDetail(OrderInfo orderInfo) {
        Long id = orderInfo.getId();
        OrderDetail orderDetail = orderDetailService.getById(id);//order_detail表中id和order_id相同

        if (orderDetail != null) {
            String courseName = orderDetail.getCourseName();
            orderInfo.getParam().put("courseName", courseName);
        }
        return orderInfo;
    }
}
