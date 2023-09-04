USE PROYECTO_LP1;

INSERT INTO CARGO (DESC_CARGO)
VALUES 
('Administrador'), ('Empleado');

INSERT INTO USUARIO (NOMBRES, APELLIDOS,TELEFONO,CORREO, NOMBRE_USUARIO, CONTRASENA, COD_CARGO, ACTIVO)
VALUES
('Jack','Moreno','+51 986968728','jackfra@gmail.com', 'jack_username', 'jack_password', 1, 1),
('Darwen','Tantalalena','+51 956856965','darweTa@gmail.com', 'darwen_username', 'darwen_password', 2, 1),
('Jheicer','Rodriguez','+51 965987956','jheiRo@gmail.com', 'jheicer_username', 'jheicer_password', 2, 1),
('Andree','Palomino','+51 952632523','andrePal@gmail.com', 'andree_username', 'andree_password', 2, 1),
('Angel','Mamane','+51 974956985','angelMama@gmail.com', 'angel_username', 'angel_password', 2, 0);
    
INSERT INTO CLIENTE (NOMBRES, APELLIDOS, DNI, TELEFONO, CORREO, DIRECCION)
VALUES
('Juan', 'Perez', '12345678', '987654321', 'juan.perez@example.com', 'Calle 123'),
('Maria', 'Gomez', '87654321', '123456789', 'maria.gomez@example.com', 'Avenida 456'),
('Pedro', 'Lopez', '56789012', '987654321', 'pedro.lopez@example.com', 'Calle 789'),
('Laura', 'Rodriguez', '23456789', '123456789', 'laura.rodriguez@example.com', 'Avenida 123'),
('Carlos', 'Martinez', '90123456', '987654321', 'carlos.martinez@example.com', 'Calle 456'),
('Ana', 'Sanchez', '34567890', '123456789', 'ana.sanchez@example.com', 'Avenida 789'),
('Luis', 'Gonzalez', '67890123', '987654321', 'luis.gonzalez@example.com', 'Calle 123'),
('Elena', 'Torres', '01234567', '123456789', 'elena.torres@example.com', 'Avenida 456'),
('Mario', 'Rios', '45678901', '987654321', 'mario.rios@example.com', 'Calle 789'),
('Sofia', 'Fernandez', '89012345', '123456789', 'sofia.fernandez@example.com', 'Avenida 123');

INSERT INTO CATEGORIA (NOMBRE_CATEGORIA)
VALUES
('Pollo a la Brasa'),
('Bebidas'),
('Ensaladas'),
('Acompañamientos'),
('Postres'),
('Parrillas'),
('Sopas'),
('Tragos'),
('Carnes'),
('Mariscos');

