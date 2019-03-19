/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.14 Yerzhan Tulepov         Created.
 */

SPOOL Logs\9_Grants.log

-- smpp_server rights.
GRANT SELECT, UPDATE(status) ON srs TO smpp_server;
GRANT SELECT, INSERT(id$) ON active_srs TO smpp_server;
GRANT SELECT, INSERT(id$, sr_id$, interface, direction, data), UPDATE(data) ON messages TO smpp_server;
GRANT INSERT(id$, code, description, user$) ON system_log TO smpp_server;
GRANT SELECT ON messages_seq TO smpp_server;
GRANT SELECT ON system_log_seq TO smpp_server;
GRANT EXECUTE ON smpp_server_pkg TO smpp_server;

-- front_server rights.
GRANT SELECT, UPDATE(status, cipher_suite) ON applications TO front_server;
GRANT INSERT(id$, code, description, user$) ON system_log TO front_server;
GRANT SELECT ON system_log_seq TO front_server;
GRANT SELECT, INSERT(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, src_name, dst_name, amount, currency, ptc, description, st_type, rn, sn), UPDATE(rn, sn) ON srs TO front_server;
GRANT SELECT ON srs_seq TO front_server;

-- sr_controller rights.
GRANT SELECT ON v_timed_out_srs TO sr_controller;
GRANT EXECUTE ON sr_controller_pkg TO sr_controller;

-- interface rights.
GRANT SELECT ON contracts TO interface;
GRANT SELECT ON accounts TO interface;
GRANT UPDATE(data) ON srs TO interface;
GRANT UPDATE(data) ON messages TO interface;
GRANT EXECUTE ON interface_pkg TO interface;

-- app_entry rights.
GRANT SELECT ON branches TO app_entry;
GRANT SELECT ON doc_types TO app_entry;
GRANT SELECT ON countries TO app_entry;
GRANT SELECT ON currencies TO app_entry;
GRANT SELECT ON interfaces TO app_entry;
GRANT SELECT, INSERT(id$, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, last_name, first_name, birth_date, doc_date, doc_issuer, tax_payer_id, address, middle_name) ON applications TO app_entry;
GRANT SELECT, INSERT(id$, application_id$, kind, interface, a_number, name, priority, single_amount, single_currency, day_amount, day_currency, day_quantity, month_amount, month_currency, month_quantity, from_hour, from_min, to_hour, to_min) ON app_accounts TO app_entry;

-- sec_officer rights.
GRANT SELECT, INSERT(id$, status$, date$, user$, name, parent_id$, hu$, cid$), UPDATE(status$, date$, user$, name, parent_id$, hu$) ON branches TO sec_officer;
GRANT SELECT, INSERT(id$, status$, date$, user$, sys_name, full_name, hu$, cid$), UPDATE(status$, date$, user$, sys_name, full_name, hu$) ON users TO sec_officer;
GRANT SELECT, INSERT(id$, status$, date$, user$, user_id$, branch_id$, hu$, cid$), UPDATE(status$, date$, user$, user_id$, branch_id$, hu$) ON users_branches TO sec_officer;
GRANT SELECT ON branches_seq TO sec_officer;
GRANT SELECT ON users_seq TO sec_officer;
GRANT SELECT ON users_branches_seq TO sec_officer;

-- setup rights.
GRANT SELECT, INSERT(id$, status$, date$, user$, name, hu$, cid$), UPDATE(status$, date$, user$, name, hu$) ON doc_types TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, name, hu$, cid$), UPDATE(status$, date$, user$, name, hu$) ON countries TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, code, name, hu$, cid$), UPDATE(status$, date$, user$, code, name, hu$) ON currencies TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, code, name, send_sms, sms_text, hu$, cid$), UPDATE(status$, date$, user$, name, send_sms, sms_text, hu$) ON status_details TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, param, value, hu$, cid$), UPDATE(status$, date$, user$, param, value, hu$) ON global_params TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, format, code, name, hu$, cid$), UPDATE(status$, date$, user$, format, code, name, hu$) ON interfaces TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, name, is_2_phase, is_cancellable, check_fields, src_interface, hu$, cid$), UPDATE(status$, date$, user$, name, is_2_phase, is_cancellable, check_fields, src_interface, hu$) ON sr_types TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, sr_type_id$, interface, type, code, hu$, cid$), UPDATE(status$, date$, user$, interface, type, code, hu$) ON sr_type_codes TO setup;
GRANT SELECT, INSERT(id$, status$, date$, user$, code, a_level, hu$, cid$), UPDATE(status$, date$, user$, code, a_level, hu$) ON cipher_suites TO setup;
GRANT SELECT ON users TO setup;
GRANT SELECT ON doc_types_seq TO setup;
GRANT SELECT ON countries_seq TO setup;
GRANT SELECT ON currencies_seq TO setup;
GRANT SELECT ON status_details_seq TO setup;
GRANT SELECT ON global_params_seq TO setup;
GRANT SELECT ON interfaces_seq TO setup;
GRANT SELECT ON sr_types_seq TO setup;
GRANT SELECT ON sr_type_codes_seq TO setup;
GRANT SELECT ON cipher_suites_seq TO setup;

