CREATE OR REPLACE PACKAGE BODY smpp_server_pkg AS
    PROCEDURE process_sr_notification(p_id$ NUMBER, p_phone_number OUT NOCOPY VARCHAR2, p_message OUT NOCOPY VARCHAR2) IS
    l_step                          PLS_INTEGER;
    l_send_sms                      status_details.send_sms%TYPE;
    l_status_detail                 status_details.code%TYPE;
    l_sms_text                      status_details.sms_text%TYPE;
    BEGIN
        l_step := 1;
        SELECT phone_number, status_detail INTO p_phone_number, l_status_detail FROM srs
         WHERE id$ = p_id$ AND sms_sent IS NULL AND status_detail IS NOT NULL
           FOR UPDATE;
        DELETE FROM active_srs WHERE id$ = p_id$;
        l_step := 2;
        SELECT send_sms, name, sms_text INTO l_send_sms, p_message, l_sms_text FROM status_details WHERE code = l_status_detail AND status$ = 'C';
        IF l_send_sms = 'N' THEN
            UPDATE srs SET sms_sent = 'N' WHERE id$ = p_id$;
            p_message := NULL;
            RETURN;
        ELSIF l_sms_text IS NOT NULL THEN
            p_message := l_sms_text;
        END IF;
        UPDATE srs SET sms_sent = 'Y' WHERE id$ = p_id$;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            IF l_step = 1 THEN
                INSERT INTO system_log(id$, code, description, user$) VALUES (system_log_seq.NEXTVAL, 'D-001', 'Запрос на обработку не найден: [' || TO_CHAR(p_id$) || '].', USER);
            ELSIF l_step = 2 THEN
                INSERT INTO system_log(id$, code, description, sr_id$, user$) VALUES (system_log_seq.NEXTVAL, 'D-002', 'Описание статуса не найдено.', p_id$, USER);
            ELSE
                p_message := NULL;
                RAISE;
            END IF;
            p_message := NULL;
    END process_sr_notification;
END smpp_server_pkg;
/
SHOW ERRORS