INSERT INTO PRODUCTOS (COD_CATEGORIA, NOMBRE_PRODUCTO, STOCK, FECHA_VENCIMIENTO, PRECIO, ACTIVO)
VALUES
(1, '1/4 de Pollo a la Brasa', 50, '2023-09-05', 15.99, 1),
(1, '1/2 de Pollo a la Brasa', 30, '2023-09-18', 21.99, 1),
(1, 'Pollo Entero a la Brasa', 20, '2023-09-30', 39.99, 1),
(2, 'Gaseosa Coca-Cola', 100, '2023-08-07', 2.99, 1),
(2, 'Gaseosa Inca Kola', 100, '2023-08-20', 2.99, 1),
(2, 'Jugo de Naranja', 50, '2023-08-25', 3.99, 1),
(3, 'Ensalada Mixta', 40, '2023-08-10', 6.99, 1),
(3, 'Ensalada Caesar', 30, '2023-08-22', 7.99, 1),
(3, 'Ensalada Griega', 35, '2023-09-02', 8.99, 1),
(4, 'Papas Fritas', 80, '2023-08-14', 3.99, 1),
(4, 'Yuca Frita', 60, '2023-08-28', 4.99, 1),
(4, 'Tostones', 70, '2023-09-06', 4.99, 1),
(5, 'Torta de Chocolate', 25, '2023-08-16', 9.99, 1),
(5, 'Cheesecake de Fresa', 20, '2023-08-29', 8.99, 1),
(5, 'Arroz con Leche', 30, '2023-09-10', 6.99, 1),
(6, 'Parrillada Mixta', 15, '2023-08-20', 24.99, 1),
(6, 'Choripán', 40, '2023-08-31', 6.99, 1),
(6, 'Matambre', 30, '2023-09-12', 19.99, 1),
(7, 'Sopa de Pollo', 50, '2023-08-24', 5.99, 1),
(7, 'Chupe de Camarones', 20, '2023-09-04', 12.99, 1),
(7, 'Locro de Gallina', 25, '2023-09-15', 10.99, 1),
(8, 'Pisco Sour', 100, '2023-08-28', 7.99, 1),
(8, 'Margarita', 80, '2023-09-08', 7.99, 1),
(8, 'Chilcano de Pisco', 60, '2023-09-18', 8.99, 1),
(9, 'Lomo Saltado', 35, '2023-08-30', 16.99, 1),
(9, 'Tallarín Saltado', 40, '2023-09-10', 14.99, 1),
(9, 'Seco de Res', 30, '2023-09-22', 17.99, 1),
(10, 'Ceviche Mixto', 30, '2023-09-01', 13.99, 1),
(10, 'Arroz con Mariscos', 25, '2023-09-12', 15.99, 1),
(10, 'Jalea Mixta', 20, '2023-09-23', 17.99, 1),
(1, '1/4 de Pollo a la Parrilla', 40, '2023-09-07', 14.99, 1),
(2, 'Agua Mineral', 60, '2023-08-12', 1.99, 1),
(3, 'Ensalada Caprese', 25, '2023-08-25', 9.99, 1),
(4, 'Camote Frito', 40, '2023-09-08', 4.99, 1),
(5, 'Mazamorra Morada', 30, '2023-09-19', 5.99, 1),
(6, 'Costillitas BBQ', 20, '2023-08-24', 18.99, 1),
(7, 'Sopa Criolla', 35, '2023-09-05', 6.99, 1),
(8, 'Mojito', 70, '2023-09-15', 8.99, 1),
(9, 'Ají de Gallina', 30, '2023-09-26', 13.99, 1),
(10, 'Causa Rellena', 40, '2023-08-31', 11.99, 1),
(1, '1/2 de Pollo a la Parrilla', 25, '2023-09-10', 19.99, 1),
(2, 'Gaseosa Sprite', 80, '2023-09-21', 2.99, 1),
(3, 'Ensalada Waldorf', 30, '2023-09-02', 7.99, 1),
(4, 'Plátano Frito', 45, '2023-09-13', 3.99, 1),
(5, 'Suspiro Limeño', 15, '2023-09-24', 10.99, 1),
(6, 'Chuleta de Cerdo', 20, '2023-09-05', 16.99, 1),
(7, 'Chupe de Pollo', 25, '2023-09-16', 11.99, 1),
(8, 'Caipirinha', 50, '2023-09-27', 7.99, 1),
(9, 'Causa de Pollo', 35, '2023-08-31', 9.99, 1),
(10, 'Leche de Tigre', 40, '2023-09-11', 6.99, 1),
(1, 'Pollo a la Broaster', 30, '2023-09-14', 17.99, 1),
(2, 'Agua con Gas', 60, '2023-08-19', 1.99, 1),
(3, 'Ensalada de Palta', 20, '2023-09-01', 8.99, 1),
(4, 'Taro Frito', 35, '2023-09-12', 4.99, 1),
(5, 'Picarones', 40, '2023-09-23', 5.99, 1),
(6, 'Bondiola de Cerdo', 15, '2023-08-28', 21.99, 1),
(7, 'Sopa de Quinua', 25, '2023-09-08', 7.99, 1),
(8, 'Daiquiri', 70, '2023-09-18', 8.99, 1),
(9, 'Arroz Chaufa', 30, '2023-09-29', 12.99, 1),
(10, 'Chupe de Mariscos', 20, '2023-08-31', 14.99, 1);

INSERT INTO ESTADO (COD_ESTADO, NOMBRE)
VALUES
  ('CAN', 'CANCELADO'),
  ('FIN', 'FINALIZADO'),
  ('EMI', 'EMITIDO');
  

