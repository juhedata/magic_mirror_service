package com.juhe.demo.common;

import com.github.pagehelper.PageInfo;
import java.util.List;
import lombok.Data;

/**
 * @CLassName CommonPage
 * @Description 通用分页
 * @Author xxmfypp
 * @Date 2019/7/8 18:01
 * @Version 1.0
 **/
@Data
public class CommonPage<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPage;
    private Long total;
    private List<T> list;

    /**
     * @return com.juhe.demo.common.CommonPage<T>
     * @Author xxmfypp
     * @Description 分页重置方法
     * @Date 18:08 2019/7/8
     * @Param [list]
     **/
    public static <T> CommonPage<T> restPage(List<T> list) {
        CommonPage<T> result = new CommonPage<T>();
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        result.setTotalPage(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return result;
    }

}
