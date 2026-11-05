-- Rollback dell'importazione IMPORT_WL_20261105 per IDADMIN=2.
-- Richiede che l'importazione sia stata eseguita con la versione che crea
-- e popola la tabella import_analisi_audit.
-- Elimina esclusivamente gli ID registrati durante quell'importazione.

START TRANSACTION;

SET @analisi_da_rimuovere = (
  SELECT COUNT(*)
  FROM `import_analisi_audit`
  WHERE `batch_id`='IMPORT_WL_20261105' AND `entity_type`='ANALISI'
);

SET @clienti_da_valutare = (
  SELECT COUNT(*)
  FROM `import_analisi_audit`
  WHERE `batch_id`='IMPORT_WL_20261105' AND `entity_type`='CUSTOMER'
);

SELECT @analisi_da_rimuovere AS analisi_registrate,
       @clienti_da_valutare AS clienti_registrati;

DELETE a
FROM `analisi` a
INNER JOIN `import_analisi_audit` audit
        ON audit.`entity_id`=a.`ID`
       AND audit.`batch_id`='IMPORT_WL_20261105'
       AND audit.`entity_type`='ANALISI'
WHERE a.`IDADMIN`=2;

SET @analisi_rimosse = ROW_COUNT();

-- I clienti creati dall'importazione vengono eliminati soltanto se non sono
-- stati nel frattempo utilizzati da analisi, contratti, fatture o campioni.
DELETE c
FROM `customers` c
INNER JOIN `import_analisi_audit` audit
        ON audit.`entity_id`=c.`id`
       AND audit.`batch_id`='IMPORT_WL_20261105'
       AND audit.`entity_type`='CUSTOMER'
WHERE c.`id_admin`=2
  AND NOT EXISTS (SELECT 1 FROM `analisi` a WHERE a.`CLIENTE`=c.`id`)
  AND NOT EXISTS (SELECT 1 FROM `sales` s WHERE s.`id_seller`=c.`id` OR s.`id_buyer`=c.`id`)
  AND NOT EXISTS (SELECT 1 FROM `invoices` i WHERE i.`id_customer`=c.`id`)
  AND NOT EXISTS (SELECT 1 FROM `yield_measures` y WHERE y.`id_customer`=c.`id`);

SET @clienti_rimossi = ROW_COUNT();

DELETE audit
FROM `import_analisi_audit` audit
LEFT JOIN `analisi` a
       ON audit.`entity_type`='ANALISI' AND audit.`entity_id`=a.`ID`
LEFT JOIN `customers` c
       ON audit.`entity_type`='CUSTOMER' AND audit.`entity_id`=c.`id`
WHERE audit.`batch_id`='IMPORT_WL_20261105'
  AND (
       (audit.`entity_type`='ANALISI' AND a.`ID` IS NULL)
       OR
       (audit.`entity_type`='CUSTOMER' AND c.`id` IS NULL)
  );

SELECT @analisi_rimosse AS analisi_rimosse,
       @clienti_rimossi AS clienti_rimossi,
       (SELECT COUNT(*)
        FROM `import_analisi_audit`
        WHERE `batch_id`='IMPORT_WL_20261105') AS elementi_non_rimossi;

COMMIT;
