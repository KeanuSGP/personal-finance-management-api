import '../Table/Table.css';
import { useState, useEffect } from 'react'
import { FaSort } from "react-icons/fa";
import { IoSearch } from "react-icons/io5";

const Table = ({ columns, data, selectedId, setSelected, itemType, otherSelectParamether }) => {

    const [search, setSearch] = useState('');
    const [searched, setSearched] = useState([]);
    const [isAscending, setIsAscending] = useState(false);
    const [sorted, setSorted] = useState([]);

    const handleSearch = (e) => {
        setSearch(e.target.value)
    }

    useEffect(() => {
        const arr = [];
        data?.forEach(element => {
            for (const value of Object.values(element)) {
                if (String(value).toLocaleLowerCase().includes(search) && search.length >= 1) {
                    if (!arr.some(item => item.id === element.id)) {
                        arr.push(element)
                    }
                }
            }
        });
        setSearched(arr);
    }, [search])

    const selectCheck = (item) => {
        if (itemType == "transaction") {
            if (selectedId == item.id && otherSelectParamether == item.installmentNumber) return true;
        } else {
            if (selectedId == item.id) return true;
        }
    }

    const formatData = (data) => {
        if (data) {
            const dataArr = data.split('-');
            const formattedData = `${dataArr[2]}/${dataArr[1]}/${dataArr[0]}`
            return formattedData
        }
        return;
    }

    const sortTable = (accessor) => {
        if (isAscending) {
            setIsAscending(false);
            return setSorted([...data].sort((a, b) => typeof a[accessor] === 'string' ? a[accessor].localeCompare(b[accessor]) : a[accessor] - b[accessor]))
        }

        setIsAscending(true);
        return setSorted([...data].sort((a, b) => typeof a[accessor] === 'string' ? b[accessor].localeCompare(a[accessor]) : b[accessor] - a[accessor]))
    }

    return (
        <>
            {data?.length > 0 ? (
                <div className='tableComponent'>
                    <div className='searchBarDiv'>
                        <IoSearch />
                        <input type='text' placeholder='buscar'
                            className='searchBar'
                            onChange={handleSearch}
                        />
                    </div>
                    <div className='tableContainer'>
                        <table className="tableElement">
                            <thead>
                                <tr>
                                    {columns.map((column, index) => (
                                        <th key={index} onClick={() => sortTable(column.accessor)}>
                                            <span className='headerStyle'>{column.header}<FaSort className='sortIcon' /></span>
                                        </th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    (searched.length ? searched : sorted.length ? sorted : data).map((item, index) => (
                                        <tr
                                            key={index}
                                            id={item.id}
                                            onClick={() => setSelected(item.id, item)}

                                            className={`${index % 2 != 0 ? `isOdd` : ""} ${selectCheck(item) ? `selected` : ''}`}
                                        >
                                            {columns.map((column, index) => (
                                                <td key={index} >
                                                    <span className={`${column.type == 'money' ? item.type === 'CRÉDITO' ? 'credit' : item.type === 'DÉBITO' ? 'debit' : '' : ''} ${item[column.accessor] === 'PENDENTE' ? 'pending' : ''} ${item[column.accessor] === 'PAGO' ? 'paid' : ''}`}>
                                                        {column.type === 'money' ?
                                                            new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item[column.accessor])
                                                            : column.type === 'date' ? formatData(item[column.accessor])
                                                                : column.type === 'color' ?
                                                                    <div style={{ display: 'flex', gap: '10px' }}>{item[column.accessor]} | <div style={{ width: "20px", height: "20px", borderRadius: "50%", backgroundColor: `#${item[column.accessor]}`, border: '1px solid gray' }}></div></div>
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
                </div>
            ) : (<p>Nenhum dado encontrado.</p>)}

        </>
    )

}

export default Table;