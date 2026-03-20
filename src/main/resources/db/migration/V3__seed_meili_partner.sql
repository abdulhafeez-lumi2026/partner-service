INSERT INTO partner (
    partner_id, partner_code, name, contact_email,
    api_key_hash, client_id, client_secret_hash,
    status, rate_limit, commission_percentage,
    quote_mode, booking_mode,
    allowed_branches, allowed_vehicle_groups,
    webhook_url, ip_whitelist, contract_valid_until,
    version, created_by, updated_by
) VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'MEILI',
    'Meili Travel Technology',
    'tech@meili.com',
    -- api_key_hash: BCrypt of 'meili_apikey_2026' (dev/test only)
    '$2a$10$tvDOvMBYjajx6rCJ19I00u5OqlBFJ4eqeq9HlKqfR.TsCx9zBbIuO',
    'meili_prod_client',
    -- client_secret_hash: BCrypt of 'meili_secret_2026' (dev/test only)
    '$2a$10$9nBFMdb1a7BJUMU9N5IDF.EgXIgByeBBWb0jWiIT2jaOlbPrPpawG',
    'ACTIVE',
    120,
    10.00,
    'BOTH',
    'PAY_LATER',
    NULL,
    NULL,
    NULL,
    NULL,
    '2027-12-31',
    0,
    'system',
    'system'
);
