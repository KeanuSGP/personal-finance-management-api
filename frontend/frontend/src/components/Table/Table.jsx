import '../Table/Table.css';

const Table = ({ columns, data, selectedId, setSelected, itemType, otherSelectParamether }) => {

    const selectCheck = (item) => {
        if (itemType == "transaction") {
            if (selectedId == item.id && otherSelectParamether == item.installmentNumber) return true;
        } else {
            if (selectedId == item.id) return true;
        }
    }

    const formatData = (data) => {
        const dataArr = data.split('-');
        const formattedData = `${dataArr[2]}/${dataArr[1]}/${dataArr[0]}`
        return formattedData
    }

    return (
        <>
            {data !== undefined ? data?.length > 0 ? (
                <div className='tableContainer'>
                    <table className="tableComponentStyle">
                        <thead>
                            <tr>
                                {columns.map((column, index) => (
                                    <th key={index}>{column.header}</th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {
                                data.map((item, index) => (
                                    <tr
                                        key={index}
                                        id={item.id}
                                        onClick={() => setSelected(item.id, item)}

                                        className={`${index % 2 != 0 ? `isOdd` : ""} ${selectCheck(item) ? `selected` : ''}`}
                                    >
                                        {columns.map((column, index) => (
                                            <td key={index} >
                                                <span className={`${column.type == 'money' ? item.type === 'CRÉDITO' ? 'credit': item.type === 'DÉBITO' ? 'debit' : '' : ''} ${item[column.accessor] === 'PENDENTE' ? 'pending' : ''} ${item[column.accessor] === 'PAGO' ? 'paid' : ''}`}>
                                                {column.type === 'money' ?
                                                    `R$ ${Number(item[column.accessor]).toFixed(2)}`
                                                    : column.type === 'date' ? formatData(item[column.accessor]) 
                                                    : column.type === 'color' ?
                                                    <div style={{display: 'flex', gap: '10px'}}>{item[column.accessor]} | <div style={{width: "20px", height: "20px", borderRadius:"50%", backgroundColor:`#${item[column.accessor]}`, border:'1px solid gray'}}></div></div>
                                                    : 
                                                    item[column.accessor]}
                                                    </span>
                                            </td>
                                        ))}
                                    </tr>
                                ))

                            }

                        </tbody>

                    </table>
                </div>
            ) : (<p>Nenhum dado encontrado.</p>) : (<p>Nenhum dado encontrado.</p>)}

        </>
    )

}

export default Table;