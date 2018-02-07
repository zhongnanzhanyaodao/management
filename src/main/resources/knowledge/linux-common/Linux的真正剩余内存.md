linux一般我们通过使用 free 命令来查看内存使用状态

free -m
//-m 以兆为单位显示



    基本的关系是 (Mem) “total” = “used”+ “free”

    “shared”: 多个进程共享的内存总额
    “buffers”: Buffer Cache 和 Page Cache 的大小
    “-buffers/cache”: (Mem) “used” – “buffers” – “cached”
    “+buffers/cache”: (Mem) “free” + “buffers” + “cached”

    所以，真实剩余内存 = “free” + “buffers” + “cached” = “+buffers/cache” = 图片:内存.jpg 中高亮的那个数字

换一个角度说，内存.jpg中只占用内存 17MB。
Linux 系统使用 buffers 和 cache 是为了减少硬盘读写 (Swap)，提高重复资源的访问效率。
