package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    //数据列表
    private List<T> items;
    //总记录数
    private Long counts;
    //当前页码
    private Long page;
    //每页记录数
    private Long pageSize;


}
