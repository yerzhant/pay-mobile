/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Views.
 *
 * 2010.04.20 Yerzhan Tulepov         Created.
 */

SPOOL Logs\7_Views.log

CREATE OR REPLACE VIEW v_secure_cipher_suites AS
    SELECT c.id$, s2.code
      FROM contracts c, cipher_suites s1, cipher_suites s2
     WHERE c.cipher_suite = s1.code AND c.status$ = 'C' AND c.status = 'A'
       AND s1.status$ = 'C' AND s2.status$ = 'C' AND s1.a_level <= s2.a_level;

CREATE OR REPLACE VIEW v_timed_out_srs AS
    SELECT s.id$, send_ntf, (SYSDATE - a_date) * 86400 - lag delta
      FROM srs s, active_srs a
     WHERE s.id$ = a.id$;

SPOOL OFF
