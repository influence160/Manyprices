this files are to be copied in JBOSS_HOME/

Files edit :
standalone.conf.bat
	added line 
	SET "JAVA_HOME=C:\Program Files (x86)\Java\jdk1.6.0_45"

logging.properties
	edited line 
	formatter.PATTERN.pattern=%d{HH:mm:ss,SSS} %-5p [%c] %s%E%n to formatter.PATTERN.pattern=%d{dd-MM-yyyy HH:mm:ss,SSS} %-5p [%c] %s%E%n

mgmt-users.properties
	added line 
	admin=95333971266d87fbfa7d9963dd5e89d6

standalone.xml
	edited lines 
	
                    <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/> 
					to 
                    <pattern-formatter pattern="%d{dd-MM-yyyy HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
	added lines
	
                <datasource jndi-name="java:jboss/datasources/manyPricesDatasource" pool-name="manyPricesDatasource_pool" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/manyprices</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <user-name>root</user-name>
                    </security>
                </datasource>
				
                    <driver name="mysql" module="com.mysql">
                        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
                    </driver>