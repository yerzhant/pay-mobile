/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Packages' specs.
 *
 * 2010.04.08 Yerzhan Tulepov         Created.
 */


SPOOL Logs\8_Packages.log

ALTER SESSION SET PLSQL_WARNINGS='ENABLE:ALL';

CREATE OR REPLACE PACKAGE smpp_server_pkg AS
    PROCEDURE process_sr_notification(p_id$ NUMBER, p_phone_number OUT NOCOPY VARCHAR2, p_message OUT NOCOPY VARCHAR2);
END smpp_server_pkg;
/
SHOW ERRORS

@Scripts\Packages\SMPP_Server

CREATE OR REPLACE PACKAGE sr_controller_pkg AS
    PROCEDURE pre_process_sr(p_id$ NUMBER, p_result OUT NOCOPY VARCHAR2, p_phone_number OUT NOCOPY NUMBER, p_csi OUT NOCOPY NUMBER, p_type_code OUT NOCOPY VARCHAR2, p_rmki OUT NOCOPY NUMBER, p_sai OUT NOCOPY NUMBER,
                             p_sc OUT NOCOPY NUMBER, p_src_name OUT NOCOPY VARCHAR2, p_dst_name OUT NOCOPY VARCHAR2, p_amount OUT NOCOPY NUMBER, p_currency OUT NOCOPY VARCHAR2,
                             p_ptc OUT NOCOPY VARCHAR2, p_description OUT NOCOPY VARCHAR2, p_st_type OUT NOCOPY NUMBER, p_rn OUT NOCOPY BLOB, p_sn OUT NOCOPY BLOB,
                             p_dst_interface OUT NOCOPY VARCHAR2, p_sr_status OUT NOCOPY VARCHAR2, p_check_fields OUT NOCOPY VARCHAR2, p_is_2_phase OUT NOCOPY VARCHAR2,
                             p_applied_at OUT NOCOPY VARCHAR2);
    PROCEDURE post_process_sr(p_id$ NUMBER, p_result IN OUT NOCOPY VARCHAR2, p_src_interface OUT NOCOPY VARCHAR2, p_sr_status OUT NOCOPY VARCHAR2);
    PROCEDURE cleanup_sr(p_id$ NUMBER, p_lag NUMBER, p_result OUT NOCOPY VARCHAR2, p_status OUT NOCOPY VARCHAR2, p_src_interface OUT NOCOPY VARCHAR2);
END sr_controller_pkg;
/
SHOW ERRORS

@Scripts\Packages\SR_Controller

CREATE OR REPLACE PACKAGE interface_pkg AS
    PROCEDURE process_request(p_id$ NUMBER, p_interface VARCHAR2, p_status VARCHAR2, p_cancel VARCHAR2, p_message VARCHAR2, p_result OUT NOCOPY VARCHAR2, p_contract_id$ OUT NOCOPY NUMBER,
                            p_code OUT NOCOPY VARCHAR2, p_data OUT NOCOPY BLOB, p_date OUT NOCOPY VARCHAR2, p_account OUT NOCOPY VARCHAR2, p_amount OUT NOCOPY NUMBER,
                            p_currency OUT NOCOPY VARCHAR2, p_ptc OUT NOCOPY VARCHAR2, p_description OUT NOCOPY VARCHAR2, p_st_type OUT NOCOPY NUMBER);
    PROCEDURE process_response(p_id$ NUMBER, p_interface VARCHAR2, p_type VARCHAR2, p_status_detail NUMBER, p_account VARCHAR2, p_date VARCHAR2, p_amount NUMBER, p_currency VARCHAR2,
                            p_data_type VARCHAR2, p_result OUT NOCOPY VARCHAR2, p_message_data OUT NOCOPY BLOB, p_sr_data OUT NOCOPY BLOB);
END interface_pkg;
/
SHOW ERRORS

@Scripts\Packages\Interface

CREATE OR REPLACE PACKAGE pm_client_pkg AS
    PROCEDURE create_notification(p_phone_number NUMBER, p_csi NUMBER, p_contract_id$ NUMBER);
END pm_client_pkg;
/
SHOW ERRORS

@Scripts\Packages\PM_Client

SPOOL OFF

--EXIT
