import { useContext, useEffect, useState } from 'react'
import EntityPageHeader from '../../components/EntityPageHeader/EntityPageHeader.jsx'
import CounterpartyForm from '../../components/Modals/CounterpartyForm.jsx'
import DeleteModal from '../../components/Modals/DeleteModal.jsx'
import Modal from '../../components/Modals/Modal.jsx'
import NavBar from '../../components/Navbar/Navbar.jsx'
import Table from '../../components/Table/Table.jsx'
import { AuthContext } from '../../contexts/AuthContext.jsx'
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider'
import { api } from '../../services/api.js'
import './CounterpartyPage.css'

const CounterpartyPage = () => {
    const [openModal, setOpenModal] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [selected, setSelected] = useState(null);
    const hasSelection = selectedId !== null;
    const { authenticated } = useContext(AuthContext);
    const [name, setName] = useState("");
    const [taxId, setTaxId] = useState("");
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDeleteModal, setOpenDeleteModal] = useState(false);
    const { addToast } = useContext(toastContext)


    const columns = [
        { header: "NOME", accessor: "name" },
        { header: "CPF/CNPJ", accessor: "taxId" }
    ];

    const handleSelectedId = (id, item) => {
        if (selectedId === id) return setSelectedId(null);
        setName(item.name);
        setTaxId(item.taxId);
        setSelected(item);
        return setSelectedId(id);
    }

    const getCounterparties = async () => {
        try {
            const response = await api.get('/counterparties/me')
            if (response) {
                setData(response.data);
                return setLoading(false);
            }
        } catch (e) {
            console.log(e);
        }
    }

    useEffect(() => {
        getCounterparties();
    }, [])

    useEffect(() => {
        if (!hasSelection) {
            setName("");
            setTaxId("");
        } else {
            setName(selected.name)
            setTaxId(selected.taxId)
        }
    }, [openModal])

    const validations = (name, taxId) => {
        if (name.length <= 0) {
            addToast("Preencha o nome!", 'error')
            return false;
        } else if (name.length < 3) { return addToast("O nome deve ter ao menos 3 letras!", 'error') }
        if (taxId.length !== 11 && taxId.length !== 14) {
            addToast("A identificação deve ter 11 ou 14 numeros", 'error')
            return false;
        }
        return true;
    }

    const createCounterparty = async () => {
        const check = validations(name.trim(), taxId);
        if (check) {
            try {
                const response = await api.post('/counterparties', {
                    name: name.trim(),
                    taxId: taxId
                })
                setOpenModal(false);
                addToast("Contraparte criada com sucesso!", "success")
                getCounterparties();
            } catch (e) {
                console.log(e)
            }
        }
    }

    const editCounterparty = async (id) => {
        const check = validations(name.trim(), taxId);
        if (check) {
            try {
                const response = await api.put(`/counterparties/${id}`, {
                    name: name,
                    taxId: taxId
                })
                getCounterparties();
                addToast("Editado com sucesso!", "success")
                setOpenModal(false)
            } catch (e) {
                console.log(e)
                if (e.status === 409) {
                    addToast("A identificação pertence à outra contraparte", "error")
                }
            }
        }
    }

    const deleteCounterparty = async (id) => {
        try {
            const response = await api.delete(`/counterparties/${id}`)
            setOpenDeleteModal(false);
            addToast("Contraparte deletada com sucesso!", "success")
            getCounterparties();
            setSelectedId(null);
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <>
            <div className='pageContainer'>
                <NavBar pageName={'counterparty'} />
                <div className='mainContainer'>
                    <EntityPageHeader
                        title={"Contraparte"}
                        newEntity={() => setOpenModal(true)}
                        editEntity={() => setOpenModal(true)}
                        hasSelection={hasSelection}
                        deleteEntity={() => setOpenDeleteModal(true)}
                    />
                    <div className='tableDiv'>
                    {!loading && (
                        <Table
                            columns={columns}
                            data={data}
                            selectedId={selectedId}
                            setSelected={handleSelectedId}
                        />
                    )}
                    </div>
                </div>
                {openDeleteModal && (
                    <DeleteModal
                        name={name}
                        handleDelete={() => deleteCounterparty(selectedId)}
                        closeOrCancel={() => setOpenDeleteModal(false)}
                    />
                )}
                {openModal && (
                    <Modal
                        title={hasSelection ? "Editar contraparte" : "Criar nova contraparte"}
                        onClose={() => setOpenModal(false)}
                        children=
                        {<CounterpartyForm
                            name={hasSelection ? name : name}
                            setName={setName}
                            taxId={taxId}
                            setTaxId={setTaxId}
                            onClose={openModal}
                        />}
                        save={hasSelection ? () => editCounterparty(selectedId) : createCounterparty}
                    />
                )}
            </div>
        </>
    )
}

export default CounterpartyPage