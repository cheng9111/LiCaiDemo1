package com.bjpowernode.pojo;

//分页
public class PageInfo {

    private Integer pageNo;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalRecords;

    public PageInfo() {
        pageNo = 1;
        pageSize = 9;
        totalPages = 0;
        totalRecords = 0;
    }

    public PageInfo(Integer pageNo, Integer pageSize, Integer totalRecords) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        //可以计算总页数的.
        if( totalRecords % pageSize == 0 ){
            totalPages = totalRecords / pageSize;
        } else {
            totalPages = totalRecords / pageSize + 1;
        }
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
}
