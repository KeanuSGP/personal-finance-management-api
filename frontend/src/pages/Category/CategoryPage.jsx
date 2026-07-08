import { useEffect, useState, useContext } from 'react'
import PageHeader from '../../components/EntityPageHeader/EntityPageHeader'
import CategoryForm from '../../components/Modals/CategoryForm'
import DeleteModal from '../../components/Modals/DeleteModal'
import Modal from '../../components/Modals/Modal'
import Navbar from '../../components/Navbar/Navbar'
import Table from '../../components/Table/Table'
import { api } from '../../services/api'
import './CategoryPage.css'
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider'

const CategoryPage = () => {
    const [openModal, setOpenModal] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [selected, setSelected] = useState(null);
    const hasSelection = selectedId !== null;
    const { addToast } = useContext(toastContext)

    const [name, setName] = useState("")
    const [color, setColor] = useState("")

    const token = JSON.parse(localStorage.getItem('auth'));
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDeleteModal, setOpenDeleteModal] = useState(false);

    const columns = [
        { header: "NOME", accessor: "name" },
        { header: "COR", accessor: "color", type: "color" }
    ]

    const validations = (name) => {
        if (name.length === 0) {
            addToast("Preencha o nome!", "error")
            return false
        }
        if (name.length < 3) {
            addToast("O nome deve ter ao menos 3 letras", "error")
            return false;
        }
        return true;
    }

    const getCategories = async () => {
        try {
            const response = await api.get('/categories/me', {
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
            if (response) {
                setData(response.data);
                return setLoading(false);
            }
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        getCategories();
    }, [])

    useEffect(() => {
        if (hasSelection) {
            setName(selected.name)
            setColor(`#${selected.color}`)
            return;
        }
        setName('');
        setColor('');
    }, [openModal])

    const createCategory = async () => {
        const check = validations(name);
        console.log(name, color.slice(1));
        if (check) {
            try {
                const response = await api.post('/categories', {
                    name: name.trim(),
                    // coloquei o slice para remover o #
                    color: color.slice(1)
                })
                setOpenModal(false);
                addToast("Categoria criada com sucesso!", "success")
                getCategories();
            } catch (e) {
                console.log(e)
            }
        }

    }

    const handleSelected = (id, item) => {
        if (selectedId === id) return setSelectedId(null);
        setName(item.name);
        setColor(item.color);
        setSelected(item);
        return setSelectedId(id);
    }

    const editCategory = async (id) => {
        const check = validations(name.trim());
        const item = {
            name: name.trim(),
            color: color.slice(1)
        }
        if (check) {
            try {
                const response = await api.put(`/categories/${id}`, item)
                setOpenModal(false)
                addToast('Editado com sucesso!', 'success')
                setSelectedId(null)
                getCategories();
            } catch (e) {
                console.log(e)
                switch (e.status) {
                    case 409:
                        addToast("Nome ou cor em uso, verifique!", "error");
                        break;
                }
            }
        }

    }

    const deleteCategory = async (id) => {
        try {
            const response = await api.delete(`/categories/${id}`)
            setOpenDeleteModal(false)
            addToast("Categoria removida com sucesso!", "success")
            getCategories();
            setSelectedId(null);
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <>
            <div className='page-container'>
                <Navbar pageName={'categories'} />
                <div className='main-container'>
                    <PageHeader
                        title={"Categorias"}
                        newEntity={() => setOpenModal(!openModal)}
                        editEntity={() => setOpenModal(!openModal)}
                        deleteEntity={() => setOpenDeleteModal(!openDeleteModal)}
                        hasSelection={hasSelection}
                    />
                    <div className='categoryTableContainer'>
                        {!loading && (
                            <Table
                                columns={columns}
                                data={data}
                                selectedId={selectedId}
                                setSelected={handleSelected}
                            />
                        )}

                    </div>

                </div>
                {openDeleteModal && (
                    <DeleteModal
                        name={name}
                        handleDelete={() => deleteCategory(selectedId)}
                        closeOrCancel={() => setOpenDeleteModal(!openDeleteModal)}
                    />
                )}
                {openModal && (
                    <Modal
                        title={hasSelection ? "Editar categoria" : "Criar nova categoria"}
                        onClose={() => setOpenModal(false)}
                        save={hasSelection ? () => editCategory(selectedId) : createCategory}
                        children={
                            <CategoryForm
                                name={name}
                                setName={setName}
                                color={color}
                                setColor={setColor}
                            />
                        }
                    />
                )}
            </div>
        </>
    )
}



export default CategoryPage