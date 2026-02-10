update heroes
set acquisition = case
    when lower(grade) = 'sena' then '["영웅 합성","무한의 탑","교환 상점"]'::jsonb
    when lower(grade) in ('rare', 'legend', 'special') then '["영웅 소환","영웅 합성","조합"]'::jsonb
    else acquisition
end
where (acquisition is null or acquisition = '[]'::jsonb)
  and lower(grade) in ('rare', 'legend', 'special', 'sena');
