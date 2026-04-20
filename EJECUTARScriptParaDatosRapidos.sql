-- Instrucciones:
--   1. crear la base de datos ecommerce_db primero que nada y
-- 		no olvidar cambiar la contraseña en persistence en el proyecto.
--   2. ejecutar el script despues de correr la app al menos
--      una vez (para que hibernate genere las tablas).
--   3. la contraseña del admin es: Admin1234
USE ecommerce_db;

-- ignore evita duplicados si applistener ya las creó
INSERT IGNORE INTO categorias (nombre) VALUES
    ('Vestidos'),
    ('Blusas'),
    ('Conjuntos'),
    ('Blazers'),
    ('Faldas'),
    ('Pantalones'),
    ('Tops'),
    ('Accesorios');

-- Proveedores de muestra
INSERT IGNORE INTO proveedores (nombre, rfc, telefono, correo, direccion, activo) VALUES
    ('Textiles del Norte SA', 'TDN010101AB1', '6441234567', 'ventas@textilesnorte.mx', 'Av. Industrial 100, Cd. Obregón, Sonora', 1),
    ('Confecciones Velour MX', 'CVM020202CD2', '6442345678', 'contacto@velour.mx', 'Blvd. Las Torres 250, Hermosillo, Sonora', 1),
    ('Importadora Moda Italia', 'IMI030303EF3', '5512345678', 'pedidos@modaitalia.mx', 'Polanco 45, CDMX', 1);

-- admin. contraseña: Admin1234
INSERT IGNORE INTO usuarios (nombre, correo, contraseña, telefono, rol)
VALUES (
    'Martin Bernal',
    'admin@gmail.com',
    'Admin1234',
    '6440000000',
    'ADMINISTRADOR'
);

-- cliente. contraseña: Cliente123
INSERT IGNORE INTO usuarios (nombre, correo, contraseña, telefono, rol)
VALUES (
    'equipo 5',
    'equipo@gmail.com',
    'Cliente123',
    '6621000001',
    'CLIENTE'
);

SET @vestidos   = (SELECT id FROM categorias WHERE nombre = 'Vestidos'   LIMIT 1);
SET @blusas     = (SELECT id FROM categorias WHERE nombre = 'Blusas'     LIMIT 1);
SET @conjuntos  = (SELECT id FROM categorias WHERE nombre = 'Conjuntos'  LIMIT 1);
SET @blazers    = (SELECT id FROM categorias WHERE nombre = 'Blazers'    LIMIT 1);
SET @faldas     = (SELECT id FROM categorias WHERE nombre = 'Faldas'     LIMIT 1);
SET @pantalones = (SELECT id FROM categorias WHERE nombre = 'Pantalones' LIMIT 1);
SET @tops       = (SELECT id FROM categorias WHERE nombre = 'Tops'       LIMIT 1);
SET @accesorios = (SELECT id FROM categorias WHERE nombre = 'Accesorios' LIMIT 1);

INSERT INTO productos (nombre, descripcion, precio, imagen_url, stock, color, activo, categoria_id) VALUES

-- VESTIDOS (2)
('Vestido Azul Largo',
 'Vestido largo de corte fluido en tono azul profundo. Ideal para ocasiones formales y eventos nocturnos.',
 1290.00, 'assets/img/vestidos/vestidoAzulLargo.png', 30, 'AZUL', 1, @vestidos),

('Vestido Negro Corto',
 'Vestido corto entallado en negro clásico. Versátil para salidas casuales o cenas elegantes.',
 980.00, 'assets/img/vestidos/vestidoNegroCorto.png', 25, 'NEGRO', 1, @vestidos),

-- BLUSAS (3)
('Blusa Cruzada Estampado Gris',
 'Blusa cruzada con estampado geométrico en tonos grises. Corte moderno y cómodo.',
 520.00, 'assets/img/blusas/blusaCruzadaEstampadoGris.png', 40, 'GRIS', 1, @blusas),

('Blusa Dorada Metálica',
 'Blusa con acabado metálico dorado. Perfecta para looks de noche con un toque glamouroso.',
 690.00, 'assets/img/blusas/blusaDoradaMetalica.png', 20, 'DORADO', 1, @blusas),

('Blusa Rayas Azules',
 'Blusa de rayas horizontales en azul y blanco. Estilo marinero contemporáneo.',
 450.00, 'assets/img/blusas/blusaRayasAzules.png', 35, 'AZUL', 1, @blusas),

-- CONJUNTOS (5)
('Conjunto Azul con Volantes',
 'Conjunto de dos piezas en azul eléctrico con detalle de volantes. Fresco y veraniego.',
 1150.00, 'assets/img/conjuntos/conjuntoAzulVolantes.png', 18, 'AZUL', 1, @conjuntos),

('Conjunto Estampado Beige Negro',
 'Conjunto pantalón y top con estampado bicolor beige y negro. Elegante y atemporal.',
 1350.00, 'assets/img/conjuntos/conjuntoEstampadoBeigeNegro.png', 15, 'BEIGE', 1, @conjuntos),

