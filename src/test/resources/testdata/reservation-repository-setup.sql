insert into member (id, name, email, password, photo_url, authority)
values (1, 'member', 'member@email.com', 'password', 'photoUrl', 'ROLE_USER');

insert into product (id, name, category, description, star_avg, thumbnail, address)
values (1, '호텔1', 'HOTEL', '호텔1 설명', 4.1, '호텔1 사진 url', '서울시 강남구 한남동');

insert into product (id, name, category, description, star_avg, thumbnail, address)
values (2, '호텔2', 'HOTEL', '호텔2 설명', 3.5, '호텔2 사진 url', '서울시 송파구 잠실동');

insert into room (id, name, product_id, description, standard, capacity, price, check_in, check_out)
values (1, '객실1', 1, '객실1 설명', 2, 4, 95000, '13:00:00', '12:00:00');

insert into room (id, name, product_id, description, standard, capacity, price, check_in, check_out)
values (2, '객실2', 1, '객실2 설명', 2, 4, 105000, '14:00:00', '12:30:00');

insert into room (id, name, product_id, description, standard, capacity, price, check_in, check_out)
values (3, '객실3', 2, '객실3 설명', 3, 5, 125000, '13:00:00', '12:00:00');

insert into reservation (id, member_id, pay_method, total_price)
values (1, 1, 'CREDIT_CARD', 240000);

insert into reservation (id, member_id, pay_method, total_price)
values (2, 1, 'CREDIT_CARD', 240000);

insert into reservation_product (id, reservation_id, room_id, start_date, end_date, price)
values (1, 1, 1, '2023-12-10', '2023-12-13', 95000);

insert into reservation_product (id, reservation_id, room_id, start_date, end_date, price)
values (2, 1, 3, '2023-12-13', '2023-12-15', 125000);

insert into reservation_product (id, reservation_id, room_id, start_date, end_date, price)
values (3, 2, 2, '2023-12-24', '2023-12-27', 105000);