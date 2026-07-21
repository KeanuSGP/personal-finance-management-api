import styles from '../Dashboard/Dashboard.module.css'

import Navbar from "../../components/Navbar/Navbar";

import Table from "../../components/Table/Table"
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
    const [formatedLatestTransactions, setFormatedLatestTransactions] = useState(null)
    const [top5RevenueCategories, setTop5RevenueCategories] = useState(null)
    const [top5ExpenseCategories, setTop5ExpenseCategories] = useState(null)
    const [last6Months, setLast6Months] = useState(null)
    const [loading, setLoading] = useState(true)
    const [vision, setVision] = useState(false);
    const [revenueVision, setRevenueVision] = useState(false);
    const [expenseVision, setExpenseVision] = useState(false);
    const [showAll, setShowAll] = useState(false);

    const handleShowAll = () => {
        setShowAll(!showAll)
    }

    useEffect(() => {
        setVision(showAll);
        setExpenseVision(showAll)
        setRevenueVision(showAll);
    }, [showAll])

    const columns = [
        { header: "PAGO EM", accessor: "moment" },
        { header: "CONTA FINANCEIRA", accessor: "financialAccount" },
        { header: "PARCELA", accessor: "installment" }
    ]

    const transactionColumns = [
        { header: "DOCUMENTO", accessor: "doc" },
        { header: "NÚMERO PARCELA", accessor: "installmentNumber" },
        { header: "EMISSÃO", accessor: "issueDate", type: 'date' },
        { header: "TIPO", accessor: "type" },
        { header: "VALOR", accessor: "amount", type: "money" },
        { header: "VENCIMENTO", accessor: "dueDate", type: 'date' },
    ]

    const handleVision = () => {
        if (vision) return setVision(false)
        setVision(true)
    }

    const formatTransactions = (transactions) => {
        const arr = [];
        transactions.forEach(t => {
            if (t.installments.length > 1) {
                const i = t.installments.reduce((atual, maior) => atual.id > maior.id ? atual : maior)
                const obj = {
                    id: t.id,
                    doc: t.doc,
                    installmentNumber: i.installmentNumber,
                    issueDate: t.issueDate,
                    type: t.type,
                    amount: i.amount,
                    dueDate: i.dueDate
                }
                arr.push(obj)
            } else {
                const obj = {
                    id: t.id,
                    doc: t.doc,
                    installmentNumber: t.installments[0].installmentNumber,
                    issueDate: t.issueDate,
                    type: t.type,
                    amount: t.installments[0].amount,
                    dueDate: t.installments[0].dueDate
                }
                arr.push(obj)
            }

        })
        setFormatedLatestTransactions(arr)
    }

    const formatMoment = (moment) => {
        const split = moment.split('T');
        const dataSplit = split[0].split('-');
        const formatted = `${dataSplit[2]}/${dataSplit[1]}/${dataSplit[0]} ${split[1].slice(0, -1)}`
        return formatted;
    }

    const formatPayments = (payments) => {
        const arr = [];
        payments.forEach(p => {
            const payment = {
                id: p.id,
                moment: formatMoment(p.moment),
                financialAccount: p.financialAccount.name,
                financialAccountId: p.financialAccount.id,
                installment: p.installment
            }
            arr.push(payment)
        })

        setLatest(arr)
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
            formatPayments(data.latestPayments)
            setLatestTransactions(data.latestTransactions)
            formatTransactions(data.latestTransactions)
            setTop5ExpenseCategories(data.top5ExpenseCategories)
            setTop5RevenueCategories(data.top5RevenueCategories)
            setLast6Months(data.last6Months)
        }
    }, [loading])

    const translateMonth = (month) => {
        if (month == undefined || month == null) return;
        switch(month.toLowerCase()) {
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

    if (!loading) {
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
                                        loading ? "R$ -----" : balance !== undefined && balance !== null ? (vision ? `R$ ${balance.toFixed(2)}` : "R$ -----") : "R$ -----"
                                    }</h1>
                                </div>
                                <div className={styles.revenueContainer}>
                                    <div className={styles.titleAndVision}>
                                        <h3 className={styles.revenueTitle}>Receita</h3>
                                        {revenueVision ? (<FaEyeSlash className={styles.iconStyle} onClick={() => setRevenueVision(prev => !prev)} />) : (
                                            <FaEye className={styles.iconStyle} onClick={() => setRevenueVision(prev => !prev)} />
                                        )}
                                    </div>
                                    <h1 className={styles.revenue}>{
                                        loading ? "R$ -----" : revenue !== undefined && revenue !== null ? (revenueVision ? `R$ ${revenue.toFixed(2)}` : "R$ -----") : "R$ -----"
                                    }</h1>
                                </div>
                                <div className={styles.expenseContainer}>
                                    <div className={styles.titleAndVision}>
                                        <h3 className={styles.expenseTitle}>Despesa</h3>
                                        {expenseVision ? (<FaEyeSlash className={styles.iconStyle} onClick={() => setExpenseVision(prev => !prev)} />) : (
                                            <FaEye className={styles.iconStyle} onClick={() => setExpenseVision(prev => !prev)} />
                                        )}
                                    </div>
                                    <h1 className={styles.expense}>{
                                        loading ? "R$ -----" : expense !== undefined && expense !== null ? expenseVision ? `R$ ${expense.toFixed(2)}` : "R$ -----" : "R$ -----"
                                    }</h1>
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
                                        options={{ maintainAspectRatio: false, responsive: true, layout:{padding:{bottom: 30}}, devicePixelRatio: window.devicePixelRatio }}
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
                                        options={{ maintainAspectRatio: false, responsive: true, layout:{padding:{bottom: 30}}, devicePixelRatio: window.devicePixelRatio }}

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
                                        options={{layout:{padding:{bottom: 30}}}}
                                    />
                                </div>

                            </div>

                            <div className={styles.latestTransactions}>
                                <h3>Ultimas transações</h3>
                                <Table
                                    columns={transactionColumns}
                                    data={formatedLatestTransactions}

                                />
                            </div>
                            <div className={styles.latest}>
                                <h3>Últimos pagamentos</h3>
                                {latest ? (
                                    <Table
                                        columns={columns}
                                        data={latest}
                                    />
                                ) : <p>No data found</p>}
                            </div>
                        </div>
                    </div>
                </div>
            </>
        )
    }
}


export default Dashboard;