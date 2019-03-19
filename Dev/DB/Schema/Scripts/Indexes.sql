/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Creation of indexes.
 *
 * 2010.04.08 Yerzhan Tulepov         Created.
 */

SPOOL Logs\6_Indexes.log

CREATE INDEX ix_branches_cid$ ON branches(cid$) TABLESPACE dic_i;
CREATE INDEX ix_doc_types_cid$ ON doc_types(cid$) TABLESPACE dic_i;
CREATE INDEX ix_countries_cid$ ON countries(cid$) TABLESPACE dic_i;
CREATE INDEX ix_currencies_cid$ ON currencies(cid$) TABLESPACE dic_i;
CREATE INDEX ix_users_cid$ ON users(cid$) TABLESPACE dic_i;
CREATE INDEX ix_status_details_cid$ ON status_details(cid$) TABLESPACE dic_i;
CREATE INDEX ix_global_params_cid$ ON global_params(cid$) TABLESPACE dic_i;
CREATE INDEX ix_interfaces_cid$ ON interfaces(cid$) TABLESPACE dic_i;
CREATE INDEX ix_sr_types_cid$ ON sr_types(cid$) TABLESPACE dic_i;
CREATE INDEX ix_sr_type_codes_cid$ ON sr_type_codes(cid$) TABLESPACE dic_i;
CREATE INDEX ix_fx_rates_cid$ ON fx_rates(cid$) TABLESPACE misc_i;
CREATE INDEX ix_cipher_suites_cid$ ON cipher_suites(cid$) TABLESPACE dic_i;
CREATE INDEX ix_clients_cid$ ON clients(cid$) TABLESPACE contr_i;
CREATE INDEX ix_contracts_cid$ ON contracts(cid$) TABLESPACE contr_i;
CREATE INDEX ix_accounts_cid$ ON accounts(cid$) TABLESPACE contr_i;

CREATE INDEX ix_branches_parent_id$ ON branches(parent_id$) TABLESPACE dic_i;

CREATE INDEX ix_users_branches_user_id$ ON users_branches(user_id$) TABLESPACE dic_i;
CREATE INDEX ix_users_branches_branch_id$ ON users_branches(branch_id$) TABLESPACE dic_i;

CREATE INDEX ix_status_details_code ON status_details(code) TABLESPACE dic_i;

CREATE INDEX ix_cipher_suites_code ON cipher_suites(code) TABLESPACE dic_i;
CREATE INDEX ix_cipher_suites_a_level ON cipher_suites(a_level) TABLESPACE dic_i;

CREATE INDEX ix_system_log_occurred_at ON system_log(occurred_at) TABLESPACE misc_i;
CREATE INDEX ix_system_log_sr_id$ ON system_log(sr_id$) TABLESPACE misc_i;
--CREATE INDEX ix_system_log_contract_id$ ON system_log(contract_id$) TABLESPACE misc_i;
CREATE INDEX ix_system_log_user$ ON system_log(user$) TABLESPACE misc_i;

CREATE INDEX ix_applications_applied_at ON applications(applied_at) TABLESPACE app_i;
CREATE INDEX ix_applications_phone_number ON applications(phone_number) TABLESPACE app_i;
CREATE INDEX ix_applications_last_first ON applications(last_name, first_name) TABLESPACE app_i;

CREATE INDEX ix_clients_last_first_names ON clients(last_name, first_name) TABLESPACE contr_i;
CREATE INDEX ix_clients_from_date ON clients(from_date) TABLESPACE contr_i;
CREATE INDEX ix_clients_branch_id$ ON clients(branch_id$) TABLESPACE contr_i;

CREATE INDEX ix_contracts_client_id$ ON contracts(client_id$) TABLESPACE contr_i;
CREATE INDEX ix_contracts_phone_number ON contracts(phone_number) TABLESPACE contr_i;
CREATE INDEX ix_contracts_open_close_date ON contracts(open_date, close_date) TABLESPACE contr_i;
CREATE INDEX ix_contracts_a_number ON contracts(a_number) TABLESPACE contr_i;

CREATE INDEX ix_srs_contract_id$ ON srs(contract_id$) TABLESPACE sr_i;
CREATE INDEX ix_srs_a_date ON srs(a_date) TABLESPACE sr_i;

CREATE INDEX ix_messages_sr_id$ ON messages(sr_id$) TABLESPACE msg_i;
CREATE INDEX ix_messages_a_date ON messages(a_date) TABLESPACE msg_i;

SPOOL OFF
