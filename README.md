Like Manyprices, this document is available in two languages English and French).
Tout comme Manyprices, ce document est disponible en 2 langues (français et anglais), cliquez [ici](http://example.com/ "Français") si vous etes plus a l'aise avec le français

Description
===========

A small CRM that focus on price comparison of a product for different customers 

-![alt text](http://i47.tinypic.com/24wyq21.jpg)

Building and running
====================
  persistence properties are set to :
  
    jta-data-source=java:jboss/datasources/manyPricesDatasource
    hibernate.dialect=org.hibernate.dialect.MySQLDialect
    hibernate.hbm2ddl.auto=update
    
  you must create the datasource java:jboss/datasources/manyPricesDatasource under jboss as7

###Using forge

        $ forge
        $ build
        $ as7 deploy
        
###Using maven 3
        $ mvn clean package
        $ mvn jboss-as:deploy
        
Introduction
============
C'est l'histoire d'un commerçant en quincaillerie qui s'appel Hedi qui avait un magasin ou il vendait tout plein d'article et avait plusieurs fournisseurs qui fournissait les memes articles.
les prix d'achat changait souvent et sont different entre les diferents fournisseurs, a tel point que hedi avait du mal a retenir tous les prix d'achat de ces fournisseurs et a trouver le fournisseur qui propose le meilleur prix pour un article donné.

Tache
=====
Notre but est d'ecrire un programme qui aide Mr Hedi a gérer ces fournisseurs et produits, qui lui permet de trouver rapidement pour un produit donné tous les prix d'achat possibles et qui l'aide a trouver le fournisseur qui propose le meilleur prix

The Problem
===========
One of their current problems is printing price tags on their fresh produce.
Every morning, as soon as the various produce have been delivered,
Mrs. Hollingberry enters it into a program her nephew had written.

The result is a comma-seperated file that includes, among other
fields, the cost price (in cents) and delivery date of each product.

The Task
========
Your job is to write a program that reads the csv file and then creates
a new file that will be used to print out the price tags.

