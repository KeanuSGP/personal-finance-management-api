import Button from '../Button/Button'
import { IoIosCloseCircle } from "react-icons/io";
import './DeleteModal.css'


const DeleteModal = ({ name, handleDelete, closeOrCancel }) => {
    return (
        <>
        <div className='deleteModalContainerOverlay'>
            <div className='deleteModalContainer'>
                <div className='deleteModalCloseBtnContainer'>
                    <button className='deleteModalCloseButton'
                     onClick={() => closeOrCancel(false)}
                    ><IoIosCloseCircle /></button>
                </div>
                <div>
                    <p>Tem certeza que deseja deletar {name}?</p>
                </div>
                <div className='deleteModalButtons'>
                    <Button value='Cancel' onClick={() => closeOrCancel(false)}></Button>
                    <Button value='Delete' onClick={handleDelete}></Button>
                </div>
            </div>
        </div>
        </>
    )
}

export default DeleteModal;