-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: facebook
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `password` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `avatar` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `token` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,0,'2022-10-31 23:17:06',1,NULL,1,'admin','$2a$12$oMm3D2Jds5bv/gDi2YZp5.H.U9QXM2hRzUfxsWThptk.BKeXI0dxS','0971339759',NULL,'9fdce537-eaa8-460a-9181-1f9feb06ba3e','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwOTcxMzM5NzU5IiwiaWF0IjoxNjY3Mzk3MzQxLCJleHAiOjE2Njc0ODM3NDF9.zCDR_vbbp8lGH2joyTVytZnVZHzB_pJJkKl9GOPjLASO94hnkCBnVvBw0zyPvxTZnanSdieBw6Kp5MJjqO1g8A'),(2,0,'2022-10-31 23:17:06',2,NULL,2,'Nguyễn Văn A','$2a$10$DN7C75oS8AXHJ/KTuMwuUOUHNIu0nm42sm6GJ48pNXNyHLJSLF9zS','0971339753',NULL,NULL,NULL),(3,0,'2022-10-31 23:17:06',3,NULL,3,'Nguyễn Văn B','$2a$10$rtHAEPLdvfBVABLA/bkjmuTCeCMC/1CKnEP0b/InHj/XxcmikJKdi','0368638976',NULL,'5c4727b1-7bde-4d92-9a85-cd20173a5c23',NULL);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blocks`
--

DROP TABLE IF EXISTS `blocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blocks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL,
  `created_on` datetime NOT NULL,
  `created_by` int NOT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `id_blocks` int NOT NULL,
  `id_blocked` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blocks`
--

LOCK TABLES `blocks` WRITE;
/*!40000 ALTER TABLE `blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE `blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `idA` int DEFAULT NULL,
  `idB` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat`
--

LOCK TABLES `chat` WRITE;
/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `post_id` int DEFAULT NULL,
  `account_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,0,'2022-11-02 22:23:14',NULL,NULL,NULL,'Đây là comment sửa lại ',2,1),(2,0,'2022-11-01 22:09:53',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(3,0,'2022-11-01 22:10:46',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(4,0,'2022-11-01 22:11:07',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(5,0,'2022-11-01 22:11:10',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(6,0,'2022-11-01 22:11:15',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(7,0,'2022-11-01 22:13:39',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(8,0,'2022-11-01 22:13:54',NULL,NULL,NULL,'Bình luận về bài viết một',1,1),(9,0,'2022-11-01 22:14:13',NULL,NULL,NULL,'Bình luận về bài viết một',1,1),(10,0,'2022-11-01 22:14:33',NULL,NULL,NULL,'Bình luận về bài viết một',1,1),(11,0,'2022-11-01 22:14:40',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(12,0,'2022-11-01 22:14:45',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(13,0,'2022-11-01 22:22:58',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(14,0,'2022-11-01 22:23:10',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(15,0,'2022-11-02 21:36:32',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(16,0,'2022-11-02 21:36:38',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(17,0,'2022-11-02 21:36:52',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(18,0,'2022-11-02 21:37:44',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(19,0,'2022-11-02 21:38:43',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(20,0,'2022-11-02 21:48:54',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(21,0,'2022-11-02 21:58:39',NULL,NULL,NULL,'Bình luận về bài viết một',2,1),(22,0,'2022-11-02 22:00:13',NULL,NULL,NULL,'Bình luận về bài viết một',2,1);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `post_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
INSERT INTO `file` VALUES (1,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:27:51',NULL,'1606x40 (2).png',3),(2,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:27:51',NULL,'720x720 (5).png',3),(3,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:28:37',NULL,'720x720 (5).png',3),(4,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:29:19',NULL,'720x720 (5).png',3),(5,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:33:38',NULL,'720x720 (5).png',3),(6,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:34:09',NULL,'720x720 (5).png',3),(7,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:38:16',NULL,'720x720 (5).png',3),(8,0,'2022-10-31 23:20:37',NULL,'2022-10-31 23:41:33',NULL,'720x720 (5).png',3),(9,0,'2022-11-02 20:57:40',NULL,NULL,NULL,'',7),(10,0,'2022-11-02 21:06:41',NULL,NULL,NULL,'',8);
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL,
  `created_on` datetime NOT NULL,
  `created_by` int NOT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `idA` int NOT NULL,
  `idB` int NOT NULL,
  `is_friend` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `post_id` int DEFAULT NULL,
  `account_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (1,0,'2022-11-01 21:23:55',2,NULL,NULL,2,2),(2,0,'2022-11-01 21:23:55',3,NULL,NULL,2,3),(7,0,'2022-11-01 21:23:55',1,NULL,NULL,1,1),(8,0,'2022-11-01 21:23:55',2,NULL,NULL,1,2),(9,0,'2022-11-01 21:23:55',3,NULL,NULL,1,3),(10,0,'2022-11-01 21:23:55',1,NULL,NULL,2,1);
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `account_id` int DEFAULT NULL,
  `can_comment` tinyint(1) DEFAULT NULL,
  `can_edit` tinyint(1) DEFAULT NULL,
  `banned` tinyint(1) DEFAULT NULL,
  `state` tinyint(1) DEFAULT NULL,
  `in_campaign` tinyint(1) DEFAULT NULL,
  `id_campaign` int DEFAULT NULL,
  `auto_accept` tinyint(1) DEFAULT NULL,
  `auto_block` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,0,'2022-10-16 18:05:33',1,'2022-10-16 18:05:33',NULL,'Bài đăng faceboook 1',1,1,1,0,1,0,0,0,0),(2,0,'2022-10-16 18:05:33',1,'2022-10-16 18:05:33',NULL,'Bài đăng faceboook Bài đăng faceboook Bài đăng faceboook  2',1,1,1,0,1,0,0,0,0),(3,0,'2022-10-16 18:05:33',1,'2022-10-16 18:05:33',NULL,'Nội dung bài đăng thứ 2: Một kì nghỉ đẹp!!',1,1,1,0,1,0,0,0,0),(4,0,'2022-10-16 18:05:33',2,'2022-10-16 18:05:33',NULL,'Nội dung bài đăng thứ 4: Một kì nghỉ hè thật đẹp!!',2,1,1,0,1,0,0,0,0),(5,0,'2022-11-02 20:55:53',1,NULL,NULL,'',1,0,0,0,0,0,NULL,0,0),(6,0,'2022-11-02 20:57:34',1,NULL,NULL,NULL,1,0,0,0,0,0,NULL,0,0),(7,0,'2022-11-02 20:57:40',1,NULL,NULL,'Bài viết này đã được edit ',1,0,0,0,0,0,NULL,1,0);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `account_id` int DEFAULT NULL,
  `post_id` int DEFAULT NULL,
  `type_report_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (1,0,'2022-10-16 20:56:29',1,NULL,NULL,'Nội dung sai phạm',1,1,1),(2,0,'2022-10-16 20:57:50',1,NULL,NULL,'Nội dung sai phạm',1,1,1);
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type_report`
--

DROP TABLE IF EXISTS `type_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_report`
--

LOCK TABLES `type_report` WRITE;
/*!40000 ALTER TABLE `type_report` DISABLE KEYS */;
INSERT INTO `type_report` VALUES (1,0,'2022-10-16 18:05:33',1,NULL,0,'Báo cáo nội dung sai phạm');
/*!40000 ALTER TABLE `type_report` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-02 22:28:45
