<#macro showPageInfoWithNum pageInfo>
    <#if pageInfo??>
    <div>
        <ul class="pagination">
            <li><a class="current">第${pageInfo.pageNum}/${pageInfo.totalPage}页</a></li>
            <li><a>共${pageInfo.totals}条记录</a></li>
            <#if pageInfo.pageNum gt 1 >
                <li><a href="javascript:void(0)" onclick="changePageNum(${pageInfo.pageNum-1})">上一页</a></li>
            <#else>
                <li class="disabled"><a>上一页</a></li>
            </#if>

        <#-- 数字start-->
            <#assign pageNo=pageInfo.pageNum>
            <#assign pageCount=pageInfo.totalPage>
            <#assign start=1>
        <#--如果当前页大于4则显示前两页和省略号-->
            <#if (pageNo > 4)>
                <#assign start=(pageNo - 1)>
                <li><a href="javascript:void(0)" onclick="changePageNum(1)">1</a></li>
                <li><a href="javascript:void(0)" onclick="changePageNum(2)">2</a></li>
                <li class="disabled"><a>&hellip;</a></li>
            </#if>
        <#-- 显示当前页号和前后的页号 -->
            <#assign end=(pageNo + 1)>
            <#if (end > pageCount)>
                <#assign end=pageCount>
            </#if>
            <#if (pageCount == 0)>
                <#assign end=1>
            </#if>
            <#list start..end as i>
                <#if (pageNo==i)>
                    <li class="disabled"><a>${i}</a></li>
                <#else>
                    <li><a href="javascript:void(0)" onclick="changePageNum(${i})">${i}</a></li>
                </#if>
            </#list>

        <#-- 如果后面页数过多,显示省略号 -->
            <#if (end < pageCount - 2)>
                <li class="disabled"><a>&hellip;</a></li>
            </#if>
            <#if (end < pageCount - 1)>
                <li><a href="javascript:void(0)" onclick="changePageNum(${pageCount-1})">${pageCount-1}</a></li>
            </#if>
            <#if (end < pageCount)>
                <li><a href="javascript:void(0)" onclick="changePageNum(${pageCount})">${pageCount}</a></li>
            </#if>
        <#-- 数字end-->

            <#if pageInfo.pageNum lt pageInfo.totalPage>
                <li><a href="javascript:void(0)" onclick="changePageNum(${pageInfo.pageNum+1})">下一页</a></li>
            <#else>
                <li class="disabled"><a>下一页</a></li>
            </#if>
        </ul>
    </div>
    </#if>
</#macro>