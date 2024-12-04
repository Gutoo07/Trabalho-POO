--use master
--drop database trabalhopoo
--drop table comanda
--drop table comanda_produto
--drop table cliente
--drop table produto


--------------------------------------
CREATE DATABASE trabalhopoo
GO
--------------------------------------------------------------------
USE trabalhopoo
GO
CREATE TABLE cliente(
	id			int,
	nome		varchar(100),
	telefone	char(11),
	cpf			char(11)	null
	PRIMARY KEY (id)
)
GO
CREATE TABLE produto(
	id		int,
	nome	varchar(30),
	valor	decimal(7,2)
	PRIMARY KEY (id)
) 
GO
CREATE TABLE cliente_produto(
	clienteId		int,
	produtoId		int,
	qtd				int
	PRIMARY KEY(clienteId, produtoId)
	FOREIGN KEY(clienteId) REFERENCES cliente(id),
	FOREIGN KEY(produtoId) REFERENCES produto(id)
)

--Cliente sem produtos
SELECT c.id
FROM cliente c
LEFT OUTER JOIN cliente_produto cp
ON c.id = cp.clienteId
WHERE cp.clienteId IS NULL
--Produto em nenhum carrinho
SELECT p.id
FROM produto p
LEFT OUTER JOIN cliente_produto cp
ON p.id = cp.produtoId
WHERE cp.produtoId IS NULL AND p.id = ?

--Add produto no carrinho
IF EXISTS (SELECT 1 FROM cliente_produto WHERE produtoId = 0 AND clienteId = 0)
BEGIN
   UPDATE cliente_produto
    SET qtd = qtd + 1
    WHERE produtoId = 0 AND clienteId = 0
END
ELSE
BEGIN
    INSERT INTO cliente_produto (clienteId, produtoId, qtd)
    VALUES (0, 0, 1)
END

-- -1 produto do carrinho
IF (SELECT qtd FROM cliente_produto WHERE clienteId = 1 AND produtoId = 1) > 1
BEGIN
	UPDATE cliente_produto
	SET	qtd = qtd - 1
	WHERE clienteId = 1 AND produtoId = 1
END
ELSE
BEGIN
	DELETE FROM cliente_produto
	WHERE clienteId = 1 AND produtoId = 1
END


--Pesquisar carrinho de cliente
SELECT p.id, p.nome, p.valor, cp.qtd
FROM produto p
INNER JOIN cliente_produto cp
ON p.id = cp.produtoId
WHERE cp.clienteId = 0

----------------------------------------------
----------------------------------------------
select * from cliente
select * from produto
select * from cliente_produto

drop table produto
drop table cliente
drop table cliente_produto
