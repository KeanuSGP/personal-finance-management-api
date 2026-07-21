import { createContext, useEffect, useState } from "react";
import styles from './ToastNotification.module.css';
import Toast from './ToastNotification';

export const toastContext = createContext();

export function ToastProvider ( {children} ) {
    const [toasts, setToasts] = useState([])

    function addToast(message, type = 'info') {
        const id = Date.now() + Math.random();
        setToasts(prev => [{id, message, type }, ...prev]);
        setTimeout(() => removeToast(id), 5000);
    }

    function removeToast(id) {
        setToasts( prev => prev.filter(toast => toast.id !== id) );
    }

    return <toastContext.Provider value={ {addToast} }>
        {children}
        <div className={styles.toastNotificationContainer}>
            {toasts.map(toast => (
                <Toast key={toast.id} {...toast} onClose={() => removeToast(toast.id)}/>
            ))}
        </div>
    </toastContext.Provider>
}