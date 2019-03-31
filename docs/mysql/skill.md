

# 查看Mysql实时执行的Sql语句

1.进入Mysql
2.启用Log功能(general_log=ON) SHOW VARIABLES LIKE "general_log%"; SET GLOBAL general_log = 'ON';
3.设置Log文件地址(所有Sql语句都会在general_log_file里) SET GLOBAL general_log_file = 'c:\mysql.log';
4.下载BareTail专门查看Log文件的绿色软件(提供免费版本仅220k)
5.执行mysql命令然后在BareTail里查看


' or '1' = 'test


# mysql 改成emoji
win:在“HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\services”中查找“Mysql"
E:\Program\mysql-8.0.13-winx64\bin\mysqld --defaults-file="E:\Program\mysql-8.0.13-winx64\bin\my.cnf" mysql8


************不需要改my.cnf,连接为useUnicode=true

[client]

default-character-set=utf8mb4

 

[mysqld]
character-set-client-handshake = FALSE

character-set-server = utf8mb4

collation-server = utf8mb4_unicode_ci

init_connect=’SET NAMES utf8mb4'
[mysql]

default-character-set=utf8mb4

