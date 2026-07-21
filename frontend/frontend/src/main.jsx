import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router'
import { AuthProvider } from './contexts/AuthContext.jsx'
import CategoryPage from './pages/Category/CategoryPage.jsx'
import CounterpartyPage from './pages/Counterparty/CounterpartyPage.jsx'
import AccountsPage from './pages/FinancialAccount/FinancialAccountPage.jsx'
import Login from './pages/Login/Login.jsx'
import Register from './pages/Register/Register.jsx'
import PrivateRoutes from './security/PrivateRoutes.jsx'

import './index.css'
import Transactions from './pages/Transactions/Transactions.jsx'
import TransactionForm from './components/Modals/TransactionForm.jsx'
import MultiSelect from './components/MultiSelect/MultiSelect.jsx'
import Select from './components/Select/Select.jsx'
import InstallmentForm from './components/Modals/InstallmentForm.jsx'
import Payments from './pages/Payments/Payments.jsx'
import Dashboard from './pages/Dashboard/Dashboard.jsx'
import { ToastProvider } from './components/ToastNotification/ToastProvider.jsx'
import Navbar from './components/Navbar/Navbar.jsx'

const router = createBrowserRouter([

  {
    path: "/",
    element: <Login />
  },
  {
    path: "/login",
    element: <Login />
  },
  {
    path: "/register",
    element: <Register />

  },
  {
    path: "/dashboard",
    element:
      <PrivateRoutes children={<Dashboard />}>
      </PrivateRoutes>
  },
  {
    path: "/counterparty",
    element:
      <PrivateRoutes children={<CounterpartyPage />}>
      </PrivateRoutes>
  },
  {
    path: "/accounts", element: (
      <PrivateRoutes children={<AccountsPage />}>
      </PrivateRoutes>
    )

  },
  {
    path: "/categories",
    element: (
      <PrivateRoutes children={<CategoryPage />}>
      </PrivateRoutes>
    )
  },
  {
    path: "/Transactions",
    element: <PrivateRoutes><Transactions /></PrivateRoutes>
  },
  {
    path: "/Payments",
    element: <PrivateRoutes><Payments /></PrivateRoutes>
  },
  {
    path: "/nav",
    element: <Navbar pageName={"TESTE"} />
  }

]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ToastProvider>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ToastProvider>
  </StrictMode>,
)
