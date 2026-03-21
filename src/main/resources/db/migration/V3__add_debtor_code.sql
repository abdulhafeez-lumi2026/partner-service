ALTER TABLE partner ADD COLUMN debtor_code VARCHAR(50) NOT NULL AFTER contact_email;
CREATE INDEX idx_partner_debtor_code ON partner(debtor_code);
