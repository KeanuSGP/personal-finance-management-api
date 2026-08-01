import styles from '../Payments/Payments.module.css'
import Navbar from '../../components/Navbar/Navbar'
import Header from '../../components/EntityPageHeader/EntityPageHeader'
import Table from '../../components/Table/Table'
import { api } from '../../services/api'
import { useEffect, useState, useContext } from 'react'
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider'


const Payments = () => {

    const [data, setData] = useState([]);
    const [selected, setSelected] = useState(null)
    const [selectedId, setSelectedId] = useState(null)
    const hasSelection = selected != null ? true : false
    const [loading, setLoading] = useState(true);
    const { addToast } = useContext(toastContext)

    const columns = [
        { header: "PAGO EM", accessor: "moment" },
        { header: "CONTA FINANCEIRA", accessor: "finAcc" },
        { header: "TRANSAÇÃO", accessor: "transactionDoc" },
        { header: "PARCELA", accessor: "installmentNumber" },
        { header: "VALOR", accessor: "installmentAmount", type: 'money' }
    ]

    const handleSelection = (id, item) => {
        if (selectedId != null || selected != null) return setSelected(null), setSelectedId(null)
        setSelectedId(id)
        setSelected(item)
    }

    const formatMoment = (moment) => {
        const split = moment.split('T');
        const dataSplit = split[0].split('-');
        const formatted = `${dataSplit[2]}/${dataSplit[1]}/${dataSplit[0]} ${split[1].slice(0, -1)}`
        return formatted;
    }

    const getPayments = async () => {
        const dataWithFormattedMoment = [];
        try {
            const response = await api.get('/payments/me')
            if (response) {
                response.data.forEach(payment => {
                    const formattedPayment = {
                        ...payment,
                        moment: formatMoment(payment.moment)
                    };
                    dataWithFormattedMoment.push(formattedPayment);
                });
                setData(dataWithFormattedMoment);
                setLoading(false)
            }
        } catch (e) {
            console.log(e)
        }
    }

    const deletePayment = async (id) => {
        try {
            const response = await api.delete(`/payments/${id}`)
            if (response) {
                addToast("Pagamento deletado com sucesso!", "success")
                getPayments();
                setSelected(null)
                setSelectedId(null)
            }
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => { getPayments()}, [])

    return (
        <>
            <div className={styles.container}>
                <Navbar
                    pageName={'Payments'}
                />
                <div className={styles.mainContent}>
                    <Header
                        title={'Pagamentos'}
                        context={'Payments'}
                        hasSelection={hasSelection}
                        deleteEntity={() => deletePayment(selectedId)}
                    />
                    <div className={styles.tableDiv}>
                        {!loading && (
                            <Table
                                columns={columns}
                                data={!loading ? data : loading}
                                setSelected={(id, item) => handleSelection(id, item)}
                                selectedId={selectedId}
                            />
                        )}
                    </div>
                </div>
            </div>

        </>
    )
}

export default Payments;