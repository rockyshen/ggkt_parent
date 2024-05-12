package com.atguigu.ggkt.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.ggkt.exception.GgktException;
import com.atguigu.ggkt.model.wechat.Menu;
import com.atguigu.ggkt.vo.wechat.MenuVo;
import com.atguigu.ggkt.wechat.mapper.MenuMapper;
import com.atguigu.ggkt.wechat.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单明细 订单明细 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-12
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private WxMpService wxMpService;

    //获取所有一级菜单
    @Override
    public List<Menu> findMenuOneInfo() {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",0);
        List<Menu> menuOneList = baseMapper.selectList(wrapper);
        return menuOneList;
    }

    //获取所有menu，按一级和二级菜单进行封装
    @Override
    public List<MenuVo> findMenuInfo() {
        //1 创建list集合，最终数据封装
        List<MenuVo> finalMenuList = new ArrayList<>();

        //2 查询所有菜单数据（包含1、2级）
        List<Menu> menuList = baseMapper.selectList(null);

        //3 从所有菜单数据里，获取所有一级菜单数据
        List<Menu> oneMenuList = menuList.stream()
                .filter(menu -> menu.getParentId().longValue() == 0)
                .collect(Collectors.toList());

        //4 封装一级菜单数据，放入最终数据list中
        oneMenuList.stream().forEach(oneMenu -> {
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu,oneMenuVo);
            //5 封装二级菜单数据（判断一级菜单id = 二级菜单的parent_id）
            List<Menu> twoMenuList = menuList.stream()
                    .filter(twoMenu -> twoMenu.getParentId().longValue() == oneMenu.getId())
                    .collect(Collectors.toList());

            ArrayList<MenuVo> children = new ArrayList<>();
            twoMenuList.stream().forEach(twoMenu -> {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu,twoMenuVo);
                children.add(twoMenuVo);
            });
            oneMenuVo.setChildren(children);
            finalMenuList.add(oneMenuVo);
        });
        return finalMenuList;
    }

    // 微信公众号同步菜单
    // 将数据封装成微信要求的JSON格式button-click等，利用fast-json工具
    @Override
    public void syncMenu() {
        //1 获取所有菜单数据
        List<MenuVo> OneMenuVoList = this.findMenuInfo();

        JSONArray buttonList = new JSONArray();

        for (MenuVo oneMenuVo:OneMenuVoList) {
            //JSON对象 一级菜单
            JSONObject one = new JSONObject();
            one.put("name",oneMenuVo.getName());

            //JSON数组 二级菜单
            JSONArray subButton = new JSONArray();
            for (MenuVo twoMenuVo:oneMenuVo.getChildren()) {
                JSONObject view = new JSONObject();
                view.put("type", twoMenuVo.getType());
                if(twoMenuVo.getType().equals("view")) {
                    view.put("name", twoMenuVo.getName());
                    view.put("url", "http://ggkt2.vipgz1.91tunnel.com/#"
                            +twoMenuVo.getUrl());
                } else {
                    view.put("name", twoMenuVo.getName());
                    view.put("key", twoMenuVo.getMeunKey());
                }
                subButton.add(view);
            }
            one.put("sub_button", subButton);
            buttonList.add(one);
            }

        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        try {
            String menuId = wxMpService.getMenuService().menuCreate(button.toJSONString());
            System.out.println("menuId: "+menuId);
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new GgktException(20001,"微信菜单同步失败");
        }
    }

    // 微信公众号菜单删除
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new GgktException(20001,"公众号菜单删除失败");
        }
    }
}
