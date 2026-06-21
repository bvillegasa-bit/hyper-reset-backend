-- ============================================
-- Migration V3: Allow NULL coach in test_fisico
-- DEPORTISTA self-testing doesn't always have
-- an assigned coach record.
-- ============================================

ALTER TABLE test_fisico
    MODIFY COLUMN id_coach BIGINT NULL;

-- Also update schema.sql reference for fresh installs
