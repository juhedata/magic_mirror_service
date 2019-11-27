package com.juhe.demo.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @CLassName SimilarCompareBo
 * @Description 相似度比较对象
 * @Author kuai.zhang
 * @Date 2019/8/29 16:23
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimilarCompareBo {

    private Long id;

    private Float score;
}