-- pm_client rights.
GRANT SELECT ON branches TO pm_client;
GRANT SELECT ON doc_types TO pm_client;
GRANT SELECT ON countries TO pm_client;
GRANT SELECT ON currencies TO pm_client;
GRANT SELECT ON users TO pm_client;
GRANT SELECT ON users_branches TO pm_client;
GRANT SELECT ON status_details TO pm_client;
GRANT SELECT ON interfaces TO pm_client;
GRANT SELECT ON sr_types TO pm_client;
GRANT SELECT, INSERT(id$, status$, date$, user$, currency, buy, sell, hu$, cid$), UPDATE(date$, status$, user$, currency, buy, sell, hu$) ON fx_rates TO pm_client;
GRANT SELECT, UPDATE(status, country_id$, doc_type_id$, doc_id, branch_id$, manager, manager_date, last_name, first_name, birth_date, doc_date, doc_issuer, address, middle_name, tax_payer_id, decline_reason) ON applications TO pm_client;
GRANT SELECT,
      INSERT(id$, application_id$, kind, interface, a_number, name, priority, single_amount, single_currency, day_amount, day_currency, day_quantity, month_amount, month_currency, month_quantity, from_hour, from_min, to_hour, to_min),
      UPDATE(                      kind, interface, a_number, name, priority, single_amount, single_currency, day_amount, day_currency, day_quantity, month_amount, month_currency, month_quantity, from_hour, from_min, to_hour, to_min) ON app_accounts TO pm_client;
GRANT SELECT, INSERT(id$, status$, date$, user$, country_id$, doc_type_id$, doc_id, from_date, branch_id$, last_name, first_name, birth_date, doc_date, doc_issuer, address, middle_name, tax_payer_id, hu$, cid$),
              UPDATE(     status$, date$, user$, country_id$, doc_type_id$, doc_id,            branch_id$, last_name, first_name, birth_date, doc_date, doc_issuer, address, middle_name, tax_payer_id, hu$) ON clients TO pm_client;
GRANT SELECT, INSERT(id$, status$, date$, user$, client_id$, si, status, open_date, applied_at, phone_number, sc, password_tries, cipher_suite, close_date, a_number, hu$, cid$),
              UPDATE(     status$, date$, user$,                 status,                                          password_tries,               close_date, a_number, hu$) ON contracts TO pm_client;
GRANT SELECT,
      INSERT(id$, status$, date$, user$, contract_id$, kind, interface, a_number, name, priority, single_amount, single_currency, day_amount, day_total_amount, day_currency, day_quantity, day_total_quantity, day_date, month_amount, month_total_amount, month_currency, month_quantity, month_total_quantity, month_date, from_hour, from_min, to_hour, to_min, hu$, cid$),
      UPDATE(     status$, date$, user$,               kind, interface, a_number, name, priority, single_amount, single_currency, day_amount, day_total_amount, day_currency, day_quantity,                               month_amount, month_total_amount, month_currency, month_quantity,                                   from_hour, from_min, to_hour, to_min, hu$) ON accounts TO pm_client;
GRANT SELECT ON srs TO pm_client;
GRANT SELECT ON active_srs TO pm_client;
GRANT SELECT ON messages TO pm_client;
GRANT SELECT ON system_log TO pm_client;
GRANT SELECT ON fx_rates_seq TO pm_client;
GRANT SELECT ON clients_seq TO pm_client;
GRANT SELECT ON contracts_seq TO pm_client;
GRANT SELECT ON accounts_seq TO pm_client;
GRANT EXECUTE ON pm_client_pkg TO pm_client;

SPOOL OFF
