import Input from '../Input/Input'
import { useState } from 'react'
import Button from '../Button/Button'
import './Modal.css'
import { IoIosCloseCircle } from "react-icons/io";

const Modal = ({ onClose, children, title, save }) => {

    return (
        <div className='modalContainer'>
            <div className="modalComponent">
                <div className='modalHeader'>
                    {title}
                    <button className='closeIcon' onClick={onClose}>
                        <IoIosCloseCircle  />
                    </button>
                </div>
                <form className='modalForm'>
                    {children}
                </form>

                <div className='modalBtnDiv'>
                    <Button value={"Cancel"}></Button>
                    <Button onClick={save} value={"Save"}></Button>
                </div>
            </div>
        </div>
    )

}

export default Modal