import { useContext, useEffect } from "react";
import { AuthContext } from "../contexts/AuthContext";
import { Navigate, useLocation } from "react-router-dom"
import Login from '../pages/Login/Login'

const PrivateRoutes = ({ children }) => {
    const { isAuthenticated, user, loading } = useContext(AuthContext);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
        return <Navigate to={"/login"} />
    } else {
        return children;
    }

}



export default PrivateRoutes;