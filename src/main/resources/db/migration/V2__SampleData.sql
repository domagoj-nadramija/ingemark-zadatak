
-- Add sample customers
INSERT INTO webshop.webshop_customer
(id, first_name, last_name, email)
VALUES (1000,'John', 'Doe', 'john@doe.com'),
       (1001,'Jane', 'Doe', 'jane@doe.com');


-- Add sample products
INSERT INTO webshop.webshop_product
(id, code, "name", price_hrk, description, is_available)
VALUES (2000,'5475331705', 'Oral-B električna četkica za zube Pro 2000', 499.0, 'Oral-B električna četkica za zube Pro 2000 s dvije metode četkanja i senzorom pritiska, pruža izvanredno temeljito čišćenje. Ima ugrađeni timer koji osigurava optimalno vrijeme četkanja.', true),
       (2001,'1063556262', 'Audio-Technica AT-LPW30TK gramofon', 2349.0, 'Audio-Technica AT-LPW30TK je visokokvalitetni gramofon s ručnim upravljanjem, motor s pogonom na remen s mogućnosti rotacije pri brzini od 33–1/3 ili 45 okretaja u minuti i glava za podizanje AT-VM95C s dva magneta u pokretu. Omogućuje povezivanje s uređajima koji nemaju namjenski fono priključak za gramofon', true),
       (2002,'3334507236', 'Black+Decker BXMX500E ručni mikser', 222.0, 'Ručna mješalica Black + Decker BXMX500E može se pohvaliti ulaznom snagom od 500 W, pet brzina i turbo funkcijom. Ima površinu od nehrđajućeg čelika s površinskom obradom protiv otisaka prstiju. Metlice i kuke mogu se jednostavno ukloniti pritiskom na gumb.', true),
       (2003,'1274341108', 'Forever ForeVigo SW-300 pametni sportski sat', 309.0 , 'Forever pametni sportski sat ForeVigo SW-300 rozo zlatne boje s Bluetooth tehnologijom, aplikacijom, vodootpornošću IP67 i dodatnim remenom.', false),
       (2004,'5711941190', 'Igroljub društvena igra Catan', 222.0, 'Zabavna igra za promicanje taktičkog razmišljanja. Igra je pogodna za 3 ili 4 igrača starija od 10 godina. Potrebno je oko 75 minuta.', true);


-- Add sample orders
INSERT INTO webshop.webshop_order
(id, customer_id, status, price_hrk, price_eur)
VALUES (3000, 1000, 'DRAFT', 943.0, 0),
       (3001, 1000, 'SUBMITTED', 2349.0, 312.35),
       (3002, 1001, 'DRAFT', 222.0, 0),
       (3003, 1001, 'SUBMITTED', 927.0, 123.26);


-- Add sample order items
INSERT INTO webshop.webshop_order_item
(id, order_id, product_id, quantity)
VALUES (4000, 3000, 2004, 1),
       (4001, 3000, 2000, 1),
       (4002, 3000, 2002, 1),
       (4003, 3001, 2001, 1),
       (4004, 3002, 2004, 1),
       (4005, 3003, 2003, 3);

