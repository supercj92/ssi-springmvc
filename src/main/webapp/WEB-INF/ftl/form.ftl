<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/asset/js/lib/bootstrap/bootstrap.min.css">
</head>
<body>
<div class="container">
    <form action="/form" method="post">
        <div class="form-group">
            <label>orderId</label>
            <input class="form-control" name="orderId" placeholder="orderId">
        </div>
        <div class="form-group">
            <label>customerName</label>
            <input class="form-control" name="customerName" placeholder="customerName">
        </div>
        <div class="form-group">
            <input type="submit" value="submit" class="btn btn-primary">
        </div>
    </form>

    <form action="/form" method="post">
        <div class="radio">
            <label>
                <input type="radio" value="male" checked name="gender">男
            </label>
        </div>
        <div class="radio">
            <label>
                <input type="radio" value="female" name="gender">女
            </label>
        </div>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="agree" checked name="contract">同意
            </label>
        </div>
        <input type="submit" class="btn btn-primary" value="submit">
    </form>

</div>
<script type="text/javascript" src="/asset/js/lib/jquery1.12.4.min.js"></script>
<script type="text/javascript" src="/asset/js/lib/bootstrap/bootstrap.min.js"></script>
</body>