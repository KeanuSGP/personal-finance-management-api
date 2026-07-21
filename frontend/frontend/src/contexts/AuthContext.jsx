import { createContext, useContext, useEffect, useState } from 'react';
import { api, apiLogout } from '../services/api';
import { useToast } from '../components/ToastNotification/useToast';


const AuthContext = createContext();


function AuthProvider({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [user, setUser] = useState({});
    const { addToast } = useToast();

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem('user'));

        async function validate(user) {
            try {
                console.log(user)
                const response = await api.post('/auth/validate', {}, { headers: { 'Authorization': 'Bearer ' + user.token } })
                if (response) {
                    console.log(response)
                    setIsAuthenticated(true);
                    setUser(user)
                } else {
                    handleLogout()
                }
            } catch (e) {
                console.log(e)
                handleLogout()
            }
        }
        if (user !== null) {
            validate(user)
        } else {
            handleLogout()
        }
    }, [])

    useEffect(() => { apiLogout(handleLogout) }, [])

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
            console.log(e.code)
            if (e.code == 'ERR_NETWORK') {
                addToast("ERRO DE CONEXÃO COM O SERVIDOR", "error")
            }
            setIsAuthenticated(false);
            setLoading(false)
        }
    }

    const handleLogout = () => {
        localStorage.removeItem('user')
        setIsAuthenticated(false)
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, handleSubmit, setUsername, setPassword, handleLogout, user, loading }}>
            {children}
        </AuthContext.Provider>
    )
}

export { AuthContext, AuthProvider }
