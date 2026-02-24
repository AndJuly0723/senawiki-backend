alter table guide_decks
    drop constraint if exists fk_guide_decks_counter_parent;

alter table guide_decks
    add constraint fk_guide_decks_counter_parent
        foreign key (counter_parent_deck_id) references guide_decks (id) on delete cascade;
