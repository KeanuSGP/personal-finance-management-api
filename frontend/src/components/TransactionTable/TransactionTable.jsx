import { Fragment, useEffect, useState } from 'react'
import styles from './TransactionTable.module.css'
import { IoMdArrowDropright } from "react-icons/io";
import { IoMdArrowDropdown } from "react-icons/io";

const TransactionTable = ({ data, setSelected, selectedId }) => {
    const [isExpanded, setIsExpanded] = useState([]);
    const [isAscending, setIsAscending] = useState(false);
    const [sorted, setSorted] = useState([]);


    const transactionColumns = [
        { header: 'documento', accessor: 'doc' },
        { header: 'emissão', accessor: 'issueDate' },
        { header: 'tipo', accessor: 'type' },
        { header: 'descrição', accessor: 'description' }
    ]

    const installmentColumns = [
        { header: 'parcela', accessor: 'installmentNumber' },
        { header: 'valor', accessor: 'amount', type: 'money' },
        { header: 'vencimento', accessor: 'dueDate' },
        { header: 'situação', accessor: 'status', type: 'status' }
    ]

    const handleActive = (id, transaction) => {
        if (isExpanded.includes(id)) {
            const filtered = isExpanded.filter(savedId => savedId !== id);
            return setIsExpanded(filtered);
        }
        setIsExpanded([...isExpanded, id])
    }

    const sortTable = (t, accessor) => {
        const copy = [...data];
        const index = copy.findIndex(item => item.transaction.id === t.transaction.id);

        if (isAscending) {
            setIsAscending(false);
            copy[index].installments.sort((a, b) => typeof a[accessor] === 'string' ? a[accessor].localeCompare(b[accessor]) : a[accessor] - b[accessor]);
            return setSorted(copy);
        }
        // console.log("antes", copy[index].installments)
        setIsAscending(true);
        copy[index].installments.sort((a, b) => typeof a[accessor] === 'string' ? b[accessor].localeCompare(a[accessor]) : b[accessor] - a[accessor]);
        return setSorted(copy);
        // console.log("depois", copy[index].installments)
        // console.log(data)
        // console.log(data.findIndex(item => item.transaction.id === t.transaction.id));
        // console.log(t);
        // console.log(t.installments);
    }
    return (
        <>
            <div className={styles.tableContainer}>
                <table className={styles.table}>
                    <thead>
                        <tr className={styles.tr}>
                            <th></th>
                            {transactionColumns.map((column, index) => (
                                <th key={index}><span>{column.header.toUpperCase()}</span></th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {(sorted.length ? sorted : data)?.map((item, index) => {
                            const isOpen = isExpanded.includes(item.transaction.id)

                            return (
                                <Fragment key={index}>
                                    <tr onClick={() => handleActive(item.transaction.id, item)} className={styles.transactionRow}>
                                        <td>{isOpen ? <IoMdArrowDropdown /> : <IoMdArrowDropright />}</td>
                                        {
                                            transactionColumns.map((column, index) => (
                                                <td key={index}>
                                                    <span>{item.transaction[column.accessor]}</span>
                                                </td>
                                            ))

                                        }
                                    </tr>
                                    {isOpen && (
                                        <Fragment key={index}>
                                            <tr>
                                                <td colSpan={5}>
                                                    <div className={styles.subTableDiv}>
                                                        <table className={styles.innerTable}>
                                                            <thead>
                                                                <tr >
                                                                    {/* <th></th> */}
                                                                    {installmentColumns.map((column, index) => (
                                                                        <th key={index} onClick={() => sortTable(item, column.accessor)}>
                                                                            {column.header}
                                                                        </th>
                                                                    ))}
                                                                    <th onClick={() => sortTable('financialAccount')}>conta</th>
                                                                    <th onClick={() => sortTable('categories')}>categorias</th>
                                                                    <th onClick={() => sortTable('counterParty')}>contraparte</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                {item.installments?.map((installment, index) => (
                                                                    <tr key={index} onClick={() => setSelected({ transaction: item.transaction.id, installment: installment.id }, { transaction: item, installment: installment })} className={`${selectedId.installment == installment.id ? 'selected' : ''}`}>
                                                                        {/* <td></td> */}
                                                                        {installmentColumns.map((column, index) => (
                                                                            <td key={index}>
                                                                                <span className={column.type == 'status' ? installment[column.accessor] === 'PAID' ? styles['status-paid'] : styles['status-pending'] : ''}>
                                                                                    {column.type == 'money' ?
                                                                                        new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(installment[column.accessor]) :
                                                                                        installment[column.accessor]}
                                                                                </span>

                                                                            </td>
                                                                        ))}
                                                                        <td>{item.financialAccount.name}</td>

                                                                        {item.categories.map((category, index) => (
                                                                            <td key={index}>{category.name}</td>
                                                                        ))}

                                                                        <td>{item.counterParty.name}</td>
                                                                    </tr>
                                                                ))}
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                                {/* {
                                                    installmentColumns.map((column, index) => (
                                                        <td key={index}><span>{installment[column.accessor]}</span></td>
                                                    ))
                                                } */}
                                            </tr>
                                        </Fragment>
                                    )}
                                </Fragment>
                            )
                        })}
                    </tbody>
                </table>
            </div>

        </>
    )
}

export default TransactionTable;