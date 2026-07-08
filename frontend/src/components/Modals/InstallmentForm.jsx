import { useEffect, useState } from 'react'
import Button from '../Button/Button'
import '../Modals/InstallmentForm.css'
import { IoMdCloseCircle } from "react-icons/io";

const Item = ({ handleInstallments, dataId, amount, dueDate, id, itemNumber }) => {
    const [initialAmount, setInitialAmount] = useState(amount);
    const [initialDueDate, setInitialDueDate] = useState(dueDate);

    return (
        <>
            <div className="installmentItemBody" onChange={(e) => handleInstallments(e)} data-id={dataId} key={id}>
                <h1>{itemNumber}</h1>
                <label>
                    Valor:
                    <input
                        type={'number'}
                        min='0'
                        className='installmentInputStyle'
                        name='amount'
                        value={initialAmount}
                        onChange={e => {e.target.value < 0 ? (setInitialAmount(0), e.target.blur()) : setInitialAmount(e.target.value)}}
                    />
                </label>
                <label>
                    Vencimento:
                    <input
                        type={'date'}
                        className='installmentInputStyle'
                        name='dueDate'
                        value={initialDueDate}
                        onChange={e => setInitialDueDate(e.target.value)}
                    />
                </label>
            </div>
        </>
    )
}

const InstallmentForm = ({ installments, create, closeModal, handleChanges }) => {

    return (
        <>
            {installments?.length ? (
                <div className='installmentOverlay'>
                    <div className='installmentsBody'>
                        <div className='installmentModalDiv'>
                            <h3>Defina o valor e o vencimento da parcela:</h3>
                            <IoMdCloseCircle onClick={closeModal} style={{ fontSize: '1.6em', cursor: 'pointer' }} />
                        </div>
                        {
                            //  
                            installments?.map((installment, index) => (
                                <Item dataId={index} id={index} key={index} handleInstallments={handleChanges} 
                                    amount={installment.amount}
                                    dueDate={installment.dueDate}
                                    itemNumber={index + 1}
                                />
                            ))
                        }
                        <div className='installmentButtons'>
                            <Button
                                value={'Cancel'}
                                onClick={closeModal}
                            />
                            <Button value={'Save'} onClick={create} />
                        </div>
                    </div>
                </div>
            ) : (
                <h1>Coisou</h1>
            )}

        </>
    )
}

export default InstallmentForm;