/*This scripts updates idUserCreation column in order to allow null*/

ALTER TABLE `kinton`.`virtualimage` MODIFY COLUMN `idUserCreation` INTEGER UNSIGNED DEFAULT NULL,
 MODIFY COLUMN `creationDate` DATETIME  DEFAULT NULL;