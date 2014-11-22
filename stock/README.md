#Readme
use jsoup to extract stocks info, companies info, and history data.
use hibernate, spring jpa to save mysql database.

## Web page download
web pages are saved in `/home/${USER_NAME}/stock`

##Usage
cd /home/chookin/project/myworks/cash-cow/utils/ && mvn install -DskipTests
cd /home/chookin/project/myworks/cash-cow/stock/ && mvn clean package -DskipTests

cd /home/chookin/project/myworks/cash-cow/ && java -Dhttp.proxyHost=proxy.cmcc  -Dhttp.proxyPort=8080 -jar stock/target/stock-1.0.jar collect --hist=2014:3:2014:4

java -jar /home/chookin/project/myworks/cash-cow/stock/target/stock-1.0.jar collect --proxy=no --hist=2014:3:2014:4

## sql
select * from `history_data` where stock_id=1 order by time desc limit 100;