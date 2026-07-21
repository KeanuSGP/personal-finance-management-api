- Adicionar interceptador no axios
- Tratar códigos de erro no backend (403 e 401)
- Adicionar endpoint para logout no backend

- DESIGN
-- Terminar página de Transactions
--- Criar componentes de Seleção (eles devem carregar como opções os dados que o usuário tem de cada entidade)

--------- 23/05/2026
- Verificar uma forma de que as mudanças nos campos de amount e dueDate do formulário de transação sejam postas no objeto de transação criando um objeto de parcela caso ela não exista.

- Caso o usuário precise inserir mais de uma parcela, dentro do objeto de transação deve-se criar parcelas correspondentes a quantidade estabelecida pelo usuário

29/05/2026

- Verificar forma de adequar a formatação da transação para que a requisição ocorra sem erros.
- Problemas principal: Os selects setam os valores setando o ID no objeto da transação, mas a formatação busca as entidades pelo nome (que só funciona na primeira vez que os valores são setados como strings no objeto da transação)