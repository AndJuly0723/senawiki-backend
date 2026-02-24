alter table guide_decks
    add column if not exists counter_parent_deck_id bigint;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_guide_decks_counter_parent'
    ) then
        alter table guide_decks
            add constraint fk_guide_decks_counter_parent
                foreign key (counter_parent_deck_id) references guide_decks (id) on delete set null;
    end if;
end
$$;

create index if not exists idx_guide_decks_counter_parent
    on guide_decks (counter_parent_deck_id);
