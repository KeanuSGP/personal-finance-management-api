import axios from 'axios'
import { useNavigate } from 'react-router';
import { AuthContext } from '../contexts/AuthContext';
import { useContext } from 'react';

export const api = axios.create({
    baseURL: 'http://localhost:8080'
})

let logoutFn = null;

export function injectLogout(logoutFunction) {
    logoutFn = logoutFunction;
}

api.interceptors.request.use(
    config => {
        if (config.url !== '/auth/login' && config.url !== '/auth/register' && config.url !== '/auth/validate') {
            if (localStorage.getItem("user") === null) return localStorage.removeItem("user");
            
            const token = JSON.parse(localStorage.getItem("user")).token

            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        }
        return config;
    }
)

api.interceptors.response.use(
    response => {return response},
    error => {
        if (error.status === 401 || !error.response) {
            logoutFn();
        }
        return Promise.reject(error)
    }
)
