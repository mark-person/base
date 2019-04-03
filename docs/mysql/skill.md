

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



== fulltext index

SELECT * FROM `student` WHERE MATCH(`name`) AGAINST('value')

剔除一半row以上都有的字，譬如说，每个row都有mysql这个字的话，那用mysql去查时，会找不到任何row，这在row的数量无敌多时很有用，因为把所有row都找出来是没有意义的，这时，mysql几乎被当作是stopword；但是当row只有两笔时，是啥鬼也查不出来的，因为每个字都出现50%以上，要避免这种状况，请用IN BOOLEAN MODE。 

此时使用全文索引的格式就变成了： SELECT * FROM `student` WHERE MATCH(`name`) AGAINST('聪' IN BOOLEAN MODE)

为什么要设置 Mysql 的 ft_min_word_len=1 ？
Mysql 默认的最小索引长度是 4。如果是英文默认值是比较合理的，但是中文绝大部分词都是2个字符

在 [mysqld] 后面加入一行“ft_min_word_len=1 ”
查看show variables like 'ft_min_word_len'

全转成拼音。。。走 fulltext index 逃:）

查看show variables like 'ngram_token_size'


ngram_token_size=2 #表示控制innodb全文检索分词的长度

# 查看索引
CREATE TABLE articles (
         id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
         title VARCHAR(200),
         body TEXT,
         FULLTEXT (title,body)
       ) ENGINE=InnoDB

set global innodb_ft_aux_table='repository/articles'

INSERT INTO articles (title,body) VALUES
       ('MySQL Tutorial','DBMS stands for DataBase ...'),
       ('How To Use MySQL Well','After you went through a ...'),
       ('Optimizing MySQL','In this tutorial we will show ...'),
       ('1001 MySQL Tricks','1. Never run mysqld as root. 2. ...'),
       ('MySQL vs. YourSQL','In the following database comparison ...'),
       ('MySQL Security','When configured properly, MySQL ...');

SELECT WORD, DOC_COUNT, DOC_ID, POSITION
       FROM INFORMATION_SCHEMA.INNODB_FT_INDEX_CACHE LIMIT 5;

关联删除
SET GLOBAL innodb_optimize_fulltext_only=1;
OPTIMIZE TABLE articles;













