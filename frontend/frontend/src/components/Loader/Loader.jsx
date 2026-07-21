import { useEffect, useState } from "react"
import styles from './Loader.module.css'

export const Loader = () => {


    return (
        <>
            <div className={styles.container}>
                <img src='loading.svg' alt="EXEMPLO" className={styles.loading} />
            </div>
        </>
    )
}