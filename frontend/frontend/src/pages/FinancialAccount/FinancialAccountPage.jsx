import { useContext, useEffect, useState } from 'react'
import PageHeader from '../../components/EntityPageHeader/EntityPageHeader'
import DeleteModal from '../../components/Modals/DeleteModal'
import ModalForm from '../../components/Modals/FinancialAccountForm'
import Modal from '../../components/Modals/Modal'
import Navbar from '../../components/Navbar/Navbar'
import Table from '../../components/Table/Table'
import { AuthContext } from '../../contexts/AuthContext'
import { api } from '../../services/api'
import './FinancialAccountPage.css'
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider'


const FinancialAccountPage = () => {
    const { user } = useContext(AuthContext)
    const [openModal, setOpenModal] = useState(false);
    const [openDeleteModal, setOpenDeleteModal] = useState(false);
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const { addToast } = useContext(toastContext)


    const [name, setName] = useState("")
    const [balance, setBalance] = useState("")

    const [selectedId, setSelectedId] = useState(null);
    const hasSelection = selectedId !== null;

    const handleSelectItem = (id, item) => {
        if (selectedId === id) return setSelectedId(null), setName(''), setBalance('');
        setName(item.name)
        setBalance(item.balance)
        return setSelectedId(id);
    }


    const columns = [
        { header: "NOME", accessor: "name" },
        { header: "SALDO", accessor: "balance", type: "money" }
    ]


    const getAccounts = async () => {
        try {
            const response = await api.get('/accounts/me')
            if (response) {
                setData(response.data);
                return setLoading(false);
            }

        } catch (e) {
            console.log(e)
        }

    }

    useEffect(() => {
        getAccounts();
    }, [])

    const verifications = (name, balance) => {
        if (name.trim().length === 0) {
            addToast("O nome deve ser preenchido.", "error")
            return false
        }
        if (name.trim().length < 3) {
            addToast("O nome deve ter ao menos 3 letras.", "error")
            return false
        }
        if (balance.length === 0) {
            addToast("Preencha o saldo.", "error")
            return false;
        }
        if (balance < 0) {
            addToast("O saldo não pode ser negativo", "error")
            return false
        }
        return true;
    }

    const createAccount = async () => {
        const check = verifications(name, balance);
        if (check) {
            try {
                const response = await api.post('/accounts', {
                    name: name,
                    balance: balance
                })
                setOpenModal(false);
                addToast("Conta criada com sucesso!", "success")
                getAccounts();
            } catch (e) {
                console.log(e)
            }
        }
    }

    const editAccount = async () => {
        const check = verifications(name, balance);

        if (selectedId === null) return;

        if (check) {
            try {
                const request = {
                    name: name,
                    balance: balance
                }
                const response = await api.put(`/accounts/${selectedId}`, request
                )
                if (response) {
                    setOpenModal(false);
                    addToast("Conta alterada com sucesso!", "success")
                    getAccounts();
                    handleClose();
                }
            } catch (e) {
                console.log(e)

                if (e.status == 409) {
                    return addToast("Já existe uma conta com esse nome!", "error")
                }
                addToast("Erro ao tentar alterar a conta", "error")

            }
        }

    }

    useEffect(() => {console.log( hasSelection)}, [hasSelection])

    const deleteAccount = async () => {

        if (selectedId === null) return;

        try {
            const response = await api.delete(`/accounts/${selectedId}`)
            if (response) {
                addToast("Conta deletada com sucesso!", "success")
                setOpenDeleteModal(false);
                getAccounts();
                setSelectedId(null)
            }
        } catch (e) {
            console.log(e);
        }

    }

    const handleClose = () => {
        setOpenModal(!openModal);
        setName('');
        setBalance('');
    }


    return (
        <div className='page-body'>
            <Navbar pageName={'accounts'} />
            <div className='data-body'>
                <PageHeader
                    title={"Contas Financeiras"}
                    hasSelection={hasSelection}
                    newEntity={() => setOpenModal(true)}
                    editEntity={() => setOpenModal(true)}
                    deleteEntity={() => setOpenDeleteModal(true)}
                />
                <div className='tableDiv'>
                    <Table
                        columns={columns}
                        data={data}
                        selectedId={selectedId}
                        setSelected={handleSelectItem}
                    />
                </div>

            </div>
            {openModal && (
                <Modal
                    title={hasSelection ? 'Editar conta' : 'Criar nova conta'}
                    onClose={handleClose}
                    children={
                        <ModalForm
                            name={name}
                            setName={setName}
                            balance={balance}
                            setBalance={setBalance}
                        />
                    }
                    save={hasSelection ? editAccount : createAccount}
                />
            )}
            {
                openDeleteModal && (
                    <DeleteModal
                        name={name}
                        handleDelete={deleteAccount}
                        closeOrCancel={setOpenDeleteModal}
                    />
                )
            }
        </div>
    )
}

export default FinancialAccountPage