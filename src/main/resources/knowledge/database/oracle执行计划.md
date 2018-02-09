#第一步：EXPLAIN PLAN FOR sql;
例子：EXPLAIN PLAN FOR SELECT * FROM EMP;
#第二步：select * from table(dbms_xplan.display);


执行顺序的原则:
    一般按缩进长度来判断，缩进最大的最先执行，如果有2行缩进一样，那么就先执行上面的。