insert into branches(user$, name) values (user, 'Филиал 1');
insert into branches(user$, name) values (user, 'Филиал 2');
insert into doc_types(user$, name) values (user, 'Уд. личности');
insert into doc_types(user$, name) values (user, 'Паспорт');
insert into countries(user$, name) values (user, 'Казахстан');
insert into countries(user$, name) values (user, 'Бельгия');
insert into users(user$, sys_name, full_name) values (user, 'TestUser', 'Тестовый сотр.');
insert into users_branches(user$, user_id$, branch_id$) values (user, 2, 1);
insert into users_branches(user$, user_id$, branch_id$) values (user, 2, 2);
insert into interfaces(user$, format, code, name) values (user, 'X', 'XML', 'Банковский');
insert into interfaces(user$, format, code, name) values (user, 'I', 'ISO', 'Карточный');
insert into sr_types(user$, name, check_fields, is_2_phase, is_cancellable, src_interface) values (user, 'Обновление счетов', null, 'Y', 'N', 'SYS');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 1, 'FRONT', 'I', '6');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 1, 'SYS', 'S', 'GAL');
insert into sr_types(user$, name, check_fields, is_2_phase, is_cancellable, src_interface) values (user, 'Перевод', 'APESD', 'N', 'Y', null);
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 2, 'FRONT', 'I', '3');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 2, 'ISO', 'S', 'Debit');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 2, 'XML', 'S', 'Debit');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 2, 'ISO', 'D', 'Credit');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 2, 'XML', 'D', 'Credit');
insert into sr_types(user$, name, check_fields, is_2_phase, is_cancellable, src_interface) values (user, 'Выписка', 'ST', 'Y', 'N', null);
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 3, 'FRONT', 'I', '4');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 3, 'XML', 'S', 'Statement');
insert into sr_types(user$, name, check_fields, is_2_phase, is_cancellable, src_interface) values (user, 'Баланс', 'S', 'Y', 'N', null);
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 4, 'FRONT', 'I', '5');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 4, 'ISO', 'S', 'Balanse');
insert into sr_type_codes(user$, sr_type_id$, interface, type, code) values (user, 4, 'XML', 'S', 'Balanse');
insert into fx_rates(user$, currency, buy, sell) values (user, 'KZT', 1, 1);
insert into fx_rates(user$, currency, buy, sell) values (user, 'USD', 145, 146.1234);
insert into fx_rates(user$, currency, buy, sell) values (user, 'EUR', 194.9876, 196.56);
insert into cipher_suites(user$, code, a_level) values (user, 'cipher suite 1', 1);
insert into cipher_suites(user$, code, a_level) values (user, 'cipher suite 2', 2);
insert into cipher_suites(user$, code, a_level) values (user, 'cipher suite 3', 3);
insert into cipher_suites(user$, code, a_level) values (user, 'cipher suite 4', 4);
insert into cipher_suites(user$, code, a_level) values (user, 'cipher suite 5', 5);
insert into cipher_suites(user$, code, a_level) values (user, 'SSL_RSA_WITH_RC4_128_SHA', 7);
insert into cipher_suites(user$, code, a_level) values (user, 'SSL_RSA_WITH_RC4_128_MD5', 6);
commit;
exit

insert into applications(status, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, cipher_suite) values ('W', 1, 1, 1, 1, 77772429542, '111');
insert into applications(status, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, cipher_suite) values ('W', 1, 1, 2, 1, 77017101275, '111');
--insert into applications(status, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, cipher_suite) values ('P', 1, 1, 1, 1, 77774401192, '111');
--insert into app_accounts values (1, 2, 'S', 'XML', 'Number 1', 'Name 1', 0, 1, 'KZT', 2, 'USD', 22, 3, 'EUR', 33, 1, 2, 3, 4);
--insert into app_accounts values (1, 2, 'R', 'ISO', 'Number 2', 'Name 2', 0, 10, 'KZT', 20, 'USD', 220, 30, 'EUR', 330, 5, 6, 7, 8);
--insert into applications(status, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, cipher_suite) values ('D', 1, 1, 1, 1, 77774401193, '111');
--insert into applications(status, country_id$, doc_type_id$, doc_id, branch_id$, phone_number, cipher_suite, decline_reason) values ('D', 1, 1, 1, 1, 77774401193, '111', 'Aaaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbbbbb ccccccccccccccccccccccccccccccccccccc dddddddddddddddddddddddddddddddddddddddd eeeeeeeeeeeee fffffffffffffff ggggggggg.');
insert into clients(user$, country_id$, doc_type_id$, doc_id, branch_id$) values (user, 1, 1, 1, 1);
insert into contracts(user$, client_id$, si, status, applied_at, phone_number, cipher_suite) values (user, 1, 1, 'A', SYSDATE, 77772429542, 'cipher suite 3');
insert into contracts(user$, client_id$, si, status, applied_at, phone_number, cipher_suite) values (user, 1, 1, 'A', SYSDATE, 77017101275, 'cipher suite 3');

insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'S', 'ISO', '1234567', 'Visa', 1);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'R', 'ISO', '1234567', 'AmEx', 1);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'S', 'XML', 'B1234567', 'Текущий счет 1', 2);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'S', 'XML', 'T1234567', 'Текущий счет 2', 3);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'R', 'ISO', '40000000123', 'MasterCard', 2);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 1, 'R', 'XML', 'X1234567', 'Счет 3', 3);

insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'S', 'ISO', '1234567', 'Visa', 1);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'R', 'ISO', '1234567', 'AmEx', 1);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'S', 'XML', 'B1234567', 'Текущий счет A', 2);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'S', 'XML', 'T1234567', 'Текущий счет Б', 3);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'R', 'ISO', '40000000123', 'MasterCard', 2);
insert into accounts(user$, contract_id$, kind, interface, a_number, name, priority) values (USER, 2, 'R', 'XML', 'X1234567', 'Счет В', 3);
--insert into srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, sr_type_id$, rmki, sai, sc, contract_id$, status, src_interface, src_account, rn, sn)
--values (srs_seq.nextval, 7774401193, 1, 'cipher suite 4', 'FRONT', '5', 4, 1, 1, 1, 1, 'A', 'XML', '40123456789', 'FF9034850495845894', '99080485928547257854759488');
--insert into srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, rn, sn)
--values (srs_seq.nextval, 7774401193, 1, 'cipher suite 4', 'FRONT', '6', 1, 1, 1, 'FF9034850495845894', '99080485928547257854759488');
commit;
exit
select * from system_log;
select * from srs;
select * from active_srs;
select * from messages;
select * from contracts;
select * from accounts;
select * from sr_types;
select * from sr_type_codes;
select * from applications;
exit
delete from active_srs;
update contracts set si = 1, password_tries = 0;

begin
  for i in 1..100000 loop
    if i mod 2 = 0 then
      insert into clients(id$, user$, country_id$, doc_type_id$, doc_id, branch_id$, last_name, first_name) values (clients_seq.nextval, user, 1, 1, 'doc'||clients_seq.currval, 1, 'lastname'||clients_seq.currval, 'firstname'||clients_seq.currval);
    else
      insert into clients(id$, user$, country_id$, doc_type_id$, doc_id, branch_id$, last_name, first_name) values (clients_seq.nextval, user, 1, 1, 'doc'||clients_seq.currval, 2, 'lastname'||clients_seq.currval, 'firstname'||clients_seq.currval);
    end if;
  end loop;
end;
/

begin
  for i in 1..100000 loop
    insert into contracts(id$, user$, client_id$, si, status, applied_at, phone_number, cipher_suite, a_number) values (contracts_seq.nextval, user, contracts_seq.currval, contracts_seq.currval, 'A', sysdate, contracts_seq.currval, 'cipher'||contracts_seq.currval, 'number'||contracts_seq.currval);
  end loop;
end;
/

begin
  for i in 1..100000 loop
    insert into srs(id$, phone_number, csi, contract_id$, src_name, src_interface, src_account, dst_name, dst_interface, dst_account, amount, currency, description)
    values (srs_seq.nextval, srs_seq.currval, srs_seq.currval, srs_seq.currval, 'src'||srs_seq.currval, 'XML', 'srcacc'||srs_seq.currval, 'dst'||srs_seq.currval, 'ISO', 'dstacc'||srs_seq.currval, srs_seq.currval, 'KZT', 'desc'||srs_seq.currval);  end loop;
end;
/
