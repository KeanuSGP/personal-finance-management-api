import { useContext, useState } from 'react'
import { useNavigate } from 'react-router'
import Button from '../../components/Button/Button.jsx'
import Input from '../../components/Input/Input.jsx'
import { api } from '../../services/api'
import './Register.css'
import { toastContext, ToastProvider } from '../../components/ToastNotification/ToastProvider.jsx'
import { Loader } from '../../components/Loader/Loader.jsx'

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false)
    const { addToast } = useContext(toastContext)
    const navigate = useNavigate();

    async function createUser() {
        setLoading(true)
        try {
            const response = await api.post('/auth/register', {
                name: username,
                password: password
            })
            if (response) {
                setLoading(false);
                addToast("Conta criada com sucesso!", 'success');
                setTimeout(() => {navigate('/login')}, 2000)
            }
        } catch (e) {
            setLoading(false);
            console.log(e.response.data);
        }

    }

    const handleSubmit = (e) => {
        if (username.length < 3 || password.length < 3) {
            addToast('os dados de login precisam de ao menos 3 caracteres', "error")
        }
        e.preventDefault();
        createUser();
    }

    return (
        <div className='container'>
            <form className='formStyle' onSubmit={handleSubmit}>
                <h1 className={'msgStyle'}>Crie sua conta</h1>
                <div className='inputDiv'>
                    <Input type='text' placeholder='usuário' onChange={(e) => setUsername(e.target.value)} style={"inputStyle"} />
                </div>
                <div className='inputDiv'>
                    <Input type='password' placeholder='senha' onChange={(e) => setPassword(e.target.value)} style={"inputStyle"} />
                </div>
                <Button value={"Criar conta"} customize={'btnStyle'}></Button>
            </form>
            <div>
                <p>Já tem uma conta? <a href='/login' className={'linkStyle'}>Entrar</a></p>
            </div>
            {loading && (
                <Loader />
            )}
        </div>
    )
}

export default Register