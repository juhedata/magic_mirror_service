package com.juhe.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.juhe.demo.common.CommonResult;
import com.juhe.demo.entity.GroupName;

import com.juhe.demo.vo.GroupNameVO;
import java.util.List;

/**
 * <p>
 * 组名称 服务类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
public interface IGroupNameService extends IService<GroupName> {

    /**
     * @return count
     * @Author xxmfypp
     * @Description 根据名称查询分组数量
     * @Date 10:55 2019/7/12
     * @Param [name]
     **/
    int existGroupName(String name);

    /**
     * @return int
     * @Author kuai.zhang
     * @Description 更新查看是否名称重复
     * @Date 20:30 2019/7/18
     * @Param [id, name]
     **/
    int updateExistGroupName(Long id, String name);

    /**
     * @return com.juhe.demo.common.CommonResult
     * @Author kuai.zhang
     * @Description 分页显示分组信息
     * @Date 20:38 2019/7/18
     * @Param [name, page, size]
     **/
    List<GroupNameVO> listGroupName(String name);

    /**
     * @return java.lang.Boolean
     * @Author xianzhu.lu
     * @Description 保存分组信息
     * @Date 2019/8/1
     * @Param name
     **/
    Boolean saveGroupName(String name);
}
