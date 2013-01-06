Manyprices
==========

A small CRM that focus on price comparison of a product for different customers.

Manyprices is available in two languages (English and French).

-![alt text](http://i46.tinypic.com/f1d3k2.jpg)

Building and running
====================
  persistence properties are set to :
  
    jta-data-source=java:jboss/datasources/manyPricesDatasource
    hibernate.dialect=org.hibernate.dialect.MySQLDialect
    hibernate.hbm2ddl.auto=update
    
  you must create the datasource java:jboss/datasources/manyPricesDatasource under jboss as7 or edit persistence.xml.

###Using forge

        $ forge
        $ build
        $ as7 deploy
        
###Using maven 3
        $ mvn clean package
        $ mvn jboss-as:deploy
        

