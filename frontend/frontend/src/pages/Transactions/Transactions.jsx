import Header from '../../components/EntityPageHeader/EntityPageHeader'
import Navbar from '../../components/Navbar/Navbar';
import Table from '../../components/Table/Table'
import '../../pages/Transactions/Transactions.css'
import Modal from "../../components/Modals/Modal"
import TransactionForm from '../../components/Modals/TransactionForm'
import { useState, useEffect, useContext } from 'react'
import { api } from '../../services/api'
import InstallmentModal from '../../components/Modals/InstallmentForm'
import DeleteModal from '../../components/Modals/DeleteModal';
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider'


const Transactions = () => {
    const today = new Date().toLocaleDateString('pt-BR').split('/');
    const todayForDateInput = `${today[2]}-${today[1]}-${today[0]}`
    const [isOpen, setIsOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [installmentQtd, setInstallmentQtd] = useState(1);
    const [activeInstallmentModal, setActiveInstallmentModal] = useState(false);
    const { addToast } = useContext(toastContext)


    const getDueDate = (date) => {
        const actualDay = Number(date.split('-')[2]);
        const actualMonth = Number(date.split('-')[1]);
        const actualYear = Number(date.split('-')[0]);
        const nextMonth = actualMonth + 1;
        const dueDate = new Date(actualYear, actualMonth + 1, 0);
        const lastDay = dueDate.getDate();
        dueDate.setDate(Math.min(actualDay, lastDay));
        const dueDateFormatted = dueDate.toLocaleDateString('pt-BR').split('/')
        const dueDateForInput = `${dueDateFormatted[2]}-${dueDateFormatted[1]}-${dueDateFormatted[0]}`
        return dueDateForInput;
    }

    const INITIAL_TRANSACTION = {
        "doc": "",
        "issueDate": todayForDateInput,
        "type": "",
        "description": "",
        "categories": null,
        "installments": [
            {
                amount: 0,
                dueDate: getDueDate(todayForDateInput)
            }
        ],
        "counterparty": null,
        "financialAccount": null
    }
    const [transaction, setTransaction] = useState(INITIAL_TRANSACTION)
    const [transactionWithInstallments, setTransactionWithInstallments] = useState(transaction);
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [formatedData, setFormatedData] = useState([]);
    const [selected, setSelected] = useState(0);
    const [selectedId, setSelectedId] = useState(0);
    const [selectedInstallment, setSelectedInstallment] = useState(0);
    const hasSelection = selectedId != 0;
    const [openDeleteModal, setOpenDeleteModal] = useState(false);
    const [deleteContext, setDeleteContext] = useState('installment');

    const columns = [
        { header: "DOCUMENTO", accessor: "doc" },
        { header: "NÚMERO PARCELA", accessor: "installmentNumber" },
        { header: "EMISSÃO", accessor: "issueDate", type: 'date' },
        { header: "TIPO", accessor: "type" },
        { header: "DESCRIÇÃO", accessor: "description" },
        { header: "VALOR", accessor: "amount", type: "money" },
        { header: "VENCIMENTO", accessor: "dueDate", type: 'date' },
        { header: "SITUAÇÃO", accessor: "status" },
        { header: "CONTA FINANCEIRA", accessor: "financialAccount" },
        { header: "CONTRAPARTE", accessor: "counterparty" },
        { header: "CATEGORIAS", accessor: "categories" }
    ]

    const validateTransactionData = (obj) => {
        for (const key in obj) {
                    console.log(obj)
            console.log(key)
            if (obj[key] === null) {
                addToast("Todos os campos devem ser preenchidos!", "error")
                return false;
            } else {
                if (obj[key].length < 1) {
                    addToast("Todos os campos devem ser preenchidos!", "error")
                    return false;
                }
            }
        }
        return true;
    }

    const handleInputChanges = (e) => {
        const { name, value } = e.target

        if (name === 'amount' || name === 'dueDate') {
            const updatedInstallments = transaction.installments[0];
            if (name === 'amount') {
                if (value < 0) {
                    addToast("O valor não pode ser negativo.", "error")
                    e.target.blur();
                    return updatedInstallments[name] = 0;
                }
            }
            updatedInstallments[name] = value;
            return setTransaction(prev => ({ ...prev, installments: [updatedInstallments] }))
        }

        if (name === 'installmentQuantity') {
            return setInstallmentQtd(value);
        }

        setTransaction(prev => ({ ...prev, [name]: value }));
    }

    const createInstallments = () => {
        if (installmentQtd <= 1) return;

        const copyInstallments = structuredClone(transactionWithInstallments.installments);

        let standardDay = Number(transaction.installments[0].dueDate.split('-')[2]);


        if (copyInstallments.length < installmentQtd) {
            const difference = installmentQtd - copyInstallments.length;
            const amount = copyInstallments[0].amount;

            for (let i = 0; i < difference; i++) {
                const dueDate = new Date(copyInstallments[i].dueDate);
                const month = dueDate.getMonth() + 1;
                const correctDate = new Date(dueDate.getFullYear(), month + 1, 0);
                const currentDay = correctDate.getDate();
                correctDate.setDate(Math.min(standardDay, currentDay))

                copyInstallments.push({ amount: amount, dueDate: correctDate.toISOString().split("T")[0] })

            }

            setTransactionWithInstallments(prev => ({ ...prev, installments: copyInstallments }));

        }
    }

    const handleInstallmentInputChanges = (e) => {
        const { name, value } = e.target;
        const currentId = e.currentTarget.dataset.id;

        const copyInstallments = transactionWithInstallments.installments;

        copyInstallments.forEach((installment, index) => {
            if (currentId == index) {
                if (name === 'amount' && value < 0) {
                    addToast("O valor não pode ser negativo.", "error")
                } else {
                    installment[name] = value;
                }
            }
        })

        setTransactionWithInstallments(prev => ({ ...prev, installments: copyInstallments }));

    }


    const handleSelectChanges = (e) => {
        const { dataset, textContent } = e.target

        if (dataset.type == 'type') {
            if (textContent == 'DÉBITO') {
                return setTransaction(prev => ({ ...prev, [dataset.type]: 'DEBIT' }))
            }

            if (textContent == 'CRÉDITO') {
                return setTransaction(prev => ({ ...prev, [dataset.type]: 'CREDIT' }))
            }

            return setTransaction(prev => ({ ...prev, [dataset.type]: textContent }));
        }
        setTransaction(prev => ({ ...prev, [dataset.type]: parseInt(dataset.id) }));

    }

    const handleMultiSelectChanges = (e, action) => {
        const { type, id } = e.currentTarget.dataset
        setTransaction(prev => {
            const categories = prev[type] || [];
            const alreadyExists = categories.includes(id);
            if (action == 'add') {
                const updatedValues = alreadyExists ? [...categories] : [...categories, { id: parseInt(id), name: e.target.textContent }]
                return (
                    { ...prev, [type]: updatedValues }
                )

            } else {
                const updatedValues = categories.filter(value => value.id != id)
                return (
                    { ...prev, [type]: updatedValues }
                )
            }
        })

    }

    const translateStatus = (status) => {
        switch (status) {
            case 'PENDING':
                return 'PENDENTE'
            case 'PAID':
                return 'PAGO'
        }
    }

    const translateType = (type) => {
        switch (type) {
            case 'DEBIT':
                return 'DÉBITO'
            case 'CREDIT':
                return 'CRÉDITO'
        }
    }

    const formatData = (data) => {
        if (data == null || data == undefined) return;
        const formatedArr = [];
        data.map(transaction => {
            transaction.installments.map(installment => {
                const novaTransacao = {
                    "id": transaction.id,
                    "doc": transaction.doc,
                    "installmentId": installment.id,
                    "installmentNumber": installment.installmentNumber,
                    "issueDate": transaction.issueDate,
                    "type": translateType(transaction.type),
                    "description": transaction.description,
                    "amount": installment.amount,
                    "dueDate": installment.dueDate,
                    "status": translateStatus(installment.status),
                    "financialAccount": transaction.financialAccount.name,
                    "counterparty": transaction.counterParty.name,
                    "categories": transaction.categories.map(category => category.name).join(", ")
                }

                formatedArr.push(novaTransacao)
            })
        })
        setFormatedData(formatedArr);
    }

    const getTransactions = async () => {
        try {
            const response = await api('/transactions/me');

            if (response.status == 200) {
                setData(response.data)
                formatData(response.data);
                setLoading(false);
            }

        } catch (e) {
            console.log(e)
        }
    }

    const handleSelected = (id, item) => {
        if (selectedId && item.id == selectedId && item.installmentNumber == selectedInstallment) return setSelectedId(0);
        setSelected(item);
        setSelectedId(id);
        setSelectedInstallment(item.installmentNumber);
    }

    const seedTransactionFormForEdit = (selected) => {
        const transaction = data.find(t => t.id == selected.id);
        const mapped = {
            "doc": selected.doc,
            "issueDate": selected.issueDate,
            "type": selected.type,
            "description": selected.description,
            "categories": transaction.categories,
            "installments": [
                {
                    amount: selected.amount,
                    dueDate: selected.dueDate
                }
            ],
            "counterparty": selected.counterparty,
            "financialAccount": selected.financialAccount
        }

        setTransaction(mapped)
    }

    const clearTransactionModal = () => {
        setTransaction(INITIAL_TRANSACTION)
    }

    const getFinAccount = async (t) => {
        try {
            if (typeof t.financialAccount == 'number') {
                const account = await api.get(`/accounts/${t.financialAccount}`)
                if (account.status) return account.data;
            } else {
                const account = await api.get(`/accounts/name/${t.financialAccount}`)
                if (account.status) return account.data;
            }
        } catch (e) {
            console.log(e)
        }
    }

    const getCounterparty = async (t) => {
        try {

            if (typeof t.counterparty == 'number') {
                const account = await api.get(`/counterparties/${t.counterparty}`)
                if (account.status) return account.data;
            } else {
                const account = await api.get(`/counterparties/name/${t.counterparty}`)
                if (account.status) return account.data;
            }
        } catch (e) {
            console.log(e)
        }
    }

    const formatTransactionForUpdateOrCreate = async (t, method) => {
        if (method === 'update') {
            const tCopy = structuredClone(t);
            tCopy.categories = t.categories?.map(cat => cat.id);

            const counterparty = await getCounterparty(t);
            const account = await getFinAccount(t);

            switch (tCopy.type) {
                case 'DÉBITO':
                    tCopy.type = 'DEBIT'
                    break;
                case 'CRÉDITO':
                    tCopy.type = 'CREDIT'
                    break;

            }
            tCopy.counterparty = counterparty.id;
            tCopy.financialAccount = account.id;
            delete tCopy.installments;
            return tCopy;
        }

        if (method === 'create') {
            const tCopy = structuredClone(t);
            tCopy.categories = t.categories?.map(cat => cat.id);

            const counterparty = await getCounterparty(t);
            const account = await getFinAccount(t);

            tCopy.counterparty = counterparty.id;
            tCopy.financialAccount = account.id;
            return tCopy;
        }

    }

    const createTransaction = async (transaction) => {
        if (validateTransactionData(transaction)) {
            const formatted = await formatTransactionForUpdateOrCreate(transaction, 'create');
            try {
                const response = await api.post('/transactions', formatted)
                getTransactions();
                setIsOpen(false);
                addToast("Transaçao criada com sucesso!", "success")
                setActiveInstallmentModal(false);
            } catch (e) {
                console.log(e);
            }
        }
    }


    const updateTransaction = async (id) => {
        if (validateTransactionData(transaction)) {
            const formatedTransaction = await formatTransactionForUpdateOrCreate(transaction, 'update');
            const installment = data.find(transaction => transaction.id == selected.id).installments.find(i => i.id === selected.installmentId);
            installment.amount = transaction.installments[0].amount;
            installment.dueDate = transaction.installments[0].dueDate;
            try {
                const response = await api.put(`/transactions/${id}`, formatedTransaction);
                const response2 = await api.put(`/transactions/${id}/installment/${installment.id}`, installment)
                addToast("Atualizado com sucesso!", "success")
                setIsOpen(false);
                getTransactions();
            } catch (e) {
                console.log(e);
            }
        }
    }

    const deleteTransaction = async (id) => {
        try {
            const response = await api.delete(`/transactions/${id}`);
            getTransactions();
            setOpenDeleteModal(false);
            addToast("Transação deletada com sucesso!", "success");
            setSelectedId(0)
        } catch (e) {
            console.log(e)
            if (e.response.data.message == 'You cannot delete a transaction with paid installments') {
                addToast('Você não pode deletar transações/parcelas pagas.', "error")
                setOpenDeleteModal(false)
            }
        }

    }

    const deleteInstallment = async (transactionId, installmentId) => {
        try {
            const response = await api.delete(`/transactions/${transactionId}/installment/${installmentId}`);

            getTransactions();
            setOpenDeleteModal(false);
            addToast("Parcela deletada com sucesso!", "success");
            setSelectedId(0)
        } catch (e) {
            console.log(e)
            if (e.response.data.message == 'A transaction must have at least one installment') {
                addToast('Esta é uma parcela única. Delete a transação completa!', "error")
                setOpenDeleteModal(false)
            }
            if (e.response.data.message == 'You cannot delete a paid installment') {
                addToast('Você não pode deletar transações/parcelas pagas.', "error")
                setOpenDeleteModal(false)
            }
        }
    }

    const payInstallment = async (installmentId, financialAccount) => {
        try {
            const financialAccountResponse = await api.get(`/accounts/name/${financialAccount}`)
            const confirmation = confirm("Deseja realmente fazer o pagamento?")
            if (confirmation) {
                const response = await api.post(`/payments/installment/${installmentId}`, { financialAccount: financialAccountResponse.data.id })
                if (response) {
                    addToast("Parcela paga com sucesso!", "success")
                    getTransactions();
                }
            }
        } catch (e) {
            console.log(e)
        }
    }


    useEffect(() => {
        setTransactionWithInstallments(transaction);
    }, [transaction])

    useEffect(() => {
        getTransactions();
    }, [])


    useEffect(() => {
        if (!isOpen) clearTransactionModal();
    }, [isOpen])

    useEffect(() => {
        if (activeInstallmentModal == false) {
            setTransactionWithInstallments(transaction);
        }
    }, [activeInstallmentModal])


    return (
        <>
            <div className='transactionsContainer'>
                <Navbar
                    pageName={"Transações"}
                />
                <div className='transactionsMainContainer'>
                    <Header
                        title={"Transações"}
                        newEntity={() => setIsOpen((prev) => !prev)}
                        editEntity={() => { setIsEditing(true); setIsOpen(true); seedTransactionFormForEdit(selected) }}
                        hasSelection={hasSelection}
                        deleteEntity={() => { setOpenDeleteModal(true); setDeleteContext('installment') }}
                        context={'transaction'}
                        deleteTransaction={() => { setOpenDeleteModal(true); setDeleteContext('transaction') }}
                        payTransaction={() => payInstallment(selected.installmentId, selected.financialAccount)}
                    />

                    <div className='tansactionsTableContainer'>
                        {!loading && (
                            <Table
                                columns={columns}
                                data={formatedData}
                                setSelected={handleSelected}
                                selectedId={selectedId}
                                itemType={'transaction'}
                                otherSelectParamether={selectedInstallment}
                            />
                        )}

                    </div>

                </div>
                {isOpen && (
                    <Modal
                        onClose={() => setIsOpen(false)}
                        children={
                            <TransactionForm
                                handleInput={handleInputChanges}
                                handleSelect={handleSelectChanges}
                                handleMultiSelect={handleMultiSelectChanges}
                                transaction={transaction}
                                context={hasSelection ? 'editing' : ''}
                                selected={selected}
                            />}
                        title={hasSelection ? "Editar transação" : "Criar nova transação"}
                        save={hasSelection ? () => updateTransaction(selectedId) : (installmentQtd > 1 ? () => { setActiveInstallmentModal(true), createInstallments() } : () => createTransaction(transaction))}
                    />
                )}
                {activeInstallmentModal && (
                    <InstallmentModal
                        installments={transactionWithInstallments.installments}
                        closeModal={() => setActiveInstallmentModal(false)}
                        handleChanges={handleInstallmentInputChanges}
                        create={() => createTransaction(transactionWithInstallments)}
                    />
                )}
                {openDeleteModal && (
                    <DeleteModal
                        name={deleteContext == 'installment' ? ` a parcela ${selected.installmentNumber} da transação ${selected.doc}` : ` a transação ${selected.doc}`}
                        handleDelete={deleteContext == 'installment' ? () => deleteInstallment(selectedId, selected.installmentId) : () => deleteTransaction(selectedId)}
                        closeOrCancel={() => setOpenDeleteModal(false)}
                    />
                )}
            </div>
        </>

    )
}

export default Transactions;