-- Eseguire dopo 20261019_add_nichel_analisi.sql.
-- Le analisi storiche vengono associate all'annata di semina attualmente configurata.

ALTER TABLE `analisi`
    ADD COLUMN `DATA_ANALISI` date DEFAULT NULL AFTER `IDADMIN`,
    ADD COLUMN `ID_SOWING_YEAR` int(11) DEFAULT NULL AFTER `DATA_ANALISI`;

UPDATE `analisi`
SET `ID_SOWING_YEAR` = (SELECT `id_sowing_year` FROM `config` ORDER BY `id` LIMIT 1)
WHERE `ID_SOWING_YEAR` IS NULL;

ALTER TABLE `analisi`
    MODIFY COLUMN `ID_SOWING_YEAR` int(11) NOT NULL;

CREATE INDEX `idx_analisi_sowing_year`
    ON `analisi` (`ID_SOWING_YEAR`);

CREATE INDEX `idx_analisi_ricerca`
    ON `analisi` (`ID_SOWING_YEAR`, `CLIENTE`, `PRODOTTO`);
