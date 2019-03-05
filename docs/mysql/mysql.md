# windows
mysql-8.0.13-winx64.zip

~~~
# 初始化数据库
mysqld --initialize --console
# 执行完成后，会打印 root 用户的初始默认密码 A temporary password is generated for root@localhost: sOtjoMgeh1;H
# 创建my.ini
[mysqld]
basedir = D:\Program Files\MySQL\mysql-8.0.12-winx64
datadir = D:\Program Files\MySQL\mysql-8.0.12-winx64\data
port = 3306
# 安装服务(用管理员身份)
mysqld --install mysql8
# 修改密码
mysql -uroot -p
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '@Dengppx123456';
~~~

>
MySQL8.0.4以前MySQL的密码认证插件是“mysql_native_password”，而现在使用的是“caching_sha2_password”。
如果想默认使用“mysql_native_password”插件认证，可以在配置文件中配置default_authentication_plugin项。
[mysqld]
default_authentication_plugin=mysql_native_password
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '@Dengppx123456';


# centos yum
> 参考https://blog.csdn.net/xintingandzhouyang/article/details/80956348

~~~
yum localinstall https://repo.mysql.com//mysql80-community-release-el7-1.noarch.rpm
yum install mysql-community-server
service mysqld start
service mysqld status
grep 'temporary password' /var/log/mysqld.log

# 修改密码
mysql -uroot -p
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '@Dengppx123456';
# 设置允许远程登陆：
mysql> use mysql
mysql> update user set user.Host='%'where user.User='root';
mysql> flush privileges;
~~~


# tar安装
~~~
# 查看是否安装mariadb
rpm -qa | grep mariadb
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_644
或rpm -e --nodeps mariadb-libs-5.5.52-1.el7.x86_64


# 安装mysql 依赖包
yum install libaio (可能已经安装最新版)

# 去掉目录
tar -xvf /home/ppx/ftp/mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz --strip-components 1 -C /home/ppx/mysql
或 tar -xvf /home/ppx/ftp/mysql-8.0.15-linux-glibc2.12-x86_64.tar.xz --strip-components 1 -C /home/ppx/mysql


# Default options are read from the following files in the given order:
/etc/my.cnf /etc/mysql/my.cnf /usr/local/mysql/etc/my.cnf ~/.my.cnf 


/usr/local/mysql/bin/mysqld --initialize --user=root --basedir=/usr/local/mysql/ --datadir=/usr/local/mysql/data
或   /home/ppx/mysql/bin/mysqld --initialize --user=root --basedir=/home/ppx/mysql --datadir=/home/ppx/data/mysql


# error:./mysqld: error while loading shared libraries: libnuma.so.1: cannot open shared object file: No such file or directory
yum -y install numactl

10.开机自启 
# cp mysql.server /etc/init.d/mysql 
# chmod +x /etc/init.d/mysql 
11.注册服务 
# chkconfig --add mysql 
12.查看是否添加成功 
# chkconfig --list mysql 
~~~



chown -R mysql:mysql /home/ppx/data/mysql
/etc/init.d/mysql start


[mysqld]
datadir=/home/ppx/data/mysql
socket=/home/ppx/mysql/mysql.sock

log-error=/home/ppx/mysql/log/mysqld.log
pid-file=/home/ppx/mysql/mysqld/mysqld.pid

#忘记密码时使用
#skip-grant-tables
#设置协议认证方式(重点啊)
default_authentication_plugin=mysql_native_password

[mysql]
socket=/home/ppx/mysql/mysql.sock




