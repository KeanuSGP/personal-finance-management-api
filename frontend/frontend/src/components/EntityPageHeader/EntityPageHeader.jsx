import './EntityPageHeader.css'
import Button from '../Button/Button'
import { useState, useEffect } from 'react'
import { IoIosAddCircle } from "react-icons/io";
import { FaEdit } from "react-icons/fa";
import { MdDelete } from "react-icons/md";
import { FaCheckCircle } from "react-icons/fa";

const EntityPageHeader = ({ newEntity, title, editEntity, hasSelection, deleteEntity, context, deleteTransaction, payTransaction }) => {

    const [activeTransactionContext, setActiveTransactionContext] = useState(false);
    const [activePaymentContext, setActivePaymentContext] = useState(false);

    const deletePaymentMsg = 'DELETAR'

    useEffect(() => {
        if (context == 'transaction') return setActiveTransactionContext(true);
        if (String(context).toLowerCase() == 'payments') return setActivePaymentContext(true);
    }, [])

    return (
        <div className="entityPageHeaderComponent">
            <h1 className="entityName">{title}</h1>
            <div className="pageButtons">
                <Button onClick={newEntity} hasSelection={hasSelection} title={"CRIAR"} value={<IoIosAddCircle />} customize={`${!hasSelection ? 'scale' : ''} new ${activePaymentContext ? 'paymentContext' : ''}`}></Button>
                <Button onClick={editEntity} hasSelection={!hasSelection} title={"EDITAR"} value={<FaEdit />} customize={`${hasSelection ? 'scale' : ''} edit ${activePaymentContext ? 'paymentContext' : ''}`}></Button>
                <Button onClick={deleteEntity} value={<MdDelete />} hasSelection={!hasSelection} title={`${activeTransactionContext ? "DELETAR PARCELA" : "DELETAR"}`} customize={`${hasSelection ? 'scale' : ''} delete`} title={activePaymentContext ? deletePaymentMsg : ''}>Delete</Button>
                <div className='transactionAditionalBtns' style={activeTransactionContext ? { display: 'flex' } : { display: 'none' }} >
                    <Button title={"Deletar transação completa"} value={<MdDelete />}  onClick={deleteTransaction} hasSelection={!hasSelection} customize={`${hasSelection ? 'scale' : ''} delete`} />
                    <Button value={<FaCheckCircle />} title="PAGAR" hasSelection={!hasSelection} customize={`${hasSelection ? 'scale' : ''} pay`} onClick={payTransaction}/>
                </div>
            </div>
        </div>
    )
}

export default EntityPageHeader