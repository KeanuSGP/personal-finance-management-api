import styles from '../Dashboard/Dashboard.module.css'

import Navbar from "../../components/Navbar/Navbar";

import Table from "../../components/Table/Table"
import DashboardLatests from "../../components/DashboardLatests/DashboardLatests";
import { api } from '../../services/api'
import { useState, useEffect } from 'react';
import { FaEyeSlash } from "react-icons/fa";
import { FaEye } from "react-icons/fa";
import { Chart as ChartJS } from 'chart.js/auto';
import { Doughnut, Bar } from 'react-chartjs-2';


const Dashboard = () => {

    const [data, setData] = useState(null)
    const [balance, setBalance] = useState(null)
    const [revenue, setRevenue] = useState(null)
    const [expense, setExpense] = useState(null)
    const [latest, setLatest] = useState(null)
    const [latestTransactions, setLatestTransactions] = useState(null)
    const [top5RevenueCategories, setTop5RevenueCategories] = useState(null)
    const [top5ExpenseCategories, setTop5ExpenseCategories] = useState(null)
    const [last6Months, setLast6Months] = useState(null)
    const [loading, setLoading] = useState(true)
    const [vision, setVision] = useState(false);
    const [revenueVision, setRevenueVision] = useState(false);
    const [expenseVision, setExpenseVision] = useState(false);
    const [showAll, setShowAll] = useState(false);
    const [lastMonthData, setLastMonthData] = useState(null);

    const handleShowAll = () => {
        setShowAll(!showAll)
    }

    useEffect(() => {
        setVision(showAll);
        setExpenseVision(showAll)
        setRevenueVision(showAll);
    }, [showAll])

    const paymentColumns = [
        { header: "PAGO EM", accessor: "moment" },
        { header: "CONTA FINANCEIRA", accessor: "finAcc" },
        { header: "TRANSAÇÃO", accessor: "transactionDoc" },
        { header: "PARCELA", accessor: "installmentNumber" },
        { header: "VALOR", accessor: "installmentAmount" }
    ]

    const transactionColumns = [
        { header : "TIPO", accessor: 'type', type: 'status' },
        { header: "DOCUMENTO", accessor: 'doc' },
        { header: "DATA DE EMISSÃO" , accessor: 'issueDate' },
        { header: "DESCRIÇÃO", accessor: 'description' },
        { header: "VALOR", accessor: 'installments', type: 'money' }
    ]

    const handleVision = () => {
        if (vision) return setVision(false)
        setVision(true)
    }

    const getDashboard = async () => {
        try {
            const response = await api.get('/dashboard')
            if (response) {
                setData(response.data)
                setLoading(false)
            }
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        getDashboard();
    }, [])

    useEffect(() => {
        if (!loading) {
            setBalance(data.totalBalance)
            setRevenue(data.monthlyRevenue)
            setExpense(data.monthlyExpense)
            setLatest(data.latestPayments)
            setLatestTransactions(data.latestTransactions)
            setTop5ExpenseCategories(data.top5ExpenseCategories)
            setTop5RevenueCategories(data.top5RevenueCategories)
            setLast6Months(data.last6Months)
            setLastMonthData(data.last6Months[data.last6Months.length - 2])
        }
    }, [loading])

    const calculateMensalDifference = (actualValue, lastMonthValue) => {
        if (lastMonthValue === undefined) return 0;
        const lastMonthIsZero = lastMonthValue < 1 ? true : false;
        const calc = lastMonthIsZero ? actualValue:
            ((actualValue - lastMonthValue) / lastMonthValue) / 100

        const calcIsPositive = calc > 0 ? true : false;

        const calcResultFormat = calcIsPositive && lastMonthIsZero ? `+${Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(calc)}` : 
        `+${((actualValue - lastMonthValue) / lastMonthValue) * 100}%`


        return {result: calcResultFormat, isPositive: calcIsPositive };
    }

    const translateMonth = (month) => {
        if (month == undefined || month == null) return;
        switch (month.toLowerCase()) {
            case 'january':
                return 'Janeiro'
            case 'february':
                return 'Fevereiro'
            case 'march':
                return 'Março'
            case 'april':
                return 'Abril'
            case 'may':
                return 'Maio'
            case 'june':
                return 'Junho'
            case 'july':
                return 'Julho'
            case 'august':
                return 'Agosto'
            case 'september':
                return 'Setembro'
            case 'november':
                return 'Novembro'
            case 'december':
                return 'Dezembro'
        }
    }


    useEffect(() => { console.log(revenueVision) }, [revenueVision])
    return (
        <>
            <div className={styles.container}>
                <Navbar />
                <div className={styles.main}>
                    <div className={styles.titleDiv}>
                        <h1 className={styles.title}>Dashboard</h1>
                        <button className={styles.showAll} onClick={handleShowAll}>Mostrar todos</button>
                    </div>
                    <div className={styles.content}>
                        <div className={styles.values}>
                            <div className={styles.balanceContainer}>
                                <div className={styles.titleAndVision}>
                                    <h3 className={styles.balanceTitle}>Saldo</h3>
                                    {vision ? (<FaEyeSlash className={styles.iconStyle} onClick={() => handleVision()} />) : (
                                        <FaEye className={styles.iconStyle} onClick={() => handleVision()} />
                                    )}

                                </div>
                                <h1 className={styles.balance}>{
                                    loading ? "R$ -----" : balance !== undefined && balance !== null ? (vision ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(balance) : "R$ -----") : "R$ -----"
                                }
                                </h1>

                            </div>
                            <div className={styles.revenueContainer}>
                                <div className={styles.titleAndVision}>
                                    <h3 className={styles.revenueTitle}>Receita do mês</h3>
                                    {revenueVision ? (<FaEyeSlash className={styles.iconStyle} onClick={() => setRevenueVision(prev => !prev)} />) : (
                                        <FaEye className={styles.iconStyle} onClick={() => setRevenueVision(prev => !prev)} />
                                    )}
                                </div>
                                <div className={styles.valueDiv}>
                                    <h1 className={styles.revenue}>{
                                        loading ? "R$ -----" : revenue !== undefined && revenue !== null ? (revenueVision ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(revenue) : "R$ -----") : "R$ -----"
                                    }</h1>
                                    {revenueVision && (
                                        <p className={ calculateMensalDifference(revenue, lastMonthData?.revenue).isPositive ? styles['positive'] : styles['negative']}>{
                                            calculateMensalDifference(revenue, lastMonthData?.revenue).result == 0 ? '' : calculateMensalDifference(revenue, lastMonthData?.revenue).result
                                            }</p>

                                    )}
                                </div>

                            </div>
                            <div className={styles.expenseContainer}>
                                <div className={styles.titleAndVision}>
                                    <h3 className={styles.expenseTitle}>Despesa do mês</h3>
                                    {expenseVision ? (<FaEyeSlash className={styles.iconStyle} onClick={() => setExpenseVision(prev => !prev)} />) : (
                                        <FaEye className={styles.iconStyle} onClick={() => setExpenseVision(prev => !prev)} />
                                    )}
                                </div>
                                <div className={styles.valueDiv}>
                                    <h1 className={styles.expense}>{
                                        loading ? "R$ -----" : expense !== undefined && expense !== null ? expenseVision ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(expense) : "R$ -----" : "R$ -----"
                                    }</h1>
                                    {expenseVision && (
                                        <p className={ calculateMensalDifference(revenue, lastMonthData?.revenue).isPositive ? styles['positive'] : styles['negative']}>{calculateMensalDifference(expense, lastMonthData?.expense).result}</p>
                                    )}
                                </div>

                            </div>
                        </div>
                        <div className={styles.graphContainer}>
                            <div className={styles.doughnutDiv}>
                                <p>5 Categorias com mais gastos</p>
                                <Doughnut
                                    data={{
                                        labels: top5ExpenseCategories?.map((item) => item.categoryName),
                                        datasets: [
                                            {
                                                label: 'Gastos',
                                                data: top5ExpenseCategories?.map((item) => item.total),
                                                backgroundColor: top5ExpenseCategories?.map((item) => `#${item.color}33`),
                                                borderColor: top5ExpenseCategories?.map((item) => `#${item.color}66`),
                                                borderWidth: 1
                                            },
                                        ],
                                    }}
                                    options={{ maintainAspectRatio: false, responsive: true, layout: { padding: { bottom: 30 } }, devicePixelRatio: window.devicePixelRatio }}
                                />
                            </div>

                            <div className={styles.doughnutDiv}>
                                <p>5 categorias com mais receita</p>
                                <Doughnut
                                    data={{
                                        labels: top5RevenueCategories?.map((item) => item.categoryName),
                                        datasets: [
                                            {
                                                label: 'Gastos',
                                                data: top5RevenueCategories?.map((item) => item.total),
                                                backgroundColor: top5RevenueCategories?.map((item) => `#${item.color}33`),
                                                borderColor: top5RevenueCategories?.map((item) => `#${item.color}66`),
                                                borderWidth: 1
                                            },
                                        ],
                                    }}
                                    options={{ maintainAspectRatio: false, responsive: true, layout: { padding: { bottom: 30 } }, devicePixelRatio: window.devicePixelRatio }}

                                />
                            </div>

                            <div className={styles.barGraphDiv}>
                                <p>Receitas X Despesas - Ultimos 6 meses</p>
                                <Bar
                                    data={
                                        {
                                            labels: last6Months?.map((item) => translateMonth(item.month)),
                                            datasets: [
                                                { label: "Receita", data: last6Months?.map((item) => item.revenue) },
                                                { label: "Despesa", data: last6Months?.map((item) => item.expense) }
                                            ]
                                        }
                                    }
                                    options={{ layout: { padding: { bottom: 30 } } }}
                                />
                            </div>

                        </div>

                        <div className={styles.latestTransactions}>
                            <h3>Ultimas transações</h3>
                            <DashboardLatests
                                columns={transactionColumns}
                                data={latestTransactions}
                                context="transactions"
                            />
                        </div>
                        <div className={styles.latest}>
                            <h3>Últimos pagamentos</h3>
                            <DashboardLatests
                                columns={paymentColumns}
                                data={latest}
                                context="payments"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Dashboard;