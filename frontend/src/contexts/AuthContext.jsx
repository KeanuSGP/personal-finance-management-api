import { createContext, useContext, useEffect, useState } from 'react';
import { useToast } from '../components/ToastNotification/useToast';
import { api, injectLogout } from '../services/api';

const AuthContext = createContext();

function AuthProvider({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState({});
    const { addToast } = useToast();

    useEffect(() => {injectLogout(logout)}, [])

    useEffect(() => {
        
        const user = JSON.parse(localStorage.getItem('user'));

        async function validate(user) {
            try {
                console.log(user)
                const response = await api.post('/auth/validate', {}, { headers: { 'Authorization': 'Bearer ' + user.token } })
                if (response) {
                    setIsAuthenticated(true);
                    setUser(user)
                } else {
                    logout()
                }
            } catch (e) {
                console.log(e)
                logout()
            }
        }
        if (user !== null) {
            validate(user)
        } else {
            logout()
        }
        setLoading(false);
    }, [])

    const validation = (name, password) => {
        if (name.length === 0) {
            addToast("Preencha o nome de usuário", "error")
            return false;
        }
        if (password.length === 0) {
            addToast("Preencha a senha", "error")
            return false;
        }

        return true;
    }

    function handleSubmit(e) {
        e.preventDefault();
        let user = {
            name: username,
            password: password
        }

        const check = validation(username, password);
        if (check) {
            login(user);
        }

    }

    const login = async (user) => {
        setLoading(true)
        try {
            const response = await api.post('/auth/login', user)
            localStorage.setItem('user', JSON.stringify(response.data));
            setUser(response.data);
            setIsAuthenticated(true);
            setLoading(false)
        } catch (e) {
            console.log(e)
            if (e.code == 'ERR_NETWORK') {
                addToast("ERRO DE CONEXÃO COM O SERVIDOR", "error")
            }
            setIsAuthenticated(false);
            setLoading(false)
        }
    }

    const logout = () => {
        localStorage.removeItem('user')
        setIsAuthenticated(false)
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, handleSubmit, setUsername, setPassword, logout, user, loading }}>
            {children}
        </AuthContext.Provider>
    )
}

export { AuthContext, AuthProvider }
