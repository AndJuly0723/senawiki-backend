alter table guide_decks
    add column if not exists expedition_id varchar(50);

create index if not exists idx_guide_decks_expedition
    on guide_decks (guide_type, expedition_id);
