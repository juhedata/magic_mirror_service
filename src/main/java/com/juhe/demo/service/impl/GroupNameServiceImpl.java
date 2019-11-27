package com.juhe.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juhe.demo.constant.SystemConstant;
import com.juhe.demo.entity.GroupName;
import com.juhe.demo.mapper.GroupNameMapper;
import com.juhe.demo.service.IGroupNameService;
import com.juhe.demo.vo.GroupNameVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组名称 服务实现类
 * </p>
 *
 * @author xuxm
 * @since 2019-07-10
 */
@Service
public class GroupNameServiceImpl extends ServiceImpl<GroupNameMapper, GroupName> implements IGroupNameService {

    @Override
    public int existGroupName(String name) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("name", name);
        return count(queryWrapper);
    }

    @Override
    public int updateExistGroupName(Long id, String name) {
        LambdaQueryWrapper<GroupName> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupName::getName, name).ne(GroupName::getId, id);
        return count(queryWrapper);
    }

    @Override
    public List<GroupNameVO> listGroupName(String name) {
        LambdaQueryWrapper<GroupName> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(GroupName::getName, name);
        }
        List<GroupName> groupNames = list(queryWrapper);
        List<GroupNameVO> groupNameVOs = new ArrayList<>(groupNames.size());
        GroupNameVO groupNameVO;
        for (GroupName groupName : groupNames) {
            groupNameVO = new GroupNameVO();
            BeanUtils.copyProperties(groupName, groupNameVO);
            groupNameVOs.add(groupNameVO);
        }
        return groupNameVOs;
    }

    @Override
    public Boolean saveGroupName(String name) {
        int count = this.existGroupName(name);
        if (count > 0) {
            return false;
        }
        GroupName groupName = new GroupName();
        groupName.setName(name);
        groupName.setCreateTime(LocalDateTime.now());
        groupName.setDeleteable(SystemConstant.GroupName.CANDELETE);
        save(groupName);
        return true;
    }
}
