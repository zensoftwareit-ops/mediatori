-- Migrazione database per Mediarice 1.7.5
-- Eseguire una sola volta, prima del deploy del WAR.

ALTER TABLE `analisi`
    ADD COLUMN `NICHEL` varchar(255) DEFAULT NULL AFTER `ARSENICOINORGANICO`,
    ADD COLUMN `NICHELFL` varchar(255) NOT NULL DEFAULT 'NO' AFTER `ARSENICOFL`;
