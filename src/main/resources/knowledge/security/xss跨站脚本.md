XSS 全称“跨站脚本”，是注入攻击的一种。
其特点是不对服务器端造成任何伤害，而是通过一些正常的站内交互途径，例如发布评论，提交含有 JavaScript 的内容文本。
这时服务器端如果没有过滤或转义掉这些脚本，作为内容发布到了页面上，其他用户访问这个页面的时候就会运行这些脚本。

避免XSS的方法之一主要是将用户所提供的内容进行过滤，许多语言都有提供对HTML的过滤：

    PHP的htmlentities()或是htmlspecialchars()。
    Python的cgi.escape()。
    ASP的Server.HTMLEncode()。
    ASP.NET的Server.HtmlEncode()或功能更强的Microsoft Anti-Cross Site Scripting Library
    Java的xssprotect (Open Source Library)。
    Node.js的node-validator。
