-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: expense_tracker_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `expense_participants`
--

DROP TABLE IF EXISTS `expense_participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expense_participants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expense_id` int NOT NULL,
  `user_id` varchar(20) NOT NULL,
  `share_amount` decimal(10,2) NOT NULL,
  `is_settled` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_expense_participant` (`expense_id`,`user_id`),
  KEY `fk_ep_user` (`user_id`),
  CONSTRAINT `fk_ep_expense` FOREIGN KEY (`expense_id`) REFERENCES `expenses` (`expense_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ep_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`phone_number`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_share_amount` CHECK ((`share_amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expense_participants`
--

LOCK TABLES `expense_participants` WRITE;
/*!40000 ALTER TABLE `expense_participants` DISABLE KEYS */;
INSERT INTO `expense_participants` VALUES (1,1,'1234567890',25.00,0),(2,1,'9999999999',25.00,0),(3,2,'1234567890',5000.00,0),(4,2,'9999999999',1000.00,0),(5,3,'1234567890',6000.00,0),(6,3,'9999999999',4000.00,0),(7,4,'1234567890',500.00,0),(8,4,'9999999999',500.00,0),(9,5,'1234567890',1100.00,0),(10,5,'1111111111',900.00,0),(11,5,'9999999999',1000.00,0);
/*!40000 ALTER TABLE `expense_participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expenses`
--

DROP TABLE IF EXISTS `expenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expenses` (
  `expense_id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `paid_by` varchar(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `description` varchar(255) NOT NULL,
  `split_type` enum('EQUAL','CUSTOM') DEFAULT 'EQUAL',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`expense_id`),
  KEY `idx_expenses_group` (`group_id`),
  KEY `idx_expenses_paid_by` (`paid_by`),
  CONSTRAINT `fk_expense_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_expense_paid_by` FOREIGN KEY (`paid_by`) REFERENCES `users` (`phone_number`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_expense_amount` CHECK ((`amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expenses`
--

LOCK TABLES `expenses` WRITE;
/*!40000 ALTER TABLE `expenses` DISABLE KEYS */;
INSERT INTO `expenses` VALUES (1,1,'9999999999',50.00,'dafs','EQUAL','2026-02-01 09:58:25'),(2,1,'1234567890',10000.00,'hkgkjghj','CUSTOM','2026-02-01 10:12:06'),(3,1,'9999999999',10000.00,'DAVSVSF','CUSTOM','2026-02-01 10:14:21'),(4,1,'1234567890',1000.00,'wesgvfsgd','EQUAL','2026-02-01 10:16:04'),(5,1,'1234567890',3000.00,'sjkdgusfh','CUSTOM','2026-02-01 10:19:03');
/*!40000 ALTER TABLE `expenses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `user_id` varchar(20) NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_member` (`group_id`,`user_id`),
  KEY `fk_gm_user` (`user_id`),
  CONSTRAINT `fk_gm_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_gm_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`phone_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
INSERT INTO `group_members` VALUES (1,1,'1234567890','2026-02-01 09:49:39'),(2,1,'9999999999','2026-02-01 09:58:00'),(3,1,'1111111111','2026-02-01 10:17:38');
/*!40000 ALTER TABLE `group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `groups` (
  `group_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `created_by` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`),
  KEY `fk_groups_created_by` (`created_by`),
  CONSTRAINT `fk_groups_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`phone_number`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (1,'Test Persistence Group','Verifying DB','1234567890','2026-02-01 09:49:39');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlements`
--

DROP TABLE IF EXISTS `settlements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settlements` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `from_user` varchar(20) NOT NULL,
  `to_user` varchar(20) NOT NULL,
  `net_balance` decimal(10,2) DEFAULT '0.00',
  `status` enum('PENDING','PARTIAL','SETTLED') DEFAULT 'PENDING',
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement` (`group_id`,`from_user`,`to_user`),
  KEY `fk_settle_from` (`from_user`),
  KEY `fk_settle_to` (`to_user`),
  CONSTRAINT `fk_settle_from` FOREIGN KEY (`from_user`) REFERENCES `users` (`phone_number`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_settle_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_settle_to` FOREIGN KEY (`to_user`) REFERENCES `users` (`phone_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlements`
--

LOCK TABLES `settlements` WRITE;
/*!40000 ALTER TABLE `settlements` DISABLE KEYS */;
INSERT INTO `settlements` VALUES (1,1,'1234567890','9999999999',0.00,'SETTLED','2026-02-01 10:15:20'),(2,1,'9999999999','1234567890',1000.00,'PENDING','2026-02-01 10:19:03'),(3,1,'1111111111','1234567890',900.00,'PENDING','2026-02-01 10:19:03');
/*!40000 ALTER TABLE `settlements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `from_user` varchar(20) NOT NULL,
  `to_user` varchar(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `expense_id` int DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transaction_id`),
  KEY `fk_trans_expense` (`expense_id`),
  KEY `idx_trans_from` (`from_user`),
  KEY `idx_trans_to` (`to_user`),
  CONSTRAINT `fk_trans_expense` FOREIGN KEY (`expense_id`) REFERENCES `expenses` (`expense_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_trans_from` FOREIGN KEY (`from_user`) REFERENCES `users` (`phone_number`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_trans_to` FOREIGN KEY (`to_user`) REFERENCES `users` (`phone_number`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_transaction_amount` CHECK ((`amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,'1234567890','9999999999',10000.00,NULL,'','2026-02-01 10:10:32'),(2,'1234567890','9999999999',100000.00,NULL,'','2026-02-01 10:11:00'),(3,'9999999999','1234567890',10000.00,NULL,'','2026-02-01 10:12:49'),(4,'1234567890','9999999999',6000.00,NULL,'','2026-02-01 10:15:20'),(5,'9999999999','1234567890',500.00,NULL,'','2026-02-01 10:16:32');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `phone_number` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phone_number`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1111111111','qwlkndreaw','asflkdsk@gmail.com','2026-02-01 10:17:38'),('1234567890','Test User','test@example.com','2026-02-01 09:48:53'),('9999999999','ssfdada','memeber@gmail.com','2026-02-01 09:58:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-01 16:15:59
