# Installation de ManyPrices sur Linux Mint 22 MATE

## Versions utilisées
| Logiciel | Version | Lien |
|---|---|---|
| Linux Mint | 22 MATE 64-bit | https://www.linuxmint.com/download.php |
| Java JDK | 6u45 (64-bit) | https://www.oracle.com/fr/java/technologies/javase-java-archive-javase6-downloads.html |
| JBoss AS | 7.1.1.Final | https://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip |
| MySQL | 5.0.96 Linux Generic 64-bit | https://downloads.mysql.com/archives/community/ |

---

## 1. Prérequis système

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install git unzip -y
```

---

## 2. Java JDK 6

Télécharger `jdk-6u45-linux-x64.bin` depuis Oracle (lien ci-dessus).

```bash
sudo mv ~/Téléchargements/jdk-6u45-linux-x64.bin /opt/
sudo chmod +x /opt/jdk-6u45-linux-x64.bin
cd /opt && sudo ./jdk-6u45-linux-x64.bin
sudo rm /opt/jdk-6u45-linux-x64.bin
```

Le JDK se trouve dans `/opt/jdk1.6.0_45`.

---

## 3. MySQL 5.0.96

### 3.1 Installation

Télécharger `mysql-5.0.96-linux-x86_64-glibc23.tar.gz` (Linux Generic 64-bit).

```bash
sudo tar -xzf ~/Téléchargements/mysql-5.0.96-linux-x86_64-glibc23.tar.gz -C /opt/
sudo chown -R mysql:mysql /opt/mysql-5.0.96-linux-x86_64-glibc23
cd /opt/mysql-5.0.96-linux-x86_64-glibc23
sudo scripts/mysql_install_db --user=mysql
sudo chown -R root .
sudo chown -R mysql data
sudo ln -s /opt/mysql-5.0.96-linux-x86_64-glibc23 /usr/local/mysql
```

Si la bibliothèque `libncursesw.so.5` est manquante :
```bash
sudo ln -s /lib/x86_64-linux-gnu/libncursesw.so.6 /lib/x86_64-linux-gnu/libncursesw.so.5
```

### 3.2 Configuration (my.cnf)

```bash
sudo nano /opt/mysql-5.0.96-linux-x86_64-glibc23/my.cnf
```

Contenu :
```
[mysqld]
lower_case_table_names=1
basedir=/opt/mysql-5.0.96-linux-x86_64-glibc23
datadir=/opt/mysql-5.0.96-linux-x86_64-glibc23/data

[mysqld_safe]
basedir=/opt/mysql-5.0.96-linux-x86_64-glibc23
```

### 3.3 Démarrage automatique (systemd)

```bash
sudo nano /etc/systemd/system/mysql5.service
```

Contenu :
```
[Unit]
Description=MySQL 5.0 Server
After=network.target

[Service]
Type=simple
ExecStart=/opt/mysql-5.0.96-linux-x86_64-glibc23/bin/mysqld_safe --defaults-file=/opt/mysql-5.0.96-linux-x86_64-glibc23/my.cnf --user=mysql
ExecStop=/opt/mysql-5.0.96-linux-x86_64-glibc23/bin/mysqladmin -u root -proot shutdown

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable mysql5
sudo systemctl start mysql5
```

### 3.4 Mot de passe root

```bash
/opt/mysql-5.0.96-linux-x86_64-glibc23/bin/mysql -u root
```

```sql
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('root');
FLUSH PRIVILEGES;
EXIT;
```

### 3.5 Import de la base

```bash
/opt/mysql-5.0.96-linux-x86_64-glibc23/bin/mysql -u root -proot < /chemin/vers/sauvgarde_donnees.sql
```

---

## 4. JBoss AS 7.1.1

### 4.1 Installation

```bash
sudo unzip ~/Téléchargements/jboss-as-7.1.1.Final.zip -d /opt/
sudo chown -R $USER:$USER /opt/jboss-as-7.1.1.Final
```

### 4.2 Copier les fichiers du projet

Depuis le dossier `src/main/jboss/` du projet, copier vers JBoss :

```bash
sudo cp -r src/main/jboss/modules/com /opt/jboss-as-7.1.1.Final/modules/
cp src/main/jboss/standalone/configuration/logging.properties /opt/jboss-as-7.1.1.Final/standalone/configuration/
cp src/main/jboss/standalone/configuration/mgmt-users.properties /opt/jboss-as-7.1.1.Final/standalone/configuration/
cp release/manyPrices.war /opt/jboss-as-7.1.1.Final/standalone/deployments/
```

### 4.3 Modifier standalone.conf

```bash
nano /opt/jboss-as-7.1.1.Final/bin/standalone.conf
```

Ajouter au début :
```bash
export JAVA_HOME=/opt/jdk1.6.0_45
export PATH=$JAVA_HOME/bin:$PATH
```

### 4.4 Modifier standalone.xml

Fichier : `/opt/jboss-as-7.1.1.Final/standalone/configuration/standalone.xml`

**Changer le format de date dans logging :**
```xml
<pattern-formatter pattern="%d{dd-MM-yyyy HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
```

**Ajouter le datasource** dans la section `<datasources>` :
```xml
<datasource jndi-name="java:jboss/datasources/manyPricesDatasource" pool-name="manyPricesDatasource_pool" enabled="true" use-java-context="true">
    <connection-url>jdbc:mysql://localhost:3306/ManyPrices</connection-url>
    <driver>mysql</driver>
    <security>
        <user-name>root</user-name>
        <password>root</password>
    </security>
</datasource>
```

**Ajouter le driver** dans la section `<drivers>` :
```xml
<driver name="mysql" module="com.mysql">
    <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
</driver>
```

### 4.5 Créer le dossier log

```bash
mkdir /opt/jboss-as-7.1.1.Final/standalone/log
```

### 4.6 Installer le JAR Maven

```bash
# Voir setup/mvn_install/cmd.txt pour la commande mvn install
```

---

## 5. Démarrage

```bash
/opt/jboss-as-7.1.1.Final/bin/standalone.sh
```

ManyPrices accessible sur : **http://localhost:8080/manyPrices**

---

## 6. Scripts utiles

| Script | Rôle |
|---|---|
| `setup/sauvegarde.sh` | Sauvegarde la base ManyPrices |
| `setup/import-sauvegarde.sh` | Restaure une sauvegarde |
| `src/main/scripts/create_triggers.sh` | Crée les triggers MySQL |
