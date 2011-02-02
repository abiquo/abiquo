ALTER TABLE `hosttype` ADD COLUMN `configureGateway` boolean NOT NULL default 1;

UPDATE `hosttype` SET `configureGateway`=1;