package com.juhe.demo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.Data;

/**
 * @CLassName NullVO
 * @Description TODO
 * @Author kuai.zhang
 * @Date 2019/8/7 15:37
 * @Version 1.0
 **/
@Data
@JsonInclude(Include.NON_NULL)
public class NullVO implements Serializable {

}