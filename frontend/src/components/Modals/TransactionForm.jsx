import { use, useEffect, useState, useContext } from 'react'
import Input from '../../components/Input/Input'
import MultiSelect from '../MultiSelect/MultiSelect'
import { api } from '../../services/api'
import Select from '../Select/Select'
import Header from '../EntityPageHeader/EntityPageHeader'
import Modal from '../Modals/Modal'
import '../Modals/TransactionForm.css'
import { toastContext } from '../ToastNotification/ToastProvider'

const TransactionForm = ({ transaction, handleInput, handleSelect, handleMultiSelect, context, selected }) => {
    const token = JSON.parse(localStorage.getItem('auth'));
    const { addtoast } = useContext(toastContext)

    const [categories, setCategories] = useState([]);
    const [accounts, setAccounts] = useState([]);
    const [counterparties, setCounterparties] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingContext, setEditingContext] = useState(false);

    const transactionType = [
        { name: 'DÉBITO' },
        { name: 'CRÉDITO' }
    ]

    const getCategories = async () => {
        try {
            const response = await api.get('/categories/me', {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            })
            setCategories(response.data);
            if (response.data.length > 0) setLoading(false);

        } catch (e) {
            console.log(e)
        }
    }

    const getAccounts = async () => {
        try {
            const response = await api.get('/accounts/me',
                {
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                })
            setAccounts(response.data)
            if (response.data.length) setLoading(false)
        } catch (e) {
            console.log(e)
        }
    }


    const getCounterparties = async () => {
        try {
            const response = await api.get('/counterparties/me',
                {
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                })
            setCounterparties(response.data)
            if (response.data.length) setLoading(false)
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        getCategories();
        getAccounts();
        getCounterparties();
        if (context != undefined) {
            if (context == 'editing') {
                setEditingContext(true);
            }
        }
    }, [])

    return (
        <>
            <div className='transactionFormBody' >
                <div className='transactionFormMain'>
                    <div className='section'>
                        <label>
                            <span className='labelTextSize'>Documento</span>
                            <Input
                                type={"text"}
                                value={transaction.doc}
                                name={"doc"}
                                onChange={(e) => handleInput(e)}
                                required={true}
                                placeholder={"EX: NF00000"}
                            />
                        </label>
                        {editingContext && <label>Parcela:<h4>{selected.installmentNumber}</h4></label>}
                        <label>
                            <span className='labelTextSize'>Emissão</span>
                            <Input
                                type={"date"}
                                value={transaction.issueDate}
                                name={'issueDate'}
                                onChange={(e) => handleInput(e)}
                                required={true}
                            />
                        </label>
                        {!editingContext && (<label>
                            <span className='labelTextSize'>Tipo</span>
                            <Select
                                name={'transaction type'}
                                data={transactionType}
                                handleChange={handleSelect}
                                itemName={"type"}
                                value={transactionType[0].name}
                            />
                        </label>)}
                        
                        <label className='labelTextSize'>Vencimento:
                            <input type={'date'} className='tFormInputStyle' name='dueDate' onChange={(e) => handleInput(e)} value={transaction.installments[0].dueDate} required={true}></input>
                        </label>
                        <label className='labelTextSize'>Valor:
                            <input type={'number'} min={0} className='tFormInputStyle' name='amount' onChange={(e) => {handleInput(e)}} value={transaction.installments[0].amount} required={true} ></input>
                        </label>
                        <label style={{display: `${editingContext ? 'block' : 'none'}`}}>Situação:
                            <div>
                                <h4>
                                   {selected.status}
                                </h4>
                                </div>
                        </label>
                    </div>

                    <div className='section'>
                        <label>
                            <span className='labelTextSize'>Categorias</span>
                            <MultiSelect
                                options={categories}
                                handleSelect={handleMultiSelect}
                                value={transaction.categories}
                            />
                        </label>

                        <label>
                            <span className='labelTextSize'>Conta</span>
                            <Select
                                name={"account"}
                                data={accounts}
                                handleChange={handleSelect}
                                itemName={"financialAccount"}
                                value={transaction.financialAccount}
                            />
                        </label>

                        <label>
                            <span className='labelTextSize'>Contraparte</span>
                            <Select
                                name={"counterparty"}
                                data={counterparties}
                                handleChange={handleSelect}
                                itemName={"counterparty"}
                                value={transaction.counterparty}
                            />
                        </label>
                    </div>
                    {!editingContext && <label className={`${editingContext ? 'editing' : ''}`}>
                        <span className='labelTextSize'>Parcelas</span>
                        <input className='tFormInputStyle' type='number' defaultValue={1} min={1} max={96} name={'installmentQuantity'} onChange={(e) => {e.target.value > 96 ? e.target.value = 96 : e.target.value; handleInput(e); e.target.value < 0 ? (e.target.value = 1, e.target.blur()): e.target.value;}} required={true}/>
                    </label>}
                    <label className='labelTextSize lDisplay'>Descrição
                        <textarea className='descriptionStyle' placeholder='Uma breve descrição' name='description' value={transaction.description} onChange={(e) => handleInput(e)} required={true} />
                    </label>
                </div>
            </div>
        </>

    )

}

export default TransactionForm;