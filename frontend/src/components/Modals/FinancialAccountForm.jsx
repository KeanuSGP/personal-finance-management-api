import Input from "../Input/Input"

const FinancialAccountForm = ( {name, setName, balance, setBalance} ) => {
    return (
        <>
            <Input 
            type = "text"
            placeholder={"Nome da conta"}
            value = {name}
            onChange={(e) => setName(e.target.value)}
            />

            <Input 
            type = "number"
            placeholder={"Saldo"}
            value = {balance}
            onChange={(e) => setBalance(e.target.value)}
            />
        </>
    )
   
}

export default FinancialAccountForm;