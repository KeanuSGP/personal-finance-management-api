import { useEffect, useState } from 'react';
import styles from './ToastNotification.module.css'
import { MdError } from "react-icons/md";
import { FaCheckCircle } from "react-icons/fa";

const ToastNotification = ({ message, type, onClose }) => {

    const toastHeader = type.trim() === 'error' ? 'Erro' : 'Sucesso'

    return (
            <div className={`${styles.toastNotification} ${styles[type]}`}>
                <div className={styles.headerContainer}>
                    <p className={styles.header}>
                        <MdError className={`${styles.errorIcon}`} style={type.trim() === 'error' ? { display: "block" } : { display: "none" }} />
                        <FaCheckCircle className={`${styles.sucessIcon}`} style={type.trim() === 'success' ? { display: "block" } : { display: "none" }} />
                        {toastHeader}
                    </p>
                    <button className={`${styles.closeToast} ${styles[type]}`} onClick={onClose}>X</button>
                </div>

                <div className={styles.mainContent}>
                    <p className={styles.message}>
                        {message}
                    </p>
                </div>
            </div>
    )
}

export default ToastNotification;