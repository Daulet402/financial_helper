insert into fc.texts (key, text_ru, text_en, text_kk) values ('TOTAL_TEXT', 'Итого', 'Total','');
insert into fc.subcategory(name, categoryid, id, name_ru, name_en, name_kk, color) values ('INTO_FAMILY_BUDGET', 1, 74, 'В семейный бюджет', 'Into the family budget', null, null);
alter table fc.financial_control add column comment text ;
update fc.texts set text_ru = 'Запись успешно сохранена' where key = 'RECORD_SAVED_TEXT';
insert into fc.texts(key, text_ru,text_en, text_kk) values('YES_TEXT', 'Да', 'Yes', '');
insert into fc.texts(key, text_ru,text_en, text_kk) values('NO_TEXT', 'Нет', 'No', '');
insert into fc.texts(key, text_ru,text_en, text_kk) values('TYPE_COMMENT_TEXT', 'Отправьте мне комментарии', 'Type here comment', '');
insert into fc.texts(key, text_ru,text_en, text_kk) values('ADD_COMMENT_TEXT', 'Хотите ли Вы добавить комментарии ?', 'Would you like to add comments ?', '');
