-- Aggiornamento cumulativo database per mediarice 1.7.8.
-- Lo script e' rieseguibile: aggiunge solo colonne e indici mancanti.

SET @schema_name = DATABASE();

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND COLUMN_NAME='NICHEL') = 0,
  'ALTER TABLE `analisi` ADD COLUMN `NICHEL` varchar(255) DEFAULT NULL AFTER `ARSENICOINORGANICO`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND COLUMN_NAME='NICHELFL') = 0,
  'ALTER TABLE `analisi` ADD COLUMN `NICHELFL` varchar(255) NOT NULL DEFAULT ''NO'' AFTER `ARSENICOFL`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND COLUMN_NAME='DATA_ANALISI') = 0,
  'ALTER TABLE `analisi` ADD COLUMN `DATA_ANALISI` date DEFAULT NULL AFTER `IDADMIN`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND COLUMN_NAME='ID_SOWING_YEAR') = 0,
  'ALTER TABLE `analisi` ADD COLUMN `ID_SOWING_YEAR` int(11) DEFAULT NULL AFTER `DATA_ANALISI`',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE `analisi`
SET `ID_SOWING_YEAR` = (SELECT `id_sowing_year` FROM `config` ORDER BY `id` LIMIT 1)
WHERE `ID_SOWING_YEAR` IS NULL;

ALTER TABLE `analisi` MODIFY COLUMN `ID_SOWING_YEAR` int(11) NOT NULL;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND INDEX_NAME='idx_analisi_sowing_year') = 0,
  'CREATE INDEX `idx_analisi_sowing_year` ON `analisi` (`ID_SOWING_YEAR`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=@schema_name AND TABLE_NAME='analisi' AND INDEX_NAME='idx_analisi_ricerca') = 0,
  'CREATE INDEX `idx_analisi_ricerca` ON `analisi` (`ID_SOWING_YEAR`, `CLIENTE`, `PRODOTTO`)',
  'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
