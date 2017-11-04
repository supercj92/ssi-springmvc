<#macro showPageInfoWithNum paginationInfo>
    <#if paginationInfo??>
        <ul class="pagination">
            <#assign pageNum=paginationInfo.pageNum>
            <#assign totalPage=paginationInfo.totalPage>
            <#assign totalCount=paginationInfo.totalCount>
            <li><a class="current">第${pageNum}/${totalPage}页</a></li>
            <li><a>共${totalCount}条记录</a></li>
            <#if pageNum gt 1 >
                <li><a href="javascript:void(0)" onclick="pageControl(${(pageNum - 1)!})">上一页</a></li>
            <#else>
                <li class="disabled"><a>上一页</a></li>
            </#if>

        <#-- 数字start-->
            <#assign start=1>
        <#--如果当前页大于4则显示前两页和省略号-->
            <#if (pageNum > 4)>
                <#assign start=(pageNum - 1)>
                <li><a href="javascript:void(0)" onclick="pageControl(1)">1</a></li>
                <li><a href="javascript:void(0)" onclick="pageControl(2)">2</a></li>
                <li class="disabled"><a>&hellip;</a></li>
            </#if>
        <#-- 显示当前页号和前后的页号 -->
            <#assign end=(pageNum + 1)>
            <#if (end > totalPage)>
                <#assign end=totalPage>
            </#if>
            <#if (totalPage == 0)>
                <#assign end=1>
            </#if>
            <#list start..end as i>
                <#if (pageNum==i)>
                    <li class="disabled"><a>${i}</a></li>
                <#else>
                    <li><a href="javascript:void(0)" onclick="pageControl(${i})">${i}</a></li>
                </#if>
            </#list>

        <#-- 如果后面页数过多,显示省略号 -->
            <#if (end < totalPage - 2)>
                <li class="disabled"><a>&hellip;</a></li>
            </#if>
            <#if (end < totalPage - 1)>
                <li><a href="javascript:void(0)" onclick="pageControl(${totalPage-1})">${totalPage-1}</a></li>
            </#if>
            <#if (end < totalPage)>
                <li><a href="javascript:void(0)" ${totalPage}>${totalPage}</a></li>
            </#if>
        <#-- 数字end-->

            <#if pageNum lt totalPage>
                <li><a href="javascript:void(0)" onclick="pageControl(${(pageNum + 1)!})">下一页</a></li>
            <#else>
                <li class="disabled"><a>下一页</a></li>
            </#if>
        </ul>
    </#if>
</#macro>