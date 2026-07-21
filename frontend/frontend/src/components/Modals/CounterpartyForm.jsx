import Input from "../Input/Input"

const CounterpartyForm = ( {name, setName, taxId, setTaxId} ) => {
    return (
        <>
            <Input 
            type = "text"
            placeholder={"Nome contraparte"}
            value = {name}
            onChange={(e) => setName(e.target.value)}
            />

            <Input
            type = "number"
            placeholder={"CNPJ/CPF"}
            value = {taxId}
            onChange={(e) => setTaxId(e.target.value)}
            />
        </>
    )
   
}

export default CounterpartyForm;