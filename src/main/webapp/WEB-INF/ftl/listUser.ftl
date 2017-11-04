<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <#include "/pagination.ftl"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/asset/js/lib/bootstrap/bootstrap.min.css">
</head>
<body>

    <div class="container">
        <table class="table table-bordered table-hover">
            <thead>
                <th>序号</th>
                <th>用户名</th>
                <th>密码</th>
            </thead>
            <#list pageResult.resList as user>
                <tr>
                    <td>${(user.id)!}</td>
                    <td>${(user.userName)!}</td>
                    <td>${(user.pwd)!}</td>
                </tr>
            </#list>
            <tr style="text-align:right;"><td colspan="3"><@showPageInfoWithNum pageResult.paginationInfo/></td></tr>
        </table>
    </div>
    <form id="pageForm" action="/listUser" method="post">
        <input type="hidden" name="pageSize" value="${(pageResult.paginationInfo.pageSize)!}">
        <input type="hidden" id="pageNum" name="pageNum" value="${(pageResult.paginationInfo.pageNum)!}">
    </form>
    <script type="text/javascript" src="/asset/js/lib/jquery1.12.4.min.js"></script>
    <script type="text/javascript" src="/asset/js/lib/bootstrap/bootstrap.min.js"></script>
    <script>
        function pageControl (pageNum) {
            $('#pageNum').val(pageNum);
            $('#pageForm').submit();
        }
    </script>
</body>
</html>