('Conjunto Negro Casual',
 'Conjunto casual todo negro de pantalón y blusa. Minimalismo sofisticado para el día a día.',
 1100.00, 'assets/img/conjuntos/conjuntoNegroCasual.png', 22, 'NEGRO', 1, @conjuntos),

('Conjunto Rayas Naranja',
 'Conjunto de rayas en naranja vibrante. Ideal para looks de primavera llenos de energía.',
 1050.00, 'assets/img/conjuntos/conjuntoRayasNaranja.png', 12, 'NARANJA', 1, @conjuntos),

('Conjunto Rojo Pantalón',
 'Conjunto rojo intenso de pantalón y blazer corto. Presencia y estilo en un solo look.',
 1480.00, 'assets/img/conjuntos/conjuntoRojoPantalon.png', 10, 'ROJO', 1, @conjuntos),

-- BLAZERS (3)
('Blazer Azul con Rayas',
 'Blazer estructurado en azul marino con rayas sutiles. Perfecto sobre jeans o traje.',
 1750.00, 'assets/img/blazers/blazerAzulRayas.png', 14, 'AZUL', 1, @blazers),

('Blazer Gris con Rayas',
 'Blazer clásico gris con rayas discretas. Indispensable en cualquier guardarropa profesional.',
 1680.00, 'assets/img/blazers/blazerGrisRayas.png', 16, 'GRIS', 1, @blazers),

('Blazer Negro Casual',
 'Blazer negro de corte relajado. Combinable con prácticamente cualquier outfit.',
 1550.00, 'assets/img/blazers/blazerNegroCasual.png', 20, 'NEGRO', 1, @blazers),

-- FALDAS (3)
('Falda Beige Midi con Abertura',
 'Falda midi en beige cálido con abertura lateral. Elegante y femenina para cualquier ocasión.',
 780.00, 'assets/img/faldas/faldaBeigeMidiAbertura.png', 28, 'BEIGE', 1, @faldas),

('Falda Negra Corta',
 'Minifalda negra clásica. Un básico atemporal que combina con todo.',
 620.00, 'assets/img/faldas/faldaNegraCorta.png', 32, 'NEGRO', 1, @faldas),

('Falda Plisada Estampado Café',
 'Falda plisada con estampado floral en tonos café y tierra. Romántica y versátil.',
 850.00, 'assets/img/faldas/faldaPlisadaEstampadoCafe.png', 20, 'CAFE', 1, @faldas),

-- PANTALONES (3)
('Pantalón Estampado Beige Negro',
 'Pantalón de tiro alto con estampado bicolor beige y negro. Tendencia y comodidad.',
 920.00, 'assets/img/pantalones/pantalonEstampadoBeigeNegro.png', 24, 'BEIGE', 1, @pantalones),

('Pantalón Negro Fluido',
 'Pantalón negro de tela fluida y caída impecable. Cómodo para la oficina o salidas.',
 880.00, 'assets/img/pantalones/pantalonNegroFluido.png', 30, 'NEGRO', 1, @pantalones),

('Pantalón Negro Sastre Ancho',
 'Pantalón sastre de corte wide leg en negro. Moderno y favorecedor para cualquier silueta.',
 1100.00, 'assets/img/pantalones/pantalonNegroSastreAncho.png', 18, 'NEGRO', 1, @pantalones),

-- TOPS (2)
('Top Beige Cuello Alto',
 'Top de punto en beige con cuello alto. Básico de temporada que combina con todo.',
 490.00, 'assets/img/tops/topBeigeCuelloAlto.png', 40, 'BEIGE', 1, @tops),

('Top Negro Escote Drapeado',
 'Top negro con escote drapeado asimétrico. Sofisticado y atrevido para noches especiales.',
 560.00, 'assets/img/tops/topNegroEscoteDrapeado.png', 28, 'NEGRO', 1, @tops),

-- ACCESORIOS (4) — sin tallas
('Bolsa Beige Asa Trenzada',
 'Bolsa de mano en beige con asa trenzada artesanal. Complemento perfecto para looks casuales.',
 1350.00, 'assets/img/accesorios/bolsaBeigeAsaTrenzada.png', 15, 'BEIGE', 1, @accesorios),

('Bolsa Negra',
 'Bolsa negra de uso diario. Espaciosa y práctica con acabado mate.',
 980.00, 'assets/img/accesorios/bolsaNegraPRUEBA.png', 10, 'NEGRO', 1, @accesorios),

('Bolsa Rosa Estructurada',
 'Bolsa estructurada en rosa palo. Chic y femenina, ideal para outfits de primavera.',
 1450.00, 'assets/img/accesorios/bolsaRosaEstructurada.png', 12, 'ROSA', 1, @accesorios),

('Bolsa Vino Grande',
 'Bolsa grande en vino oscuro. Capacidad y estilo para el día a día más exigente.',
 1580.00, 'assets/img/accesorios/bolsaVinoGrande.png', 8, 'VINO', 1, @accesorios);

