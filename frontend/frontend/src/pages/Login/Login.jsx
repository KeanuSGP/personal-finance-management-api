import styles from './Login.module.css'

import { useContext, useEffect, useState } from 'react'
import { useNavigate } from "react-router-dom"
import Button from '../../components/Button/Button.jsx'
import Input from '../../components/Input/Input.jsx'
import { AuthContext } from '../../contexts/AuthContext.jsx'
import { Loader } from '../../components/Loader/Loader.jsx'


const Login = () => {
    const { isAuthenticated, handleSubmit, setUsername, setPassword, loading } = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (isAuthenticated) {
            navigate("/dashboard")
            setPassword('')
            setUsername('')
        }
    }, [isAuthenticated])


    return (
        <div className={styles.loginContainer}>
            <form className={styles.formStyle} onSubmit={(e) => handleSubmit(e)}>
                <h1 className={styles.msgStyle}>Acesse sua conta</h1>
                <div className={styles.inputDiv}>
                    <label>Usuário</label>
                    <Input type="text" placeholder="Digite seu nome de usuário" onChange={(e) => setUsername(e.target.value)} style = {styles.inputStyle} />
                </div>
                <div className='inputDiv'>
                    <label>Senha</label>
                    <Input type='password' placeholder='Digite sua senha' onChange={(e) => setPassword(e.target.value)} style = {styles.inputStyle}/>
                </div>
                <Button value={"Entrar"} customize={styles.btnStyle}></Button>
            </form>
            <div>
                <p>Não tem conta? <a href='/register' className={styles.linkStyle}>Criar conta</a></p>
            </div>
            {loading && (
                <Loader />
            )}
        </div>
    )
}

export default Login