-- Add rate_type column to control B2C/B2B pricing pipeline routing
-- B2C partners use DEFAULT account (Lumi B2C rates)
-- B2B partners use their debtor_code for rate lookup

ALTER TABLE partner ADD COLUMN rate_type VARCHAR(10) NOT NULL DEFAULT 'B2C';

-- Remove promo_code column - promotions are now managed in pricing-service
-- via partner_code on the promotion table

ALTER TABLE partner DROP COLUMN promo_code;

-- Update existing Meili partner to B2C
UPDATE partner SET rate_type = 'B2C' WHERE partner_code = 'MEILI';
