update heroes
set type_icon = case
    when type = 'attack' then '/images/types/attack.png'
    when type = 'defense' then '/images/types/defense.png'
    when type = 'magic' then '/images/types/magic.png'
    when type = 'support' then '/images/types/support.png'
    when type = 'allround' then '/images/types/allround.png'
    else type_icon
end
where type in ('attack', 'defense', 'magic', 'support', 'allround')
  and (type_icon is null or type_icon = '');
