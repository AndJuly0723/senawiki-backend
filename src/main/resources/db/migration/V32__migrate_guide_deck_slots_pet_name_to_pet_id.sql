alter table if exists guide_deck_slots
    add column if not exists pet_id varchar(50);

update guide_deck_slots s
set pet_id = p.id
from pets p
where s.is_pet = true
  and s.pet_id is null
  and s.hero_id is null
  and s.pet_name is not null
  and (s.pet_name = p.id or s.pet_name = p.name);

alter table if exists guide_deck_slots
    drop constraint if exists fk_guide_deck_slots_pet;

alter table if exists guide_deck_slots
    add constraint fk_guide_deck_slots_pet
        foreign key (pet_id) references pets (id) on delete restrict;

alter table if exists guide_deck_slots
    drop constraint if exists chk_guide_deck_slots_owner;

alter table if exists guide_deck_slots
    add constraint chk_guide_deck_slots_owner
        check (
            (is_pet = true and pet_id is not null and hero_id is null)
            or
            (is_pet = false and hero_id is not null and pet_id is null)
        );

alter table if exists guide_deck_slots
    drop column if exists pet_name;
