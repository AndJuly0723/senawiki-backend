alter table guide_decks
    add column if not exists raid_id varchar(50),
    add column if not exists stage_id varchar(50);
