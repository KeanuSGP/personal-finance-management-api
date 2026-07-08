import { useState, useContext, useEffect } from 'react';
import { useNavigate, useLocation } from "react-router-dom";
import './Navbar.css'
import { IoExit } from "react-icons/io5";
import { TbTransactionDollar } from "react-icons/tb";
import { MdAccountBalanceWallet } from "react-icons/md";
import { FaHandHoldingUsd, FaUser } from "react-icons/fa";
import { BiSolidCategory } from "react-icons/bi";
import { GiPayMoney, GiReceiveMoney } from "react-icons/gi";
import { Link } from 'react-router';
import { AuthContext } from '../../contexts/AuthContext';
import { MdDashboard } from "react-icons/md";
import { MdCategory } from "react-icons/md";
import { FaMoneyBillTransfer } from "react-icons/fa6";
import { MdPayments } from "react-icons/md";
import { IoIosCloseCircle } from "react-icons/io";


const Navbar = ({ pageName }) => {
    const navigate = useNavigate();
    const { handleLogout, user } = useContext(AuthContext)
    const page = useLocation().pathname.replace('/', '');
    const [open, setOpen] = useState(false);

    return (
        <>
            <div className={`navContainer ${open ? 'isOpen' : ''}`}>
                <div className='closeMenu' style={open ? {display: 'flex'}:{display: 'none'}}><IoIosCloseCircle className='closeMenuBtn' onClick={() => setOpen(prev => !prev)}/></div>
                <div className="user"><FaUser style={{ fontSize: "20px" }} />{user?.user?.name}</div>

                <nav className="navButtons">
                    <ul>
                        <Link to='/dashboard' className={`link-style ${page === 'dashboard' ? 'selected-page' : ''}`}>
                            <li>
                                <MdDashboard className='iconSize' />
                                Dashboard
                            </li>
                        </Link>
                        <Link to='/accounts' className={`link-style ${page === 'accounts' ? 'selected-page' : ''}`}>
                            <li><MdAccountBalanceWallet className='iconSize' />Contas Financeiras</li>
                        </Link>
                        <Link to='/counterparty' className={`link-style ${page === 'counterparty' ? 'selected-page' : ''}`}>
                            <li><FaHandHoldingUsd className='iconSize' />Contraparte</li>
                        </Link>
                        <Link to='/categories' className={`link-style ${page === 'categories' ? 'selected-page' : ''}`}>
                            <li><MdCategory className='iconSize' />Categorias</li>
                        </Link>
                        <Link to={'/transactions'} className={`link-style ${page === 'transactions' ? 'selected-page' : ''}`} >
                            <li><FaMoneyBillTransfer className='iconSize' />Transações</li>
                        </Link>
                        <Link to='/payments' className={`link-style ${page === 'payments' ? 'selected-page' : ''}`}>
                            <li><MdPayments className='iconSize' />Pagamentos</li>
                        </Link>
                    </ul> 
                </nav>
                <div className="exitButton" onClick={handleLogout}>
                    <div className='exitBtnDiv'>
                        <IoExit className='iconSize' />
                        Sair
                    </div>
                </div>
            </div>
            <div className={`hamburguerMenuDiv ${open ? 'isOpen' : ''}`}>
                <p>MENU</p>
                <div className='hamburguerMenuContainer' onClick={() => setOpen(prev => !prev)}>
                    <div className='part topPart'></div>
                    <div className='part midPart'></div>
                    <div className='part botPart'></div>
                </div>
            </div>
        </>

    )
}

export default Navbar;