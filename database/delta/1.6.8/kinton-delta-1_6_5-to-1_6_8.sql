--
-- Add initiatorIQN colume to PhysicalMachine
--
ALTER TABLE `kinton`.`physicalmachine` ADD `initiatorIQN` varchar(256) DEFAULT NULL;

--
-- Delete BPM tables and triggers
--
DROP TABLE IF EXISTS `kinton`.`bpm_event_status`;
DROP TABLE IF EXISTS `kinton`.`bpm_event_status_history`;
DROP TRIGGER IF EXISTS `kinton`.`bpm_event_status_after_ins_tr`;
DROP TRIGGER IF EXISTS `kinton`.`bpm_event_status_after_upd_tr`;

--
-- Delete Sparse compatibillity for KVM
--
DELETE FROM `kinton`.`hypervisor_disk_compatibilities` WHERE idHypervisor = 2 AND idFormat = 5;
