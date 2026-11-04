-- Logo documentale corrente dell'utente e fotografia del logo sui contratti.
ALTER TABLE `admins`
  ADD COLUMN `document_logo` VARCHAR(255) NULL AFTER `invoice_template`;

UPDATE `admins`
SET `document_logo` = 'logo_ghisoni_new.jpg'
WHERE `id` = 2;

ALTER TABLE `sales`
  ADD COLUMN `contract_logo` VARCHAR(255) NULL AFTER `sale_type`;

-- Tutti i contratti Ghisoni già presenti restano legati al logo storico.
UPDATE `sales`
SET `contract_logo` = 'logo_ghisoni.jpg'
WHERE `id_admin` = 2;
