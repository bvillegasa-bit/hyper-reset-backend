-- ============================================
-- Migration V2: Add tipo_test to test_fisico
-- ============================================
--
-- Rollback:
--   ALTER TABLE test_fisico DROP CONSTRAINT ck_test_tipo;
--   ALTER TABLE test_fisico DROP COLUMN tipo_test;
-- ============================================

-- Step 1: Add column (nullable initially for existing data)
ALTER TABLE test_fisico
    ADD COLUMN tipo_test VARCHAR(30) NULL
    AFTER antecedentes_medicos;

-- Step 2: Add CHECK constraint for valid TipoTest values
ALTER TABLE test_fisico
    ADD CONSTRAINT ck_test_tipo CHECK (
        tipo_test IN (
            'ILLINOIS',
            'FLEXION_CODOS',
            'VELOCIDAD_20M',
            'VELOCIDAD_REACCION',
            'SALTO_HORIZONTAL',
            'FLEXION_TRONCO',
            'DINAMOMETRIA',
            'ANDERSEN'
        )
    );

-- Step 3: Backfill existing rows from their first resultado_test
-- For each test_fisico that has resultados, take the tipo_test
-- from the first resultado (by lowest id_resultado)
UPDATE test_fisico tf
    JOIN resultado_test rt ON rt.id_test_fisico = tf.id_test_fisico
    SET tf.tipo_test = rt.tipo_test
    WHERE tf.tipo_test IS NULL
    AND rt.id_resultado = (
        SELECT MIN(r2.id_resultado)
        FROM resultado_test r2
        WHERE r2.id_test_fisico = tf.id_test_fisico
    );

-- Step 4: Make column NOT NULL (after backfill)
ALTER TABLE test_fisico
    MODIFY COLUMN tipo_test VARCHAR(30) NOT NULL;
