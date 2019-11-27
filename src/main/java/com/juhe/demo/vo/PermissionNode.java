package com.juhe.demo.vo;

import java.util.List;
import lombok.Data;

/**
 * @CLassName PermissionNode
 * @Description
 * @Author xxmfypp
 * @Date 2019/7/18 17:43
 * @Version 1.0
 **/
@Data
public class PermissionNode extends PermissionVO {

    private List<PermissionNode> children;
}
