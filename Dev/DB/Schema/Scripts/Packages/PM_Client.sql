CREATE OR REPLACE PACKAGE BODY pm_client_pkg AS
    PROCEDURE create_notification(p_phone_number NUMBER, p_csi NUMBER, p_contract_id$ NUMBER) IS
    BEGIN
        INSERT INTO srs(id$, status, phone_number, csi, contract_id$, status_detail) VALUES (srs_seq.NEXTVAL, 'P', p_phone_number, p_csi, p_contract_id$, 1);
        INSERT INTO active_srs(id$, send_ntf) VALUES (srs_seq.CURRVAL, 'Y');
    END create_notification;
END pm_client_pkg;
/
SHOW ERRORS
