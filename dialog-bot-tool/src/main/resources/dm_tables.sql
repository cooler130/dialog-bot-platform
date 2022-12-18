-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: dm_burouter2
-- ------------------------------------------------------
-- Server version	5.7.20-log

use dm_test;

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
-- Table structure for table `param`
--

DROP TABLE IF EXISTS `param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `param` (
  `id` int(11) NOT NULL COMMENT 'ID',
  `domain_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '领域名',
  `task_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '任务名',
  `param_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '变量名',
  `acquire_type` int(11) NOT NULL COMMENT '变量值获取方式（1.通过脚本计算获取；2.通过http接口调用获取；）',
  `group_num` int(11) NOT NULL COMMENT '次序号',
  `acquire_content` varchar(9000) COLLATE utf8_bin NOT NULL COMMENT '变量获取命令',
  `enable` int(11) NOT NULL COMMENT '-1,禁用；0，删除；1启用',
  `msg` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `policy`
--

DROP TABLE IF EXISTS `policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy` (
  `id` int(11) NOT NULL COMMENT '主键',
  `policy_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '策略名称',
  `domain_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '领域名称',
  `task_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '任务名称',
  `from_state` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '起始状态',
  `intent_names` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '1' COMMENT '意图名称（可多个，逗号隔开）',
  `to_state` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '到达状态',
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '-1，禁用，0，删除，1，启用',
  `msg` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '解释信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy`
--

--
-- Temporary view structure for view `policy_1n_policy_action`
--

DROP TABLE IF EXISTS `policy_1n_policy_action`;
/*!50001 DROP VIEW IF EXISTS `policy_1n_policy_action`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `policy_1n_policy_action` AS SELECT 
 1 AS `id`,
 1 AS `policy_name`,
 1 AS `domain_name`,
 1 AS `task_name`,
 1 AS `from_state`,
 1 AS `intent_names`,
 1 AS `to_state`,
 1 AS `enable`,
 1 AS `pa_ids`,
 1 AS `pa_action_contents`,
 1 AS `group_nums`,
 1 AS `action_types`,
 1 AS `pa_enables`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `policy_1n_policy_condition`
--

DROP TABLE IF EXISTS `policy_1n_policy_condition`;
/*!50001 DROP VIEW IF EXISTS `policy_1n_policy_condition`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `policy_1n_policy_condition` AS SELECT 
 1 AS `id`,
 1 AS `policy_name`,
 1 AS `domain_name`,
 1 AS `task_name`,
 1 AS `from_state`,
 1 AS `intent_names`,
 1 AS `to_state`,
 1 AS `enable`,
 1 AS `pc_conditions`,
 1 AS `condition_texts`,
 1 AS `pc_enables`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `policy_action`
--

DROP TABLE IF EXISTS `policy_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy_action` (
  `id` int(11) NOT NULL COMMENT '处理动作ID',
  `action_name` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '动作名称',
  `policy_id` int(11) NOT NULL COMMENT '关联的策略ID',
  `action_type` int(11) NOT NULL COMMENT '动作类型（1.变量处理动作；2.http接口调用动作；3.交互动作；）',
  `group_num` int(11) NOT NULL COMMENT '次序号',
  `action_content` varchar(9000) COLLATE utf8_bin NOT NULL COMMENT '处理代码（根据它在项目中查找对应的处理函数）',
  `enable` int(11) NOT NULL COMMENT '-1,禁用；0，删除；1启用',
  `msg` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '动作说明信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_action_name` (`action_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_action`
--

--
-- Table structure for table `policy_condition`
--

DROP TABLE IF EXISTS `policy_condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy_condition` (
  `id` int(11) NOT NULL COMMENT '主键',
  `policy_id` int(11) NOT NULL COMMENT '策略ID',
  `condition_name` varchar(45) COLLATE utf8_bin NOT NULL,
  `condition_whether` tinyint(4) NOT NULL DEFAULT '1' COMMENT '此condition的逻辑关系：\n1，是；0，是；-1，否',
  `condition_text` varchar(45) COLLATE utf8_bin NOT NULL COMMENT '条件字符串',
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1，启用；0，删除；-1，禁用',
  `msg` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '条件信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_condition`
--

--
-- Table structure for table `transform_relation`
--

DROP TABLE IF EXISTS `transform_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transform_relation` (
  `id` int(11) NOT NULL COMMENT '转换关系ID',
  `transform_relation_name` varchar(45) NOT NULL COMMENT '转换关系名称',
  `domain_name` varchar(20) NOT NULL,
  `task_name` varchar(20) NOT NULL,
  `context_state` varchar(20) NOT NULL COMMENT '语境状态ID',
  `intent_names` varchar(100) NOT NULL COMMENT '语境意图名称集合，以逗号相隔',
  `transform_intent_name` varchar(20) NOT NULL COMMENT '转换意图名称',
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态（1，启用；0，删除；-1，禁用）',
  `msg` varchar(100) DEFAULT NULL COMMENT '信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transform_relation`
--

LOCK TABLES `transform_relation` WRITE;
/*!40000 ALTER TABLE `transform_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `transform_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `policy_1n_policy_action`
--

/*!50001 DROP VIEW IF EXISTS `policy_1n_policy_action`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `policy_1n_policy_action` AS select `p`.`id` AS `id`,`p`.`policy_name` AS `policy_name`,`p`.`domain_name` AS `domain_name`,`p`.`task_name` AS `task_name`,`p`.`from_state` AS `from_state`,`p`.`intent_names` AS `intent_names`,`p`.`to_state` AS `to_state`,`p`.`enable` AS `enable`,group_concat(`pa`.`id` order by `pa`.`id` ASC separator ',') AS `pa_ids`,group_concat(`pa`.`action_content` order by `pa`.`id` ASC separator ',') AS `pa_action_contents`,group_concat(`pa`.`group_num` separator ' , ') AS `group_nums`,group_concat(`pa`.`action_type` separator ' , ') AS `action_types`,group_concat(`pa`.`enable` separator ' , ') AS `pa_enables` from (`policy` `p` left join `policy_action` `pa` on((`p`.`id` = `pa`.`policy_id`))) group by `p`.`id` order by `p`.`domain_name`,`p`.`task_name`,`p`.`from_state`,`p`.`intent_names`,`p`.`to_state` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `policy_1n_policy_condition`
--

/*!50001 DROP VIEW IF EXISTS `policy_1n_policy_condition`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `policy_1n_policy_condition` AS select `p`.`id` AS `id`,`p`.`policy_name` AS `policy_name`,`p`.`domain_name` AS `domain_name`,`p`.`task_name` AS `task_name`,`p`.`from_state` AS `from_state`,`p`.`intent_names` AS `intent_names`,`p`.`to_state` AS `to_state`,`p`.`enable` AS `enable`,group_concat(concat(`pc`.`condition_name`,(case `pc`.`condition_whether` when 1 then ' (Y)' when 0 then '' when -(1) then ' (N)' end)) separator ' , ') AS `pc_conditions`,group_concat(concat(`pc`.`condition_text`,(case `pc`.`condition_whether` when 1 then ' (Y)' when 0 then '' when -(1) then ' (N)' end)) separator ' , ') AS `condition_texts`,group_concat(`pc`.`enable` separator ' , ') AS `pc_enables` from (`policy` `p` left join `policy_condition` `pc` on((`p`.`id` = `pc`.`policy_id`))) group by `p`.`id` order by `p`.`domain_name`,`p`.`task_name`,`p`.`from_state`,`p`.`intent_names`,`p`.`to_state` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-16 23:15:43
