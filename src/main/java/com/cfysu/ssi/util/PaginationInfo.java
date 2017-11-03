package com.cfysu.ssi.util;

/**
 * 分页工具
 */
public class PaginationInfo{

    /**
     * 第几页
     */
    private int pageNum;

    /**
     * 每页数量
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private int totalCount;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * limit开始行
     */
    private int startRow;

    /**
     * 终止行
     */
    private int endRow;

    public static final int DEFAULT_PAGESIZE = 10;

    public PaginationInfo(int pageNum, int totalCount, int pageSize){
        this.pageNum = pageNum;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        rePaginate();
    }

    public PaginationInfo(int pageNum, int totalCount){
        this.pageNum = pageNum;
        this.totalCount = totalCount;
        this.pageSize = DEFAULT_PAGESIZE;
        rePaginate();
    }

    private void rePaginate(){

        if(pageSize < 1){
            pageSize = DEFAULT_PAGESIZE;
        }

        if(pageNum < 1){
            pageNum = 1;
        }

        if(totalCount > 0){

            totalPage = totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1);

            if(pageNum > totalPage){
                pageNum = totalPage;
            }

            endRow = pageNum * pageSize;
            if(endRow > totalCount){
                endRow = totalCount;
            }

            startRow = endRow - pageSize;
            if(startRow < 0){
                startRow = 0;
            }

            if(pageSize > totalCount){
                pageSize = totalCount;
            }
        }

    }
}
