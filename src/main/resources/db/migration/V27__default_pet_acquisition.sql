update pets
set acquisition = '["소환","합성"]'::jsonb
where acquisition is null
   or acquisition = '[]'::jsonb;
