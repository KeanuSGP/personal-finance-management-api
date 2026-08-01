import styles from './DashboardLatests.module.css';
import React from 'react';
import { FaArrowTrendUp, FaArrowTrendDown } from "react-icons/fa6";


const DashboardLatests = ({ data, columns, context }) => {

    console.log(data);

    const [actualContext, setActualContext] = React.useState(context);

    React.useEffect(() => {
        setActualContext(context);
    }, [context]);

    
    const formatMoment = (moment) => {
        const split = moment.split('T');
        const dataSplit = split[0].split('-');
        const formatted = `${dataSplit[2]}/${dataSplit[1]}/${dataSplit[0]} ${split[1].slice(0, -1)}`
        return formatted;
    }

    return (
        <>
            {data?.length >= 1 ? <table className={styles.table}>
                <thead>
                    <tr >
                        {columns.map((column, index) => (
                            <th key={index}>
                                {column.header}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody className={styles.tableBody}>
                    {
                        data?.map((item, index) => (
                            <tr>
                                {columns.map((column, index) => (
                                    <td key={index}>
                                        <span key={index} className={column.type === 'status' ? (actualContext === 'transactions' ? item.transaction : item)[column.accessor] === 'DEBIT' ? styles.debit : styles.credit : ''}>
                                            {
                                                column.type === 'status' ? (actualContext === 'transactions' ? item.transaction :
                                                    item)[column.accessor] === 'DEBIT' ?
                                                    <FaArrowTrendDown /> :
                                                    <FaArrowTrendUp /> :
                                                    column.accessor === 'installments' ? 
                                                    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item[column.accessor][0].amount) :
                                                    column.accessor === 'installmentAmount' ?
                                                    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item[column.accessor]) :
                                                    column.accessor === 'moment' ?
                                                    formatMoment(item[column.accessor]) :
                                                        (actualContext === 'transactions' ? item.transaction : item)[column.accessor]}
                                        </span>
                                    </td>

                                ))}
                            </tr>
                        ))
                    }
                </tbody>

            </table> :
                <div>Sem dados encontrados.</div>}
        </>
    );
}

export default DashboardLatests;