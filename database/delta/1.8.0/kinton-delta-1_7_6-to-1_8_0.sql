-- [ABICLOUDPREMIUM-1490] Volumes are attached directly. Reserved state disappears.
update volume_management set state = 1 where state = 2;