INSERT INTO producto_tallas (producto_id, talla)
SELECT p.id, t.talla
FROM productos p
CROSS JOIN (SELECT 'S' AS talla UNION ALL SELECT 'M' UNION ALL SELECT 'L') t
WHERE p.imagen_url IN (
    'assets/img/vestidos/vestidoAzulLargo.png',
    'assets/img/vestidos/vestidoNegroCorto.png',
    'assets/img/blusas/blusaCruzadaEstampadoGris.png',
    'assets/img/blusas/blusaDoradaMetalica.png',
    'assets/img/blusas/blusaRayasAzules.png',
    'assets/img/faldas/faldaBeigeMidiAbertura.png',
    'assets/img/faldas/faldaNegraCorta.png',
    'assets/img/faldas/faldaPlisadaEstampadoCafe.png',
    'assets/img/pantalones/pantalonEstampadoBeigeNegro.png',
    'assets/img/pantalones/pantalonNegroFluido.png',
    'assets/img/pantalones/pantalonNegroSastreAncho.png',
    'assets/img/tops/topBeigeCuelloAlto.png',
    'assets/img/tops/topNegroEscoteDrapeado.png'
);

INSERT INTO producto_tallas (producto_id, talla)
SELECT p.id, t.talla
FROM productos p
CROSS JOIN (SELECT 'XS' AS talla UNION ALL SELECT 'S' UNION ALL SELECT 'M' UNION ALL SELECT 'L' UNION ALL SELECT 'XL') t
WHERE p.imagen_url IN (
    'assets/img/conjuntos/conjuntoAzulVolantes.png',
    'assets/img/conjuntos/conjuntoEstampadoBeigeNegro.png',
    'assets/img/conjuntos/conjuntoNegroCasual.png',
    'assets/img/conjuntos/conjuntoRayasNaranja.png',
    'assets/img/conjuntos/conjuntoRojoPantalon.png',
    'assets/img/blazers/blazerAzulRayas.png',
    'assets/img/blazers/blazerGrisRayas.png',
    'assets/img/blazers/blazerNegroCasual.png'
);

INSERT INTO producto_stock_talla (producto_id, talla, cantidad)
SELECT p.id, t.talla,
    CASE t.talla
        WHEN 'S' THEN FLOOR(p.stock * 0.30)
        WHEN 'M' THEN FLOOR(p.stock * 0.45)
        WHEN 'L' THEN CEIL(p.stock  * 0.25)
    END
FROM productos p
CROSS JOIN (SELECT 'S' AS talla UNION ALL SELECT 'M' UNION ALL SELECT 'L') t
WHERE p.imagen_url IN (
    'assets/img/vestidos/vestidoAzulLargo.png',
    'assets/img/vestidos/vestidoNegroCorto.png',
    'assets/img/blusas/blusaCruzadaEstampadoGris.png',
    'assets/img/blusas/blusaDoradaMetalica.png',
    'assets/img/blusas/blusaRayasAzules.png',
    'assets/img/faldas/faldaBeigeMidiAbertura.png',
    'assets/img/faldas/faldaNegraCorta.png',
    'assets/img/faldas/faldaPlisadaEstampadoCafe.png',
    'assets/img/pantalones/pantalonEstampadoBeigeNegro.png',
    'assets/img/pantalones/pantalonNegroFluido.png',
    'assets/img/pantalones/pantalonNegroSastreAncho.png',
    'assets/img/tops/topBeigeCuelloAlto.png',
    'assets/img/tops/topNegroEscoteDrapeado.png'
);

INSERT INTO producto_stock_talla (producto_id, talla, cantidad)
SELECT p.id, t.talla,
    CASE t.talla
        WHEN 'XS' THEN FLOOR(p.stock * 0.10)
        WHEN 'S'  THEN FLOOR(p.stock * 0.25)
        WHEN 'M'  THEN FLOOR(p.stock * 0.35)
        WHEN 'L'  THEN FLOOR(p.stock * 0.20)
        WHEN 'XL' THEN CEIL(p.stock  * 0.10)
    END
FROM productos p
CROSS JOIN (SELECT 'XS' AS talla UNION ALL SELECT 'S' UNION ALL SELECT 'M' UNION ALL SELECT 'L' UNION ALL SELECT 'XL') t
WHERE p.imagen_url IN (
    'assets/img/conjuntos/conjuntoAzulVolantes.png',
    'assets/img/conjuntos/conjuntoEstampadoBeigeNegro.png',
    'assets/img/conjuntos/conjuntoNegroCasual.png',
    'assets/img/conjuntos/conjuntoRayasNaranja.png',
    'assets/img/conjuntos/conjuntoRojoPantalon.png',
    'assets/img/blazers/blazerAzulRayas.png',
    'assets/img/blazers/blazerGrisRayas.png',
    'assets/img/blazers/blazerNegroCasual.png'
);

-- ─── VERIFICACIÓN ──────────────────────────────────────────
SELECT 'Categorías'                   AS tabla, COUNT(*) AS total FROM categorias
UNION ALL SELECT 'Usuarios',                     COUNT(*)         FROM usuarios
UNION ALL SELECT 'Productos',                    COUNT(*)         FROM productos
UNION ALL SELECT 'producto_tallas',              COUNT(*)         FROM producto_tallas
UNION ALL SELECT 'producto_stock_talla',         COUNT(*)         FROM producto_stock_talla;
