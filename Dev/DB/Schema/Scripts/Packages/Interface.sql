CREATE OR REPLACE PACKAGE BODY interface_pkg AS
    PROCEDURE process_request(p_id$ NUMBER, p_interface VARCHAR2, p_status VARCHAR2, p_cancel VARCHAR2, p_message VARCHAR2, p_result OUT NOCOPY VARCHAR2, p_contract_id$ OUT NOCOPY NUMBER,
                            p_code OUT NOCOPY VARCHAR2, p_data OUT NOCOPY BLOB, p_date OUT NOCOPY VARCHAR2, p_account OUT NOCOPY VARCHAR2, p_amount OUT NOCOPY NUMBER,
                            p_currency OUT NOCOPY VARCHAR2, p_ptc OUT NOCOPY VARCHAR2, p_description OUT NOCOPY VARCHAR2, p_st_type OUT NOCOPY NUMBER) IS
    l_step                          PLS_INTEGER;
    l_status                        srs.status%TYPE;
    l_new_status                    srs.status%TYPE;
    l_src_account                   srs.src_account%TYPE;
    l_dst_account                   srs.dst_account%TYPE;
    l_sr_type_id$                   srs.sr_type_id$%TYPE;
    l_type                          sr_type_codes.type%TYPE;
    e_system_error                  EXCEPTION;
    BEGIN
        l_step := 1;
        SELECT status, sr_type_id$, contract_id$, TO_CHAR(a_date, 'YYYYMMDDHH24MISS'), src_account, dst_account, amount * 100, currency, ptc, description, NVL(st_type, -1)
          INTO l_status, l_sr_type_id$, p_contract_id$, p_date, l_src_account, l_dst_account, p_amount, p_currency, p_ptc, p_description, p_st_type
          FROM srs WHERE id$ = p_id$ AND contract_id$ IS NOT NULL FOR UPDATE;
        IF l_status = p_status THEN
            IF p_cancel = 'Y' THEN
                IF l_status NOT IN ('A', 'D', 'T', 'S') THEN
                    RAISE e_system_error;
                ELSE
                    l_type := 'C';
                    l_new_status := 'N';
                    p_account := l_src_account;
                END IF;
            ELSE
                CASE l_status
                    WHEN 'A' THEN
                        l_type := 'S';
                        l_new_status := 'S';
                        p_account := l_src_account;
                    WHEN 'R' THEN
                        l_type := 'D';
                        l_new_status := 'D';
                        p_account := l_dst_account;
                    ELSE
                        RAISE e_system_error;
                END CASE;
            END IF;
            l_step := 2;
            SELECT code INTO p_code FROM sr_type_codes WHERE sr_type_id$ = l_sr_type_id$ AND interface = p_interface AND type = l_type AND status$ = 'C';
            UPDATE srs SET status = l_new_status WHERE id$ = p_id$;
            IF p_message = 'Y' THEN
                INSERT INTO messages(id$, sr_id$, interface, direction, data) VALUES (messages_seq.NEXTVAL, p_id$, p_interface, 'S', EMPTY_BLOB()) RETURNING data INTO p_data;
            END IF;
            p_result := 'S'; -- Send a message.
        ELSE
            p_result := 'R'; -- Reprocess.
        END IF;
    EXCEPTION
        WHEN e_system_error THEN
            UPDATE srs SET status_detail = 229 WHERE id$ = p_id$ AND status_detail IS NULL;
            p_result := 'C'; -- Cleanup the SR.

        WHEN NO_DATA_FOUND THEN
            IF l_step = 1 THEN
                INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-015', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            ELSIF l_step = 2 THEN
                UPDATE srs SET status_detail = 229 WHERE id$ = p_id$ AND status_detail IS NULL;
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-016', 'Код типа запроса не найден.', p_id$, USER);
            ELSE
                RAISE;
            END IF;
            p_result := 'C'; -- Cleanup the SR.
    END process_request;

    PROCEDURE process_response(p_id$ NUMBER, p_interface VARCHAR2, p_type VARCHAR2, p_status_detail NUMBER, p_account VARCHAR2, p_date VARCHAR2, p_amount NUMBER, p_currency VARCHAR2,
                            p_data_type VARCHAR2, p_result OUT NOCOPY VARCHAR2, p_message_data OUT NOCOPY BLOB, p_sr_data OUT NOCOPY BLOB) IS
    l_status                        srs.status%TYPE;
    l_new_status                    srs.status%TYPE;
    l_date                          srs.a_date%TYPE;
    l_amount                        srs.amount%TYPE;
    l_currency                      srs.currency%TYPE;
    l_src_interface                 srs.src_interface%TYPE;
    l_src_account                   srs.src_account%TYPE;
    l_dst_interface                 srs.dst_interface%TYPE;
    l_dst_account                   srs.dst_account%TYPE;
    l_dummy                         VARCHAR2(1);
    e_matching_error                EXCEPTION;
    e_system_error                  EXCEPTION;
    BEGIN
        SELECT status, a_date, amount, currency, src_interface, src_account, dst_interface, dst_account
          INTO l_status, l_date, l_amount, l_currency, l_src_interface, l_src_account, l_dst_interface, l_dst_account
          FROM srs
         WHERE id$ = p_id$ FOR UPDATE;
        CASE l_status
            WHEN 'S' THEN
                IF p_interface = l_src_interface THEN
                    IF p_type = 'R' THEN -- Request respose.
                        IF p_account IS NOT NULL THEN
                            IF l_src_account IS NULL OR p_account <> l_src_account THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_date IS NOT NULL THEN
                            IF l_date IS NULL OR TO_DATE(p_date, 'YYYYMMDDHH24MISS') <> l_date THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_amount IS NOT NULL THEN
                            IF l_amount IS NULL OR p_amount / 100 <> l_amount THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_currency IS NOT NULL THEN
                            IF l_currency IS NULL OR p_currency <> l_currency THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                    ELSE
                        l_dummy := '?';
                        RAISE e_system_error;
                    END IF;
                ELSE
                    l_dummy := '?';
                    RAISE e_system_error;
                END IF;
                l_new_status := 'R';
            WHEN 'D' THEN
                IF p_interface = l_dst_interface THEN
                    IF p_type = 'R' THEN -- Request respose.
                        IF p_account IS NOT NULL THEN
                            IF l_dst_account IS NULL OR p_account <> l_dst_account THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_date IS NOT NULL THEN
                            IF l_date IS NULL OR TO_DATE(p_date, 'YYYYMMDDHH24MISS') <> l_date THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_amount IS NOT NULL THEN
                            IF l_amount IS NULL OR p_amount / 100 <> l_amount THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_currency IS NOT NULL THEN
                            IF l_currency IS NULL OR p_currency <> l_currency THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                    ELSE
                        l_dummy := '?';
                        RAISE e_system_error;
                    END IF;
                ELSE
                    l_dummy := '?';
                    RAISE e_system_error;
                END IF;
                l_new_status := 'T';
            WHEN 'N' THEN
                IF p_interface = l_src_interface THEN
                    IF p_type = 'C' THEN -- Cancellation respose.
                        IF p_account IS NOT NULL THEN
                            IF l_src_account IS NULL OR p_account <> l_src_account THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_date IS NOT NULL THEN
                            IF l_date IS NULL OR TO_DATE(p_date, 'YYYYMMDDHH24MISS') <> l_date THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_amount IS NOT NULL THEN
                            IF l_amount IS NULL OR p_amount / 100 <> l_amount THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                        IF p_currency IS NOT NULL THEN
                            IF l_currency IS NULL OR p_currency <> l_currency THEN
                                RAISE e_matching_error;
                            END IF;
                        END IF;
                    ELSE
                        l_dummy := '?';
                        RAISE e_system_error;
                    END IF;
                ELSE
                    l_dummy := '?';
                    RAISE e_system_error;
                END IF;
                l_new_status := 'C';
        END CASE;
        UPDATE srs SET status = l_new_status WHERE id$ = p_id$;
        IF p_data_type = 'S' THEN -- Service request.
            UPDATE srs SET data = EMPTY_BLOB() WHERE id$ = p_id$ RETURNING data INTO p_sr_data;
        ELSIF p_data_type = 'M' THEN -- Message.
            INSERT INTO messages(id$, sr_id$, interface, direction, data) VALUES (messages_seq.NEXTVAL, p_id$, p_interface, 'R', EMPTY_BLOB()) RETURNING data INTO p_message_data;
        ELSIF p_data_type = 'B' THEN -- Both.
            UPDATE srs SET data = EMPTY_BLOB() WHERE id$ = p_id$ RETURNING data INTO p_sr_data;
            INSERT INTO messages(id$, sr_id$, interface, direction, data) VALUES (messages_seq.NEXTVAL, p_id$, p_interface, 'R', EMPTY_BLOB()) RETURNING data INTO p_message_data;
        END IF;
        IF p_status_detail <> 0 THEN
            UPDATE srs SET status_detail = p_status_detail WHERE id$ = p_id$ AND status_detail IS NULL;
        END IF;
        p_result := 'D'; -- Don't cleanup;
    EXCEPTION
        WHEN e_matching_error THEN
            UPDATE srs SET status_detail = 224 WHERE id$ = p_id$ AND status_detail IS NULL;
            p_result := 'C'; -- Cleanup the SR.

        WHEN e_system_error THEN
            UPDATE srs SET status_detail = 229 WHERE id$ = p_id$ AND status_detail IS NULL;
            p_result := 'C'; -- Cleanup the SR.

        WHEN NO_DATA_FOUND THEN
            INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-017', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            p_result := 'C'; -- Cleanup the SR.
        
        WHEN CASE_NOT_FOUND THEN
            UPDATE srs SET status_detail = 229 WHERE id$ = p_id$ AND status_detail IS NULL;
            INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-018', 'Ошибка обработки: [' || l_status || '].', p_id$, USER);
            p_result := 'C'; -- Cleanup the SR.
    END process_response;
END interface_pkg;
/
SHOW ERRORS
