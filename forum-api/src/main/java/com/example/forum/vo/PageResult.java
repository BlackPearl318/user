package com.example.forum.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

// 分页返回对象
@Data
public class PageResult<T> {

    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> list;

    public PageResult(PageInfo<T> pageInfo) {
        this.total = pageInfo.getTotal();
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.list = pageInfo.getList();
    }
}
