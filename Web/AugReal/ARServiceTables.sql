CREATE DATABASE  IF NOT EXISTS `AugReal` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `AugReal`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: 160.40.50.199    Database: AugReal
-- ------------------------------------------------------
-- Server version	5.6.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `AugReal_ARApp`
--

DROP TABLE IF EXISTS `AugReal_ARApp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AugReal_ARApp` (
  `id_app` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(400) NOT NULL,
  PRIMARY KEY (`id_app`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AugReal_ARApp`
--

LOCK TABLES `AugReal_ARApp` WRITE;
/*!40000 ALTER TABLE `AugReal_ARApp` DISABLE KEYS */;
INSERT INTO `AugReal_ARApp` VALUES (0,'Unclassified'),(1,'My Application Topic'),(2,'Urban Planning');
/*!40000 ALTER TABLE `AugReal_ARApp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AugReal_ARMain`
--

DROP TABLE IF EXISTS `AugReal_ARMain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AugReal_ARMain` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'This is the INDEX',
  `id_author` varchar(100) NOT NULL COMMENT 'sc id',
  `id_coauthor` varchar(100) NOT NULL,
  `id_external` int(11) NOT NULL DEFAULT '0',
  `id_app` int(11) NOT NULL,
  `id_VRec` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `titleB` varchar(200) NOT NULL,
  `titleC` varchar(200) NOT NULL,
  `titleD` varchar(200) NOT NULL,
  `description` varchar(2000) NOT NULL,
  `descriptionB` varchar(2000) NOT NULL,
  `descriptionC` varchar(2000) NOT NULL,
  `descriptionD` varchar(2000) NOT NULL,
  `author` varchar(500) NOT NULL,
  `models` tinyint(1) NOT NULL,
  `jpg` tinyint(1) NOT NULL,
  `trackImRot` int(11) NOT NULL,
  `latitude` varchar(10) NOT NULL,
  `longitude` varchar(10) NOT NULL,
  `altitude` varchar(10) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `streetnameaddress` varchar(150) NOT NULL,
  `numberaddress` int(5) NOT NULL,
  `postalcode` varchar(10) NOT NULL,
  `country` varchar(20) NOT NULL,
  `linkurl` varchar(600) NOT NULL,
  `Langs` varchar(100) NOT NULL,
  `trackImScale` decimal(10,0) NOT NULL DEFAULT '30',
  `androidapp` varchar(145) DEFAULT NULL,
  `iphoneapp` varchar(145) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_2` (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8 COMMENT='This is the table where all constructions are inserted';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AugReal_ARMain`
--

LOCK TABLES `AugReal_ARMain` WRITE;
/*!40000 ALTER TABLE `AugReal_ARMain` DISABLE KEYS */;
INSERT INTO `AugReal_ARMain` VALUES (1,'8','\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		\n		',12345,2,0,'Trash bin','','','   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\n		','View candidate trash bins.','','','   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n   \r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\r\n		\n		','Dimitrios Ververidis',2,1,0,'40.5667635','22.9983691','140.00001','2013-01-01 04:35:22','Κapetan Χapsa',46,'570 01','Greece      							\n','                                 																																										\n		','en;es;eu;dut',30,NULL,NULL);
/*!40000 ALTER TABLE `AugReal_ARMain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AugReal_ARImages`
--

DROP TABLE IF EXISTS `AugReal_ARImages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AugReal_ARImages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(120) NOT NULL,
  `name` varchar(30) NOT NULL,
  `date` datetime NOT NULL,
  `image_count` int(11) DEFAULT NULL,
  `locked` tinyint(1) DEFAULT NULL,
  `features_available` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=439 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AugReal_ARImages`
--

LOCK TABLES `AugReal_ARImages` WRITE;
/*!40000 ALTER TABLE `AugReal_ARImages` DISABLE KEYS */;
INSERT INTO `AugReal_ARImages` VALUES (126,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Building','Building','2013-07-05 10:52:48',3687,0,1),(127,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Car','Car','2013-07-05 10:56:01',3651,0,1),(129,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Person','Person','2013-07-05 11:00:22',3638,0,1),(130,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Tree','Tree','2013-07-05 11:01:56',3658,0,1),(143,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Desk','Desk','2013-07-15 15:28:18',3665,0,1),(302,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/dimetrodon','dimetrodon','2013-07-26 09:44:32',70,0,1),(306,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/Dog','Dog','2013-07-26 11:22:18',10,0,1),(307,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/door','door','2013-07-26 13:48:07',996,0,1),(324,'/opt/lampp/htdocs/xampp/joomla/VisRec/ReconEngine/content/chair','chair','2013-07-31 16:22:31',10,0,1),(437,'ReconEngine/content/helicopter','helicopter','2013-12-24 13:55:42',3,0,0);
/*!40000 ALTER TABLE `AugReal_ARImages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AugReal_ARFileHashes`
--

DROP TABLE IF EXISTS `AugReal_ARFileHashes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AugReal_ARFileHashes` (
  `id` varchar(20) NOT NULL DEFAULT '0',
  `filename` varchar(345) DEFAULT NULL,
  `hash` varchar(105) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL COMMENT 'Date of upload or edit',
  `size` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Create hashes for each file uploaded so that it can be compared with previous versions';
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `AugReal_ARModels`
--

DROP TABLE IF EXISTS `AugReal_ARModels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AugReal_ARModels` (
  `name` varchar(30) NOT NULL,
  `path` varchar(120) NOT NULL,
  `classes_positive` text NOT NULL,
  `classes_negative` text NOT NULL,
  `date` date NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `locked` tinyint(1) DEFAULT NULL,
  `threshold` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-11-14 15:37:58
