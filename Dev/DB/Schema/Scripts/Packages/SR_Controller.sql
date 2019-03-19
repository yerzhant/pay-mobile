CREATE OR REPLACE PACKAGE BODY sr_controller_pkg AS
    e_incorrect_format              EXCEPTION;
    e_account_limit_exceeded        EXCEPTION;
    e_fx_rate_not_found             EXCEPTION;

    PROCEDURE check_fields(p_check_fields VARCHAR2, p_amount NUMBER, p_currency VARCHAR2, p_src_name VARCHAR2, p_dst_name VARCHAR2, p_st_type NUMBER) IS
    l_dummy                 VARCHAR2(1);
    BEGIN
        FOR i IN 1..LENGTH(p_check_fields) LOOP
            CASE SUBSTR(p_check_fields, i, 1)
                WHEN 'A' THEN
                    IF p_amount IS NULL OR p_currency IS NULL THEN
                        RAISE e_incorrect_format;
                    END IF;
                WHEN 'S' THEN
                    IF p_src_name IS NULL THEN
                        RAISE e_incorrect_format;
                    END IF;
                WHEN 'D' THEN
                    IF p_dst_name IS NULL THEN
                        RAISE e_incorrect_format;
                    END IF;
                WHEN 'T' THEN
                    IF p_st_type IS NULL THEN
                        RAISE e_incorrect_format;
                    END IF;
                ELSE
                    l_dummy := NULL;
            END CASE;
        END LOOP;
    END check_fields;

    FUNCTION convert_amount(p_amount NUMBER, p_from_currency VARCHAR2, p_to_currency VARCHAR2) RETURN NUMBER IS
    l_amount                        srs.amount%TYPE;
    BEGIN
        IF p_from_currency = p_to_currency THEN
            RETURN p_amount;
        ELSE
            SELECT buy * p_amount INTO l_amount FROM fx_rates WHERE status$ = 'C' AND currency = p_from_currency;
            SELECT l_amount / sell INTO l_amount FROM fx_rates WHERE status$ = 'C' AND currency = p_to_currency;
            RETURN l_amount;
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE e_fx_rate_not_found;
    END convert_amount;

    PROCEDURE check_limits(p_contract_id$ NUMBER, p_kind VARCHAR2, p_name VARCHAR2, p_amount NUMBER, p_currency VARCHAR2, p_date DATE, p_interface OUT NOCOPY VARCHAR2, p_account OUT NOCOPY VARCHAR2) IS
    l_single_amount                 accounts.single_amount%TYPE;
    l_single_currency               accounts.single_currency%TYPE;
    l_day_amount                    accounts.day_amount%TYPE;
    l_day_total_amount              accounts.day_total_amount%TYPE;
    l_day_currency                  accounts.day_currency%TYPE;
    l_day_quantity                  accounts.day_quantity%TYPE;
    l_day_total_quantity            accounts.day_total_quantity%TYPE;
    l_day_date                      accounts.day_date%TYPE;
    l_month_amount                  accounts.month_amount%TYPE;
    l_month_total_amount            accounts.month_total_amount%TYPE;
    l_month_currency                accounts.month_currency%TYPE;
    l_month_quantity                accounts.month_quantity%TYPE;
    l_month_total_quantity          accounts.month_total_quantity%TYPE;
    l_month_date                    accounts.month_date%TYPE;
    l_from_hour                     accounts.from_hour%TYPE;
    l_from_min                      accounts.from_min%TYPE;
    l_to_hour                       accounts.to_hour%TYPE;
    l_to_min                        accounts.to_min%TYPE;
    l_sr_hour                       NUMBER;
    l_sr_min                        NUMBER;
    BEGIN
        SELECT interface, a_number, single_amount, single_currency,
               day_amount, NVL(day_total_amount, 0), day_currency, day_quantity, day_total_quantity, day_date,
               month_amount, NVL(month_total_amount, 0), month_currency, month_quantity, month_total_quantity, month_date,
               NVL(from_hour, 0), NVL(from_min, 0), NVL(to_hour, 23), NVL(to_min, 59)
          INTO p_interface, p_account, l_single_amount, l_single_currency,
               l_day_amount, l_day_total_amount, l_day_currency, l_day_quantity, l_day_total_quantity, l_day_date,
               l_month_amount, l_month_total_amount, l_month_currency, l_month_quantity, l_month_total_quantity, l_month_date,
               l_from_hour, l_from_min, l_to_hour, l_to_min
          FROM accounts WHERE status$ = 'C' AND kind = p_kind AND contract_id$ = p_contract_id$ AND name = p_name FOR UPDATE;
        l_sr_hour := TO_NUMBER(TO_CHAR(p_date, 'HH24'));
        l_sr_min := TO_NUMBER(TO_CHAR(p_date, 'MI'));
        IF l_sr_hour < l_from_hour OR l_sr_hour > l_to_hour THEN
            RAISE e_account_limit_exceeded;
        END IF;
        IF l_sr_hour = l_from_hour AND l_sr_min < l_from_min THEN
            RAISE e_account_limit_exceeded;
        END IF;
        IF l_sr_hour = l_to_hour AND l_sr_min > l_to_min THEN
            RAISE e_account_limit_exceeded;
        END IF;
        IF p_currency IS NOT NULL AND l_single_currency IS NOT NULL AND l_single_amount < convert_amount(p_amount, p_currency, l_single_currency) THEN
            RAISE e_account_limit_exceeded;
        END IF;
        IF l_day_date = TRUNC(p_date) THEN
            IF l_day_quantity <= l_day_total_quantity THEN
                RAISE e_account_limit_exceeded;
            END IF;
            IF p_currency IS NOT NULL AND l_day_currency IS NOT NULL AND l_day_amount < l_day_total_amount + convert_amount(p_amount, p_currency, l_day_currency) THEN
                RAISE e_account_limit_exceeded;
            END IF;
        END IF;
        IF l_month_date = TRUNC(p_date, 'MM') THEN
            IF l_month_quantity <= l_month_total_quantity THEN
                RAISE e_account_limit_exceeded;
            END IF;
            IF p_currency IS NOT NULL AND l_month_currency IS NOT NULL AND l_month_amount < l_month_total_amount + convert_amount(p_amount, p_currency, l_month_currency) THEN
                RAISE e_account_limit_exceeded;
            END IF;
        END IF;
    END check_limits;

    PROCEDURE update_accounts(p_contract_id$ NUMBER, p_kind VARCHAR2, p_name VARCHAR2, p_amount NUMBER, p_currency VARCHAR2, p_date DATE) IS
    l_id$                           accounts.id$%TYPE;
    l_day_amount                    accounts.day_amount%TYPE;
    l_day_total_amount              accounts.day_total_amount%TYPE;
    l_day_currency                  accounts.day_currency%TYPE;
    l_day_quantity                  accounts.day_quantity%TYPE;
    l_day_date                      accounts.day_date%TYPE;
    l_month_amount                  accounts.month_amount%TYPE;
    l_month_total_amount            accounts.month_total_amount%TYPE;
    l_month_currency                accounts.month_currency%TYPE;
    l_month_quantity                accounts.month_quantity%TYPE;
    l_month_date                    accounts.month_date%TYPE;
    BEGIN
        SELECT id$, day_amount, day_total_amount, day_currency, day_quantity, day_date, month_amount, month_total_amount, month_currency, month_quantity, month_date
          INTO l_id$, l_day_amount, l_day_total_amount, l_day_currency, l_day_quantity, l_day_date, l_month_amount, l_month_total_amount, l_month_currency, l_month_quantity, l_month_date
          FROM accounts WHERE status$ = 'C' AND kind = p_kind AND contract_id$ = p_contract_id$ AND name = p_name;
        IF l_day_amount IS NOT NULL OR l_day_quantity IS NOT NULL THEN
            IF l_day_date = TRUNC(p_date) THEN
                IF l_day_amount IS NOT NULL THEN
                    l_day_total_amount := l_day_total_amount + convert_amount(p_amount, p_currency, l_day_currency);
                    UPDATE accounts SET day_total_amount = l_day_total_amount WHERE id$ = l_id$;
                END IF;
                IF l_day_quantity IS NOT NULL THEN
                    UPDATE accounts SET day_total_quantity = day_total_quantity + 1 WHERE id$ = l_id$;
                END IF;
            ELSE
                l_day_total_amount := convert_amount(p_amount, p_currency, l_day_currency);
                l_day_date := TRUNC(p_date);
                UPDATE accounts SET day_total_amount = l_day_total_amount, day_total_quantity = 1, day_date = l_day_date WHERE id$ = l_id$;
            END IF;
        ELSE
            IF l_day_date IS NOT NULL THEN
                UPDATE accounts SET day_total_amount = NULL, day_total_quantity = NULL, day_date = NULL WHERE id$ = l_id$;
            END IF;
        END IF;
        IF l_month_amount IS NOT NULL OR l_month_quantity IS NOT NULL THEN
            IF l_month_date = TRUNC(p_date, 'MM') THEN
                IF l_month_amount IS NOT NULL THEN
                    l_month_total_amount := l_month_total_amount + convert_amount(p_amount, p_currency, l_month_currency);
                    UPDATE accounts SET month_total_amount = l_month_total_amount WHERE id$ = l_id$;
                END IF;
                IF l_month_quantity IS NOT NULL THEN
                    UPDATE accounts SET month_total_quantity = month_total_quantity + 1 WHERE id$ = l_id$;
                END IF;
            ELSE
                l_month_total_amount := convert_amount(p_amount, p_currency, l_month_currency);
                l_month_date := TRUNC(p_date, 'MM');
                UPDATE accounts SET month_total_amount = l_month_total_amount, month_total_quantity = 1, month_date = l_month_date WHERE id$ = l_id$;
            END IF;
        ELSE
            IF l_month_date IS NOT NULL THEN
                UPDATE accounts SET month_total_amount = NULL, month_total_quantity = NULL, month_date = NULL WHERE id$ = l_id$;
            END IF;
        END IF;
    END update_accounts;

    -- Setting p_result to X means processed, either successfully or not.
    PROCEDURE pre_process_sr(p_id$ NUMBER, p_result OUT NOCOPY VARCHAR2, p_phone_number OUT NOCOPY NUMBER, p_csi OUT NOCOPY NUMBER, p_type_code OUT NOCOPY VARCHAR2, p_rmki OUT NOCOPY NUMBER, p_sai OUT NOCOPY NUMBER,
                             p_sc OUT NOCOPY NUMBER, p_src_name OUT NOCOPY VARCHAR2, p_dst_name OUT NOCOPY VARCHAR2, p_amount OUT NOCOPY NUMBER, p_currency OUT NOCOPY VARCHAR2,
                             p_ptc OUT NOCOPY VARCHAR2, p_description OUT NOCOPY VARCHAR2, p_st_type OUT NOCOPY NUMBER, p_rn OUT NOCOPY BLOB, p_sn OUT NOCOPY BLOB,
                             p_dst_interface OUT NOCOPY VARCHAR2, p_sr_status OUT NOCOPY VARCHAR2, p_check_fields OUT NOCOPY VARCHAR2, p_is_2_phase OUT NOCOPY VARCHAR2,
                             p_applied_at OUT NOCOPY VARCHAR2) IS
    l_step                          PLS_INTEGER;
    l_cipher_suite                  srs.cipher_suite%TYPE;
    l_init_interface                srs.init_interface%TYPE;
    l_contract_id$                  srs.contract_id$%TYPE;
    l_sr_type_id$                   srs.sr_type_id$%TYPE;
    l_status_detail                 srs.status_detail%TYPE;
    l_src_interface                 srs.src_interface%TYPE;
    l_src_account                   srs.src_account%TYPE;
    l_dst_account                   srs.dst_account%TYPE;
    l_data                          srs.data%TYPE;
    l_contract_status               contracts.status%TYPE;
    l_password_tries                contracts.password_tries%TYPE;
    l_psw_tries_limit               contracts.password_tries%TYPE;
    l_contract_sc                   contracts.sc%TYPE;
    l_secure_cipher_suite           NUMBER;
    e_contract_is_blocked           EXCEPTION;
    e_incorrect_sc                  EXCEPTION;
    e_psw_tries_limit_exceeded      EXCEPTION;
    e_degraded_cipher_suite         EXCEPTION;
    BEGIN
        l_step := 1;
        SELECT status, phone_number, csi, cipher_suite, init_interface, type_code, contract_id$, sr_type_id$, rmki, sai, sc, status_detail, src_name, src_interface, src_account,
               dst_name, dst_interface, dst_account, amount * 100, currency, ptc, description, st_type, data, rn, sn
          INTO p_sr_status, p_phone_number, p_csi, l_cipher_suite, l_init_interface, p_type_code, l_contract_id$, l_sr_type_id$, p_rmki, p_sai, p_sc, l_status_detail, p_src_name, l_src_interface,
               l_src_account, p_dst_name, p_dst_interface, l_dst_account, p_amount, p_currency, p_ptc, p_description, p_st_type, l_data, p_rn, p_sn
          FROM srs
         WHERE id$ = p_id$ AND sms_sent IS NULL FOR UPDATE;
        CASE p_sr_status
            WHEN 'A' THEN -- Accepted.
                IF l_cipher_suite IS NULL OR l_init_interface IS NULL OR p_type_code IS NULL OR p_rmki IS NULL OR p_sai IS NULL OR p_sc IS NULL OR p_rn IS NULL OR p_sn IS NULL
                   OR l_contract_id$ IS NOT NULL OR l_sr_type_id$ IS NOT NULL OR l_status_detail IS NOT NULL OR l_data IS NOT NULL
                   OR l_src_interface IS NOT NULL OR l_src_account IS NOT NULL OR p_dst_interface IS NOT NULL OR l_dst_account IS NOT NULL THEN
                        RAISE e_incorrect_format;
                END IF;
                l_step := 2;
                SELECT sr_type_id$ INTO l_sr_type_id$ FROM sr_type_codes WHERE status$ = 'C' AND type = 'I' AND interface = l_init_interface AND code = p_type_code;
                SELECT check_fields, is_2_phase INTO p_check_fields, p_is_2_phase FROM sr_types WHERE status$ = 'C' AND id$ = l_sr_type_id$;
                IF p_check_fields IS NOT NULL THEN
                    check_fields(p_check_fields, p_amount, p_currency, p_src_name, p_dst_name, p_st_type);
                END IF;
                l_step := 3;
                SELECT id$, status, password_tries, sc, TO_CHAR(applied_at, 'SSMIHH24DDMMYY') INTO l_contract_id$, l_contract_status, l_password_tries, l_contract_sc, p_applied_at
                  FROM contracts
                 WHERE status$ = 'C' AND si = p_csi AND phone_number = p_phone_number AND status IN ('A', 'B') FOR UPDATE;
                UPDATE srs SET contract_id$ = l_contract_id$, sr_type_id$ = l_sr_type_id$ WHERE id$ = p_id$;
                IF l_contract_status = 'B' THEN
                    RAISE e_contract_is_blocked;
                END IF;
                IF p_sc <= l_contract_sc THEN
                    RAISE e_incorrect_sc;
                END IF;
                l_step := 4;
                SELECT value INTO l_psw_tries_limit FROM global_params WHERE status$ = 'C' AND param = 'PSW_TRIES_LIMIT';
                IF l_password_tries >= l_psw_tries_limit THEN
                    RAISE e_psw_tries_limit_exceeded;
                END IF;
                SELECT COUNT(*) INTO l_secure_cipher_suite FROM v_secure_cipher_suites WHERE id$ = l_contract_id$ AND code = l_cipher_suite;
                IF l_secure_cipher_suite = 0 THEN
                    RAISE e_degraded_cipher_suite;
                END IF;
                p_result := 'S'; -- Check signature.
            WHEN 'R' THEN -- Source response.
                IF l_status_detail IS NULL THEN
                    IF p_dst_interface IS NOT NULL THEN
                        COMMIT;
                        p_result := 'D'; -- Send request to destination interface.
                    ELSE
                        l_step := 2;
                        SELECT is_2_phase INTO p_is_2_phase FROM sr_types WHERE status$ = 'C' AND id$ = l_sr_type_id$;
                        UPDATE srs SET status = 'P', status_detail = 0 WHERE id$ = p_id$;
                        COMMIT;
                        p_result := 'X';
                    END IF;
                ELSE
                    UPDATE srs SET status = 'E' WHERE id$ = p_id$;
                    COMMIT;
                    p_result := 'X';
                END IF;
            WHEN 'T' THEN -- Destination response.
                IF l_status_detail IS NULL THEN
                    l_step := 2;
                    SELECT is_2_phase INTO p_is_2_phase FROM sr_types WHERE status$ = 'C' AND id$ = l_sr_type_id$;
                    UPDATE srs SET status = 'P', status_detail = 0 WHERE id$ = p_id$;
                    COMMIT;
                    p_result := 'X';
                ELSE
                    COMMIT;
                    p_result := 'C'; -- Cancel the request.
                END IF;
        END CASE;
    EXCEPTION
        WHEN e_incorrect_format THEN
            UPDATE srs SET status = 'E', status_detail = 228, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';

        WHEN e_degraded_cipher_suite THEN
            UPDATE srs SET status = 'E', status_detail = 222, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';

        WHEN e_contract_is_blocked THEN
            UPDATE srs SET status = 'E', status_detail = 223, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';

        WHEN e_incorrect_sc THEN
            UPDATE contracts SET status = 'B' WHERE id$ = l_contract_id$;
            UPDATE srs SET status = 'E', status_detail = 229, rn = NULL, sn = NULL WHERE id$ = p_id$;
            INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-009', 'Системная ошибка. Обратитесь к поставщику!', p_id$, USER);
            COMMIT;
            p_result := 'X';

        WHEN e_psw_tries_limit_exceeded THEN
            UPDATE srs SET status = 'E', status_detail = 226, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';

        WHEN CASE_NOT_FOUND THEN
            UPDATE srs SET status = 'E', status_detail = 229, rn = NULL, sn = NULL WHERE id$ = p_id$;
            INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-008', 'Ошибка обработки: [' || p_sr_status || '].', p_id$, USER);
            COMMIT;
            p_result := 'X';

        WHEN NO_DATA_FOUND THEN
            UPDATE srs SET status = 'E', status_detail = 229, rn = NULL, sn = NULL WHERE id$ = p_id$;
            IF l_step = 1 THEN
                INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-003', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            ELSIF l_step = 2 THEN
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-004', 'Тип зароса не найден.', p_id$, USER);
            ELSIF l_step = 3 THEN
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-005', 'Контракт не найден.', p_id$, USER);
            ELSIF l_step = 4 THEN
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-006', 'Параметр PSW_TRIES_LIMIT не найден.', p_id$, USER);
            ELSE
                COMMIT;
                RAISE;
            END IF;
            COMMIT;
            p_result := 'X';

        WHEN TOO_MANY_ROWS THEN
            UPDATE srs SET status = 'E', status_detail = 229, rn = NULL, sn = NULL WHERE id$ = p_id$;
            IF l_step = 3 THEN
                UPDATE contracts SET status = 'B' WHERE status$ = 'C' AND si = p_csi AND phone_number = p_phone_number AND status IN ('A', 'B');
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-007', 'Ошибка целостности.', p_id$, USER);
            ELSE
                COMMIT;
                RAISE;
            END IF;
            COMMIT;
            p_result := 'X';
    END pre_process_sr;

    -- Setting p_result to X means processed, either successfully or not.
    PROCEDURE post_process_sr(p_id$ NUMBER, p_result IN OUT NOCOPY VARCHAR2, p_src_interface OUT NOCOPY VARCHAR2, p_sr_status OUT NOCOPY VARCHAR2) IS
    l_contract_id$                      srs.contract_id$%TYPE;
    l_src_name                          srs.src_name%TYPE;
    l_dst_name                          srs.dst_name%TYPE;
    l_amount                            srs.amount%TYPE;
    l_currency                          srs.currency%TYPE;
    l_src_account                       srs.src_account%TYPE;
    l_dst_interface                     srs.dst_interface%TYPE;
    l_dst_account                       srs.dst_account%TYPE;
    l_sc                                srs.sc%TYPE;
    l_sr_type_id$                       srs.sr_type_id$%TYPE;
    l_date                              srs.a_date%TYPE;
    BEGIN
        SELECT contract_id$, src_name, dst_name, amount, currency, sc, sr_type_id$, a_date, status
          INTO l_contract_id$, l_src_name, l_dst_name, l_amount, l_currency, l_sc, l_sr_type_id$, l_date, p_sr_status
          FROM srs WHERE id$ = p_id$;
        IF p_result = 'F' THEN
            UPDATE contracts SET password_tries = password_tries + 1 WHERE id$ = l_contract_id$;
            UPDATE srs SET status = 'E', status_detail = 225, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';
        ELSE
            UPDATE contracts SET password_tries = 0, sc = l_sc WHERE id$ = l_contract_id$;
            IF l_src_name IS NOT NULL THEN
                check_limits(l_contract_id$, 'S', l_src_name, l_amount, l_currency, l_date, p_src_interface, l_src_account);
            END IF;
            IF l_dst_name IS NOT NULL THEN
                check_limits(l_contract_id$, 'R', l_dst_name, l_amount, l_currency, l_date, l_dst_interface, l_dst_account);
            END IF;
            IF p_src_interface IS NULL THEN
                SELECT src_interface INTO p_src_interface FROM sr_types WHERE status$ = 'C' AND id$ = l_sr_type_id$;
            END IF;
            UPDATE srs SET src_interface = p_src_interface, src_account = l_src_account, dst_interface = l_dst_interface, dst_account = l_dst_account, rn = NULL, sn = NULL WHERE id$ = p_id$;
            IF l_src_name IS NOT NULL THEN
                update_accounts(l_contract_id$, 'S', l_src_name, l_amount, l_currency, l_date);
            END IF;
            IF l_dst_name IS NOT NULL THEN
                update_accounts(l_contract_id$, 'R', l_dst_name, l_amount, l_currency, l_date);
            END IF;
            COMMIT;
            p_result := 'S'; -- Send request to a source interface;
        END IF;
    EXCEPTION
        WHEN e_account_limit_exceeded THEN
            UPDATE srs SET status = 'E', status_detail = 227, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';

        WHEN e_fx_rate_not_found THEN
            UPDATE srs SET status = 'E', status_detail = 221, rn = NULL, sn = NULL WHERE id$ = p_id$;
            COMMIT;
            p_result := 'X';
    END post_process_sr;

    -- Setting p_result to I means inform SMPPServer.
    -- Setting p_result to N means do not inform SMPPServer.
    -- Setting p_result to C means send cancel request.
    PROCEDURE cleanup_sr(p_id$ NUMBER, p_lag NUMBER, p_result OUT NOCOPY VARCHAR2, p_status OUT NOCOPY VARCHAR2, p_src_interface OUT NOCOPY VARCHAR2) IS
    l_step                          PLS_INTEGER;
    l_sr_type_id$                   srs.sr_type_id$%TYPE;
    l_dst_interface                 srs.dst_interface%TYPE;
    l_status_detail                 srs.status_detail%TYPE;
    l_is_cancellable                sr_types.is_cancellable%TYPE;
    l_cancelled                     active_srs.cancelled%TYPE;
    e_recancelling                  EXCEPTION;
    e_cancel                        EXCEPTION;
    BEGIN
        l_step := 1;
        SELECT status, sr_type_id$, src_interface, dst_interface, status_detail INTO p_status, l_sr_type_id$, p_src_interface, l_dst_interface, l_status_detail
          FROM srs WHERE id$ = p_id$ FOR UPDATE;
        l_step := 2;
        SELECT cancelled INTO l_cancelled FROM active_srs WHERE id$ = p_id$ FOR UPDATE;
        IF l_cancelled = 'Y' THEN
            RAISE e_recancelling;
        END IF;
        l_step := 3;
        SELECT is_cancellable INTO l_is_cancellable FROM sr_types WHERE status$ = 'C' AND id$ = l_sr_type_id$;
        CASE p_status
            WHEN 'A' THEN RAISE e_cancel; -- Accepted.
            WHEN 'N' THEN RAISE e_cancel; -- Source cancel request.
            WHEN 'C' THEN -- Source cancel response.
                UPDATE srs SET status = 'E' WHERE id$ = p_id$;
                p_result := 'N';
            WHEN 'S' THEN -- Source request.
                IF l_status_detail IS NULL THEN
                    RAISE e_cancel;
                ELSE
                    UPDATE srs SET status = 'E' WHERE id$ = p_id$;
                    p_result := 'I';
                END IF;
            WHEN 'R' THEN -- Source response.
                IF l_status_detail IS NULL THEN
                    IF l_dst_interface IS NOT NULL THEN
                        UPDATE srs SET status = 'E', status_detail = 229 WHERE id$ = p_id$;
                    ELSE
                        UPDATE srs SET status = 'P', status_detail = 0 WHERE id$ = p_id$;
                    END IF;
                ELSE
                    UPDATE srs SET status = 'E' WHERE id$ = p_id$;
                END IF;
                p_result := 'I';
            WHEN 'D' THEN -- Destination request.
                IF l_status_detail IS NOT NULL THEN
                    RAISE e_cancel;
                ELSE
                    UPDATE srs SET status = 'E', status_detail = 229 WHERE id$ = p_id$;
                    p_result := 'I';
                END IF;
            WHEN 'T' THEN -- Destination response.
                IF l_status_detail IS NOT NULL THEN
                    RAISE e_cancel;
                ELSE
                    UPDATE srs SET status = 'P', status_detail = 0 WHERE id$ = p_id$;
                    p_result := 'I';
                END IF;
            WHEN 'E' THEN p_result := 'I'; -- Declined.
            WHEN 'P' THEN p_result := 'I'; -- Processed.
        END CASE;
        UPDATE active_srs SET cancelled = 'Y', lag = p_lag WHERE id$ = p_id$;
        COMMIT;
    EXCEPTION
        WHEN e_cancel THEN
            UPDATE srs SET status_detail = 220 WHERE id$ = p_id$ AND status_detail IS NULL;
            UPDATE active_srs SET cancelled = 'Y', lag = p_lag WHERE id$ = p_id$;
            IF l_is_cancellable = 'Y' THEN
                p_result := 'C';
            ELSE
                UPDATE srs SET status = 'E' WHERE id$ = p_id$;
                p_result := 'I';
            END IF;
            COMMIT;

        WHEN e_recancelling THEN
            DELETE FROM active_srs WHERE id$ = p_id$;
            INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-010', 'Запрос уже отменен.', p_id$, USER);
            COMMIT;
            p_result := 'N';

        WHEN CASE_NOT_FOUND THEN
            DELETE FROM active_srs WHERE id$ = p_id$;
            INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-014', 'Ошибка обработки: [' || p_status || '].', p_id$, USER);
            COMMIT;
            p_result := 'N';

        WHEN NO_DATA_FOUND THEN
            DELETE FROM active_srs WHERE id$ = p_id$;
            IF l_step = 1 THEN
                INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-011', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            ELSIF l_step = 2 THEN
                INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-012', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            ELSIF l_step = 3 THEN
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-013', 'Тип зароса не найден.', p_id$, USER);
            ELSE
                COMMIT;
                RAISE;
            END IF;
            COMMIT;
            p_result := 'N';
    END cleanup_sr;
END sr_controller_pkg;
/
SHOW ERRORS
