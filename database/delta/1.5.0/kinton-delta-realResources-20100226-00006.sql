/*
 * Add the new parameters real
 */
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `realram` int(7) NOT NULL after `hd`;
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `realcpu` int(11) NOT NULL after `realram`;
ALTER TABLE `kinton`.`physicalmachine` ADD COLUMN `realhd` int(20) NOT NULL after `realcpu`;


/*
 * Initialize the values
 */
update physicalmachine set realram = ram;
update physicalmachine set realcpu = cpu;
update physicalmachine set realhd = hd;