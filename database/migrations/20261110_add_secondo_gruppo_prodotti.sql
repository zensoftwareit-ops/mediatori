-- Secondo gruppo merceologico facoltativo per i prodotti.
ALTER TABLE `products`
  ADD COLUMN `groups2` varchar(255) DEFAULT NULL AFTER `groups`;
