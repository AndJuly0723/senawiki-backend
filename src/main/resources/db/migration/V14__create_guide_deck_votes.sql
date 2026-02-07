create table if not exists guide_deck_votes (
    id bigserial primary key,
    deck_id bigint not null,
    user_id bigint not null,
    vote_type varchar(10) not null,
    created_at timestamp not null default now(),
    constraint fk_guide_deck_votes_deck
        foreign key (deck_id) references guide_decks (id) on delete cascade,
    constraint fk_guide_deck_votes_user
        foreign key (user_id) references users (id) on delete restrict,
    constraint uq_guide_deck_votes_deck_user
        unique (deck_id, user_id),
    constraint chk_guide_deck_votes_type
        check (vote_type in ('UP', 'DOWN'))
);

create index if not exists idx_guide_deck_votes_deck on guide_deck_votes (deck_id);
