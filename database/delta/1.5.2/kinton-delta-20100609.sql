-- Xen hypervisor is not compatible with following disk formats:
--   VMDK Sparse disk format
--   VHD Sparse disk format
--   VHD Flat disk format
--   RAW disk format
delete from hypervisor_disk_compatibilities where idHypervisor = 3 and idFormat = 5;
delete from hypervisor_disk_compatibilities where idHypervisor = 3 and idFormat = 6;
delete from hypervisor_disk_compatibilities where idHypervisor = 3 and idFormat = 7;
delete from hypervisor_disk_compatibilities where idHypervisor = 3 and idFormat = 1;

-- ESXi hypervisor is not compatible with RAW disk format
delete from hypervisor_disk_compatibilities where idHypervisor = 4 and idFormat = 1;

-- Hyper-V hypervisor is not compatible with RAW disk format
delete from hypervisor_disk_compatibilities where idHypervisor = 5 and idFormat = 1;
